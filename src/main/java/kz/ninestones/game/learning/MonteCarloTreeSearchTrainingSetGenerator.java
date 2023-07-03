package kz.ninestones.game.learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.learning.encode.DirectStateEncoder;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.modeling.strategy.MonteCarloTreeNode;
import kz.ninestones.game.modeling.strategy.MonteCarloTreeSearch;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

public class MonteCarloTreeSearchTrainingSetGenerator {

  private final StateEncoder stateEncoder = new DirectStateEncoder();

  public DataSetIterator generateTrainingData(int samples, int batchSize) {
    DataSet dataSet = generateDataSet(samples);
    List<DataSet> listDs = dataSet.asList();
    Collections.shuffle(listDs);
    return new ListDataSetIterator<>(listDs, batchSize);
  }

  public DataSet generateDataSet(int samples) {
    MonteCarloTreeSearch monteCarloTreeSearch = new MonteCarloTreeSearch();

    for (int i = 0; i < samples; i++) {
      System.out.println("i: " + i);
      monteCarloTreeSearch.expand();
    }
    List<MonteCarloTreeNode> traversal = monteCarloTreeSearch.traverse();

    List<INDArray> inputs = new ArrayList<>(traversal.size());
    List<INDArray> outputs = new ArrayList<>(traversal.size());

    for (MonteCarloTreeNode node : traversal) {

      long simulations = node.getSimulations();

      double[] wins =
          new double[] {
            1.0 * node.getObservedWinners().get(Player.ONE) / simulations,
            1.0 * node.getObservedWinners().get(Player.TWO) / simulations,
            1.0 * node.getObservedWinners().get(Player.NONE) / simulations,
          };

      INDArray output = Nd4j.create(wins, 1, 3);

      INDArray input = stateEncoder.toINDArray(node.getState());

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
