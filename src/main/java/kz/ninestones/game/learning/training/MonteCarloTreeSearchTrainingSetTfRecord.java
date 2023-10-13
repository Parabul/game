package kz.ninestones.game.learning.training;

import com.google.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import java.util.*;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.NormalizedStateEncoder;
import kz.ninestones.game.persistence.ProtoFiles;
import kz.ninestones.game.proto.Game;
import kz.ninestones.game.utils.TensorFlowUtils;
import org.apache.beam.runners.flink.FlinkPipelineOptions;
import org.apache.beam.runners.flink.FlinkRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.TFRecordIO;
import org.apache.beam.sdk.transforms.FlatMapElements;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.Reshuffle;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.tensorflow.example.Example;
import org.tensorflow.example.Features;

public class MonteCarloTreeSearchTrainingSetTfRecord {

  private static final String output = "/home/anarbek/tmp/flink/minimax_training.tfrecord";

  public static void main(String[] args) {
    FlinkPipelineOptions options = FlinkPipelineOptions.defaults();
    options.setFlinkMaster("localhost:8081");

    Pipeline p = Pipeline.create(options);

    FlinkRunner flinkRunner = FlinkRunner.fromOptions(options);

    TypeDescriptor<byte[]> byteArray = new TypeDescriptor<byte[]>() {};
    TypeDescriptor<Example> example = new TypeDescriptor<Example>() {};

    p.apply(
            FileIO.match()
                .filepattern(MonteCarloTreeSearchTrainingSetGenerator.MINIMAX_TRAINING_DAT))
        .apply(FileIO.readMatches())
        .apply(
            FlatMapElements.into(byteArray)
                .via(
                    (FileIO.ReadableFile f) -> {
                      try {
                        return ProtoFiles.read(f.open());
                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                    }))
        .apply(
            MapElements.into(example)
                .via(
                    bytes -> {
                      try {
                        return toExample(Game.GameSample.newBuilder().mergeFrom(bytes).build());
                      } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e);
                      }
                    }))
        .apply(Reshuffle.viaRandomKey())
        .apply(MapElements.into(byteArray).via(e -> e.toByteArray()))
        .apply(TFRecordIO.write().to(output));

    flinkRunner.run(p).waitUntilFinish();
  }

  public static Example toExample(Game.GameSample sample) {
    NormalizedStateEncoder stateEncoder = new NormalizedStateEncoder();

    float[] input = stateEncoder.encode(new State(sample.getState()));

    long simulations =
        sample.getObservedWinnersMap().values().stream().mapToLong(Long::valueOf).sum();

    float[] output =
        new float[] {
          1.0f * sample.getObservedWinnersMap().getOrDefault(Player.ONE.name(), 0) / simulations,
          1.0f * sample.getObservedWinnersMap().getOrDefault(Player.TWO.name(), 0) / simulations,
          1.0f * sample.getObservedWinnersMap().getOrDefault(Player.NONE.name(), 0) / simulations
        };

    return Example.newBuilder()
        .setFeatures(
            Features.newBuilder()
                .putFeature("input", TensorFlowUtils.floatList(input))
                .putFeature("output", TensorFlowUtils.floatList(output)))
        .build();
  }
}
