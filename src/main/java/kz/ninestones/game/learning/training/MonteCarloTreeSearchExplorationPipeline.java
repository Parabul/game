package kz.ninestones.game.learning.training;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.NormalizedStateEncoder;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.learning.montecarlo.MonteCarloTreeSearch;
import kz.ninestones.game.learning.montecarlo.StateNode;
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

    Pipeline p = Pipeline.create(options);

    run(p, options);

    p.run().waitUntilFinish();
  }

  public static void run(Pipeline pipeline, ExplorationPipelineOptions options) {
    List<Integer> instances =
        IntStream.range(0, options.getNumSeeds()).boxed().collect(Collectors.toList());

    pipeline
        .apply("Create Seed instances ", Create.of(instances))
        .apply(
            "Generate States",
            MapElements.into(BeamTypes.stateProtos).via(i -> GameSimulator.randomState().toProto()))
        .apply("Expand", ParDo.of(new ExpandFn()))
        .apply(
            "Enrich",
            MapElements.into(BeamTypes.stateNodes)
                .via(MonteCarloTreeSearchExplorationPipeline::enrich))
        .apply(
            "Encode",
            MapElements.into(BeamTypes.examples)
                .via(MonteCarloTreeSearchExplorationPipeline::encode))
        .apply(MapElements.into(BeamTypes.byteArrays).via(Example::toByteArray))
        .apply(
            TFRecordIO.write()
                .withNumShards(options.getNumOutputShards())
                .to(options.getOutputPath()));
  }

  private static Example encode(final StateNode stateNode) {
    StateEncoder stateEncoder = new NormalizedStateEncoder();

    return stateNode.toTFExample(stateEncoder);
  }

  private static StateNode enrich(StateNode stateNode) {
    GameSimulator gameSimulator = GameSimulator.MINIMAX;
    StateNode enriched = new StateNode(new State(stateNode.getState()));
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
              new MonteCarloTreeSearch(
                      GameSimulator.MINIMAX, new MonteCarloTreeSearch.TreeData(), new State(root));

      for (int i = 0; i < numExpanses; i++) {
        monteCarloTreeSearch.expand();
      }

      monteCarloTreeSearch.getTreeData().getIndex().values().forEach(out::output);
    }
  }
}