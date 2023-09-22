package kz.ninestones.game.learning.training;

import com.google.common.collect.Lists;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kz.ninestones.game.learning.encode.NormalizedStateEncoder;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.learning.montecarlo.TreeSearch;
import kz.ninestones.game.simulation.montecarlo.MinMaxMonteCarloPlayOutSimulator;
import kz.ninestones.game.utils.BeamTypes;
import org.apache.beam.runners.flink.FlinkPipelineOptions;
import org.apache.beam.runners.flink.FlinkRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TFRecordIO;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.FlatMapElements;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.Reshuffle;
import org.tensorflow.example.Example;

public class MonteCarloExplorationPipeline {

  //  private static final String OUTPUT = "/home/anarbek/tmp/explore/minimax_training.tfrecord";
  private static final String OUTPUT = "/mnt/nfs/tmp/explore/50_100_minimax_training.tfrecord";

  private static final int NUM_INSTANCES = 100;

  private static final int NUM_EXPANSES = 10;

  public static void main(String[] args) {
    FlinkPipelineOptions options = FlinkPipelineOptions.defaults();
    options.setFlinkMaster("192.168.0.51:8081");
    options.setParallelism(8);
    options.setJobName("MonteCarloExplorationPipeline");
    options.setFasterCopy(true);
//    options.setFilesToStage(ImmutableList.of("/home/anarbek/projects/game/target/game-1.0-jar-with-dependencies.jar"));

    Pipeline p = Pipeline.create(options);

    FlinkRunner flinkRunner = FlinkRunner.fromOptions(options);

    List<Integer> instances =
        IntStream.range(0, NUM_INSTANCES).boxed().collect(Collectors.toList());

    p.apply(Create.of(instances))
        .apply(
            FlatMapElements.into(BeamTypes.examples)
                .via(MonteCarloExplorationPipeline::initialExpansion))
        .apply(Reshuffle.viaRandomKey())
        .apply(MapElements.into(BeamTypes.byteArrays).via(e -> e.toByteArray()))
//        .apply(
//            FileIO.<byte[]>write()
//                .via(TFRecordIO.sink())
//                .withNumShards(10)
////                .withTempDirectory("/home/anarbek/tmp")
//                .to(OUTPUT));
            .apply(TFRecordIO.write().withNumShards(20).to(OUTPUT));

    flinkRunner.run(p).waitUntilFinish();
  }

  public static List<Example> initialExpansion(int instance) {
    TreeSearch monteCarloTreeSearch = new TreeSearch(new MinMaxMonteCarloPlayOutSimulator());
    StateEncoder stateEncoder = new NormalizedStateEncoder();

    for (int i = 0; i < NUM_EXPANSES; i++) {
      monteCarloTreeSearch.expand();
    }

    return Lists.transform(monteCarloTreeSearch.traverse(), node -> node.toTFExample(stateEncoder));
  }
}
