package kz.ninestones.game.learning.training;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.DefaultStateEncoder;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.learning.montecarlo.MonteCarloTreeSearch;
import kz.ninestones.game.learning.montecarlo.StateNode;
import kz.ninestones.game.learning.montecarlo.TreeData;
import kz.ninestones.game.proto.Game;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.BeamTypes;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.TFRecordIO;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.example.Example;

public class MonteCarloTreeSearchExplorationPipeline {
  private static final Logger logger =
      LoggerFactory.getLogger(MonteCarloTreeSearchExplorationPipeline.class);

  public static void main(String[] args) {
    ExplorationPipelineOptions options =
        PipelineOptionsFactory.fromArgs(args).withValidation().as(ExplorationPipelineOptions.class);

    Pipeline pipeline = Pipeline.create(options);

    List<Integer> instances =
        IntStream.range(0, options.getNumSeeds()).boxed().collect(Collectors.toList());

    PCollection<StateNode> stateNodes =
        pipeline
            .apply("Create seeds", Create.of(instances))
            .apply(
                "Generate Random Game States",
                MapElements.into(BeamTypes.stateProtos)
                    .via(i -> GameSimulator.randomState().toProto()))
            .apply("Expand Monte Carlo Search Tree", ParDo.of(new ExpandFn()))
            .apply(
                "Filter visited nodes", Filter.by((stateNode) -> stateNode.getSimulations() >= 2))
            .apply("Enrich Less Visited Nodes", ParDo.of(new EnrichFn()));

    stateNodes
        .apply(
            "[Direct] Encode As TensorFlow Example",
            MapElements.into(BeamTypes.examples)
                .via(MonteCarloTreeSearchExplorationPipeline::encodeDirectFeatures))
        .apply(
            "[Direct] Map To ByteArrays",
            MapElements.into(BeamTypes.byteArrays).via(Example::toByteArray))
        .apply(
            "[Direct] Write TFRecords",
            TFRecordIO.write()
                .withNumShards(options.getNumOutputShards())
                .to(options.getOutputPath()));

    stateNodes
        .apply(
            "Encode As TensorFlow Example",
            MapElements.into(BeamTypes.examples)
                .via(MonteCarloTreeSearchExplorationPipeline::encodeExperimentalFeatures))
        .apply(
            "Map To ByteArrays", MapElements.into(BeamTypes.byteArrays).via(Example::toByteArray))
        .apply(
            "Write TFRecords",
            TFRecordIO.write()
                .withNumShards(options.getNumOutputShards())
                .to(options.getExperimentalOutputPath()));

    PipelineResult res = pipeline.run();
    res.waitUntilFinish();
    logger.info(res.metrics().toString());
  }

  private static Example encodeExperimentalFeatures(final StateNode stateNode) {
    StateEncoder stateEncoder = new DefaultStateEncoder();

    return stateNode.toTFExample(stateEncoder, false);
  }

  private static Example encodeDirectFeatures(final StateNode stateNode) {
    StateEncoder stateEncoder = new DefaultStateEncoder();

    return stateNode.toTFExample(stateEncoder, true);
  }

  private static StateNode merge(Iterable<StateNode> stateNodes) {
    Iterator<StateNode> it = stateNodes.iterator();
    StateNode merged = new StateNode(it.next());
    it.forEachRemaining(merged::merge);

    return merged;
  }

  private PCollection<StateNode> deduplicate(PCollection<StateNode> stateNodes) {
    return stateNodes
        .apply(
            "Key By State Id",
            WithKeys.of(
                new SerializableFunction<StateNode, String>() {
                  @Override
                  public String apply(StateNode s) {
                    return s.getState().getId();
                  }
                }))
        .apply("Combine Duplicates", Combine.perKey(MonteCarloTreeSearchExplorationPipeline::merge))
        .apply("Values", Values.create());
  }

  public interface ExplorationPipelineOptions extends PipelineOptions {
    @Description("Path of the output files")
    @Default.String("/home/anarbek/tmp/direct/tiny.tfrecord")
    String getOutputPath();

    void setOutputPath(String value);

    @Description("Path of the output files")
    @Default.String("/home/anarbek/tmp/experimental/tiny.tfrecord")
    String getExperimentalOutputPath();

    void setExperimentalOutputPath(String value);

    @Description("# shards in the output")
    @Default.Integer(10)
    int getNumOutputShards();

    void setNumOutputShards(int value);

    @Description("# of Monte Carlo Tree Search expanses")
    @Default.Integer(10)
    int getNumExpanses();

    void setNumExpanses(int value);

    @Description("# of seeds")
    @Default.Integer(3)
    int getNumSeeds();

    void setNumSeeds(int value);

    @Description("Minimum # simulation")
    @Default.Integer(3)
    int getTopOff();

    void setTopOff(int value);
  }

  public static class ExpandFn extends DoFn<Game.StateProto, StateNode> {

    private final Counter counter = Metrics.counter(ExpandFn.class, "mcts-nodes-added");

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

      counter.inc(monteCarloTreeSearch.getTreeData().getIndex().size());
      monteCarloTreeSearch.getTreeData().getIndex().values().forEach(out::output);
    }
  }

  public static class EnrichFn extends DoFn<StateNode, StateNode> {

    private final Counter observed = Metrics.counter(ExpandFn.class, "mcts-enrich-observed");

    private final Counter updated = Metrics.counter(ExpandFn.class, "mcts-enrich-updated");

    @ProcessElement
    public void process(
        ProcessContext context, @Element StateNode in, OutputReceiver<StateNode> out) {
      observed.inc();
      int topOff = context.getPipelineOptions().as(ExplorationPipelineOptions.class).getTopOff();

      GameSimulator gameSimulator = GameSimulator.MINIMAX;
      StateNode enriched = new StateNode(in.getState());
      enriched.merge(in);
      if (enriched.getSimulations() < topOff) {
        SimulationResult simulationResult =
            gameSimulator.playOut(enriched.getState(), topOff - enriched.getSimulations());
        enriched.update(simulationResult);
        updated.inc();
      }

      out.output(enriched);
    }
  }
}
