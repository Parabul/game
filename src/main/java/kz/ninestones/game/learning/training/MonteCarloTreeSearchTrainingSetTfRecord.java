package kz.ninestones.game.learning.training;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Doubles;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import java.util.*;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.NormalizedStateEncoder;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.persistence.ProtoFiles;
import kz.ninestones.game.proto.Game;
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
import org.tensorflow.example.Feature;
import org.tensorflow.example.Features;
import org.tensorflow.example.FloatList;

public class MonteCarloTreeSearchTrainingSetTfRecord {

  private final StateEncoder stateEncoder;

  public MonteCarloTreeSearchTrainingSetTfRecord(StateEncoder stateEncoder) {
    this.stateEncoder = stateEncoder;
  }

  private static <T extends Number> Feature floatList(Collection<T> nums) {
    FloatList.Builder floatList = FloatList.newBuilder();

    nums.stream().forEach(number -> floatList.addValue(number.floatValue()));

    return Feature.newBuilder().setFloatList(floatList).build();
  }

  public static void main(String[] args) {

    MonteCarloTreeSearchTrainingSetTfRecord monteCarloTreeSearchTrainingSetTfRecord =
        new MonteCarloTreeSearchTrainingSetTfRecord(new NormalizedStateEncoder());

    // [START create_pipeline_options]
    // Create a PipelineOptions object. This object lets us set various execution
    // options for our pipeline, such as the runner you wish to use. This example
    // will run with the DirectRunner by default, based on the class path configured
    // in its dependencies.
    FlinkPipelineOptions options = FlinkPipelineOptions.defaults();
    options.setFlinkMaster("localhost:8081");

    // [END create_pipeline_options]

    // In order to run your pipeline, you need to make following runner specific changes:
    //
    // CHANGE 1/3: Select a Beam runner, such as BlockingDataflowRunner
    // or FlinkRunner.
    // CHANGE 2/3: Specify runner-required options.
    // For BlockingDataflowRunner, set project and temp location as follows:
    //   DataflowPipelineOptions dataflowOptions = options.as(DataflowPipelineOptions.class);
    //   dataflowOptions.setRunner(BlockingDataflowRunner.class);
    //   dataflowOptions.setProject("SET_YOUR_PROJECT_ID_HERE");
    //   dataflowOptions.setTempLocation("gs://SET_YOUR_BUCKET_NAME_HERE/AND_TEMP_DIRECTORY");
    // For FlinkRunner, set the runner as follows. See {@code FlinkPipelineOptions}
    // for more details.
    //   options.as(FlinkPipelineOptions.class)
    //      .setRunner(FlinkRunner.class);

    // [START create_pipeline]
    // Create the Pipeline object with the options we defined above
    Pipeline p = Pipeline.create(options);

    FlinkRunner flinkRunner = FlinkRunner.fromOptions(options);
    // [END create_pipeline]

    // Concept #1: Apply a root transform to the pipeline; in this case, TextIO.Read to read a set
    // of input text files. TextIO.Read returns a PCollection where each element is one line from
    // the input text (a set of Shakespeare's texts).

    // This example reads from a public dataset containing the text of King Lear.
    // [START read_input]

    //    TypeDescriptor<byte[]> byteArray = new TypeDescriptor<byte[]>() {};

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

        //    p.apply(Create.of(examples))
        .apply(Reshuffle.viaRandomKey())
        .apply(MapElements.into(byteArray).via(e -> e.toByteArray()))
        .apply(TFRecordIO.write().to("/home/anarbek/tmp/flink/minimax_training.tfrecord"));

    flinkRunner.run(p).waitUntilFinish();
  }

  public static Example toExample(Game.GameSample sample) {
    NormalizedStateEncoder stateEncoder = new NormalizedStateEncoder();

    List<Double> input = Doubles.asList(stateEncoder.encode(new State(sample.getState())));

    long simulations =
        sample.getObservedWinnersMap().values().stream().mapToLong(Long::valueOf).sum();

    List<Double> output =
        ImmutableList.of(
            1.0 * sample.getObservedWinnersMap().getOrDefault(Player.ONE.name(), 0L) / simulations,
            1.0 * sample.getObservedWinnersMap().getOrDefault(Player.TWO.name(), 0L) / simulations,
            1.0
                * sample.getObservedWinnersMap().getOrDefault(Player.NONE.name(), 0L)
                / simulations);

    return Example.newBuilder()
        .setFeatures(
            Features.newBuilder()
                .putFeature("input", floatList(input))
                .putFeature("output", floatList(output)))
        .build();
  }

  public List<Example> read(String infile) {

    List<Game.GameSample> samples = ProtoFiles.read(infile, Game.GameSample.getDefaultInstance());

    List<Example> examples = new ArrayList<>(samples.size());

    boolean first = true;

    for (Game.GameSample sample : samples) {

      List<Double> input = Doubles.asList(stateEncoder.encode(new State(sample.getState())));

      long simulations =
          sample.getObservedWinnersMap().values().stream().mapToLong(Long::valueOf).sum();

      List<Double> output =
          ImmutableList.of(
              1.0
                  * sample.getObservedWinnersMap().getOrDefault(Player.ONE.name(), 0L)
                  / simulations,
              1.0
                  * sample.getObservedWinnersMap().getOrDefault(Player.TWO.name(), 0L)
                  / simulations,
              1.0
                  * sample.getObservedWinnersMap().getOrDefault(Player.NONE.name(), 0L)
                  / simulations);

      examples.add(
          Example.newBuilder()
              .setFeatures(
                  Features.newBuilder()
                      .putFeature("input", floatList(input))
                      .putFeature("output", floatList(output)))
              .build());

      if (first) {
        first = false;
        System.out.println(examples.get(0));
      }
    }

    return examples;
  }
}
