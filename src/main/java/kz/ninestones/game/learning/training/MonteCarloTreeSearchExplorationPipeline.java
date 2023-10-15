package kz.ninestones.game.learning.training;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kz.ninestones.game.learning.encode.NormalizedStateEncoder;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.learning.montecarlo.MonteCarloTreeSearch;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.utils.BeamTypes;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TFRecordIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.tensorflow.example.Example;

public class MonteCarloTreeSearchExplorationPipeline {

  private static final String OUTPUT = "/home/anarbek/tmp/min_max/training_4_100.tfrecord";
  //  private static final String OUTPUT = "/mnt/nfs/tmp/explore/50_100_minimax_training.tfrecord";

  private static final int NUM_INSTANCES = 4;

  private static final int NUM_EXPANSES = 100;

  public static void main(String[] args) {
    //    FlinkPipelineOptions options = FlinkPipelineOptions.defaults();
    //    options.setFlinkMaster("192.168.0.51:8081");
    //    options.setParallelism(8);
    //    options.setJobName("MonteCarloExplorationPipeline");
    //    options.setFasterCopy(true);

    PipelineOptions options = PipelineOptionsFactory.create();

    Pipeline p = Pipeline.create(options);

    //    FlinkRunner flinkRunner = FlinkRunner.fromOptions(options);

    List<Integer> instances =
        IntStream.range(0, NUM_INSTANCES).boxed().collect(Collectors.toList());

    TypeDescriptor<MonteCarloTreeSearch.TreeData> treeDatas =
        new TypeDescriptor<MonteCarloTreeSearch.TreeData>() {};

    p.apply(Create.of(instances))
        .apply(
            MapElements.into(treeDatas)
                .via(MonteCarloTreeSearchExplorationPipeline::initialExpansion))
        .apply(
            Combine.globally(
                (left, right) -> {
                  left.merge(right);
                  return left;
                }))
        .apply(
            "Second Expansions",
            FlatMapElements.into(BeamTypes.examples)
                .via(MonteCarloTreeSearchExplorationPipeline::secondExpansion))
        //        .apply(Reshuffle.viaRandomKey())
        .apply(MapElements.into(BeamTypes.byteArrays).via(e -> e.toByteArray()))
        //        .apply(
        //            FileIO.<byte[]>write()
        //                .via(TFRecordIO.sink())
        //                .withNumShards(10)
        ////                .withTempDirectory("/home/anarbek/tmp")
        //                .to(OUTPUT));
        .apply(TFRecordIO.write().withNumShards(10).to(OUTPUT));

    p.run().waitUntilFinish();
    //    flinkRunner.run(p).waitUntilFinish();
  }

  public static MonteCarloTreeSearch.TreeData initialExpansion(int instance) {
    System.out.println("Instance " + instance);
    MonteCarloTreeSearch monteCarloTreeSearch =
        new MonteCarloTreeSearch(GameSimulator.MINIMAX);

    for (int i = 0; i < NUM_EXPANSES; i++) {
      monteCarloTreeSearch.expand();
    }

    System.out.println("Init instance expanded " + instance);
    System.out.println(
        "simulations "
            + monteCarloTreeSearch
                .getTreeData()
                .getIndex()
                .get(MonteCarloTreeSearch.ROOT_ID)
                .getSimulations());
    return monteCarloTreeSearch.getTreeData();
  }

  public static List<Example> secondExpansion(final MonteCarloTreeSearch.TreeData treeData) {
    MonteCarloTreeSearch.TreeData copy = new MonteCarloTreeSearch.TreeData(treeData);

    MonteCarloTreeSearch monteCarloTreeSearch =
        new MonteCarloTreeSearch(GameSimulator.MINIMAX, copy);
    StateEncoder stateEncoder = new NormalizedStateEncoder();

    //    for (int i = 0; i < NUM_EXPANSES; i++) {
    monteCarloTreeSearch.expand();
    //    }

    System.out.println("SecondExpansion");
    System.out.println(
        "simulations "
            + monteCarloTreeSearch
                .getTreeData()
                .getIndex()
                .get(MonteCarloTreeSearch.ROOT_ID)
                .getSimulations());

    return monteCarloTreeSearch.getTreeData().getIndex().values().stream()
        .map(node -> node.toTFExample(stateEncoder))
        .collect(Collectors.toList());
  }
}
