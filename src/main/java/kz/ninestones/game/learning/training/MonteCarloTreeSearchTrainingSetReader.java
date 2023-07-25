package kz.ninestones.game.learning.training;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.persistence.ProtoFiles;
import kz.ninestones.game.proto.Game;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

public class MonteCarloTreeSearchTrainingSetReader {

  private final StateEncoder stateEncoder;

  public MonteCarloTreeSearchTrainingSetReader(StateEncoder stateEncoder) {
    this.stateEncoder = stateEncoder;
  }

  public static DataSetIterator iterator(DataSet dataSet, int batchSize) {
    List<DataSet> listDs = dataSet.asList();
    Collections.shuffle(listDs);
    return new ListDataSetIterator<>(listDs, batchSize);
  }

  public DataSet read(String file) {

    List<Game.GameSample> samples = ProtoFiles.read(file, Game.GameSample.getDefaultInstance());

    List<INDArray> inputs = new ArrayList<>(samples.size());
    List<INDArray> outputs = new ArrayList<>(samples.size());

    for (Game.GameSample sample : samples) {

      long simulations =
          sample.getObservedWinnersMap().values().stream().mapToLong(Long::valueOf).sum();

      double[] wins =
          new double[] {
            1.0 * sample.getObservedWinnersMap().getOrDefault(Player.ONE.name(), 0L) / simulations,
            1.0 * sample.getObservedWinnersMap().getOrDefault(Player.TWO.name(), 0L) / simulations,
            1.0 * sample.getObservedWinnersMap().getOrDefault(Player.NONE.name(), 0L) / simulations,
          };

      INDArray output = Nd4j.create(wins, 1, 3);

      INDArray input = stateEncoder.toINDArray(new State(sample.getState()));

      inputs.add(input);
      outputs.add(output);
    }

    INDArray input = Nd4j.vstack(inputs);
    INDArray output = Nd4j.vstack(outputs);

    System.out.println("input shape: " + Arrays.toString(input.shape()));
    System.out.println("output shape: " + Arrays.toString(output.shape()));

    return new DataSet(input, output);
  }
}
