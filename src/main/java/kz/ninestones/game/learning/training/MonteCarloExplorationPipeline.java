package kz.ninestones.game.learning.training;

import com.google.common.collect.Lists;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kz.ninestones.game.learning.encode.NormalizedStateEncoder;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.learning.montecarlo.TreeNode;
import kz.ninestones.game.learning.montecarlo.TreeSearch;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.utils.BeamTypes;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TFRecordIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.tensorflow.example.Example;

public class MonteCarloExplorationPipeline {

  private static final String OUTPUT = "/home/anarbek/tmp/test/random_training.tfrecord";
  //  private static final String OUTPUT = "/mnt/nfs/tmp/explore/50_100_minimax_training.tfrecord";

  private static final int NUM_INSTANCES = 6;

  private static final int NUM_EXPANSES = 5;

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

    TypeDescriptor<TreeNode> treeNodes = new TypeDescriptor<TreeNode>() {};

    p.apply(Create.of(instances))
        .apply(MapElements.into(treeNodes).via(MonteCarloExplorationPipeline::initialExpansion))
        .apply(
            Combine.globally(
                (left, right) -> {
                  left.mergeFrom(right);
                  return left;
                }))
        .apply(
            FlatMapElements.into(BeamTypes.examples)
                .via(MonteCarloExplorationPipeline::secondExpansion))
        //        .apply(Reshuffle.viaRandomKey())
        .apply(MapElements.into(BeamTypes.byteArrays).via(e -> e.toByteArray()))
        //        .apply(
        //            FileIO.<byte[]>write()
        //                .via(TFRecordIO.sink())
        //                .withNumShards(10)
        ////                .withTempDirectory("/home/anarbek/tmp")
        //                .to(OUTPUT));
        .apply(TFRecordIO.write().withNumShards(4).to(OUTPUT));

    p.run().waitUntilFinish();
    //    flinkRunner.run(p).waitUntilFinish();
  }

  public static TreeNode initialExpansion(int instance) {
    System.out.println("Instance " + instance);
    TreeSearch monteCarloTreeSearch = new TreeSearch();
    StateEncoder stateEncoder = new NormalizedStateEncoder();

    for (int i = 0; i < NUM_EXPANSES; i++) {
      monteCarloTreeSearch.expand();
    }

    System.out.println("Init instance expanded " + instance);
    System.out.println("simulations " + monteCarloTreeSearch.getRoot().getSimulations());
    return monteCarloTreeSearch.getRoot();

    //
  }

  public static List<Example> secondExpansion(TreeNode treeNode) {
    System.out.println("SecondExpansion ");
    System.out.println("simulations " + treeNode.getSimulations());
    TreeSearch monteCarloTreeSearch =
        new TreeSearch(GameSimulator.RANDOM_VS_RANDOM, treeNode);
    StateEncoder stateEncoder = new NormalizedStateEncoder();

    for (int i = 0; i < NUM_EXPANSES; i++) {
      monteCarloTreeSearch.expand();
    }

    System.out.println("SecondExpansion");
    return Lists.transform(monteCarloTreeSearch.traverse(), node -> node.toTFExample(stateEncoder));
  }
}
