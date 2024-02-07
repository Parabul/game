package kz.ninestones.game.learning.training;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.NormalizedStateEncoder;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.learning.montecarlo.MonteCarloTreeSearch;
import kz.ninestones.game.learning.montecarlo.StateNode;
import kz.ninestones.game.learning.montecarlo.TreeData;
import kz.ninestones.game.proto.Game;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.BeamTypes;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TFRecordIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.tensorflow.example.Example;

public class MonteCarloTreeSearchExplorationPipeline {

  public static void main(String[] args) {
    ExplorationPipelineOptions options =
        PipelineOptionsFactory.fromArgs(args).withValidation().as(ExplorationPipelineOptions.class);

    Pipeline pipeline = Pipeline.create(options);

    List<Integer> instances =
        IntStream.range(0, options.getNumSeeds()).boxed().collect(Collectors.toList());

    pipeline
        .apply("Create seeds", Create.of(instances))
        .apply(
            "Generate Random Game States",
            MapElements.into(BeamTypes.stateProtos).via(i -> GameSimulator.randomState().toProto()))
        .apply("Expand Monte Carlo Search Tree", ParDo.of(new ExpandFn()))
        .apply(
            "Enrich Less Visited Nodes",
            MapElements.into(BeamTypes.stateNodes)
                .via(MonteCarloTreeSearchExplorationPipeline::enrich))
        .apply(
            "Encode As TensorFlow Example",
            MapElements.into(BeamTypes.examples)
                .via(MonteCarloTreeSearchExplorationPipeline::encode))
        .apply(
            "Map To ByteArrays", MapElements.into(BeamTypes.byteArrays).via(Example::toByteArray))
        .apply(
            "Write TFRecords",
            TFRecordIO.write()
                .withNumShards(options.getNumOutputShards())
                .to(options.getOutputPath()));

    pipeline.run().waitUntilFinish();
  }

  private static Example encode(final StateNode stateNode) {
    StateEncoder stateEncoder = new NormalizedStateEncoder();

    return stateNode.toTFExample(stateEncoder);
  }

  private static StateNode enrich(StateNode stateNode) {
    GameSimulator gameSimulator = GameSimulator.MINIMAX;
    StateNode enriched = new StateNode(stateNode.getState());
    enriched.merge(stateNode);
    if (enriched.getSimulations() < 10) {
      SimulationResult simulationResult =
          gameSimulator.playOut(enriched.getState(), 10 - enriched.getSimulations());
      enriched.update(simulationResult);
    }

    return enriched;
  }

  public interface ExplorationPipelineOptions extends PipelineOptions {
    @Description("Path of the output files")
    @Default.String("/home/anarbek/tmp/min_max/training_4_100.tfrecord")
    String getOutputPath();

    void setOutputPath(String value);

    @Description("# shards in the output")
    @Default.Integer(4)
    int getNumOutputShards();

    void setNumOutputShards(int value);

    @Description("# of Monte Carlo Tree Search expanses")
    @Default.Integer(4)
    int getNumExpanses();

    void setNumExpanses(int value);

    @Description("# of seeds")
    @Default.Integer(5)
    int getNumSeeds();

    void setNumSeeds(int value);
  }

  public static class ExpandFn extends DoFn<Game.StateProto, StateNode> {
    @ProcessElement
    public void process(
        ProcessContext context, @Element Game.StateProto root, OutputReceiver<StateNode> out) {

      int numExpanses =
          context.getPipelineOptions().as(ExplorationPipelineOptions.class).getNumExpanses();

      MonteCarloTreeSearch monteCarloTreeSearch =
          new MonteCarloTreeSearch(GameSimulator.MINIMAX, new TreeData(), new State(root));

      for (int i = 0; i < numExpanses; i++) {
        monteCarloTreeSearch.expand();
      }

      monteCarloTreeSearch.getTreeData().getIndex().values().forEach(out::output);
    }
  }
}
