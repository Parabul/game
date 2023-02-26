package kz.ninestones.game.learning;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.model.MinMaxModel;
import kz.ninestones.game.model.ScoreDiffStateEvaluator;
import kz.ninestones.game.model.StateEvaluator;
import kz.ninestones.game.simulation.RecordedGame;
import kz.ninestones.game.simulation.SimulateAndRecordGame;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

public class TrainingSet {

  public static DataSetIterator getTrainingData(int samples, int batchSize) {
    System.out.println("training set");

    List<INDArray> inputs = new ArrayList<>();
    List<INDArray> outputs = new ArrayList<>();

    StateEvaluator stateEvaluator = new ScoreDiffStateEvaluator();
    MinMaxModel model = new MinMaxModel(stateEvaluator);

    SimulateAndRecordGame simulateAndRecordGame = new SimulateAndRecordGame(
        ImmutableMap.of(Player.ONE, model, Player.TWO, model));

    for (int i = 0; i < samples; i++) {
      RecordedGame record = simulateAndRecordGame.record();
      int steps = record.getSteps().size();

      double[] results = new double[steps];
      Arrays.fill(results, record.getWinner().equals(Player.ONE) ? 1.0 : 0.0);

      INDArray output = Nd4j.create(results, steps, 1);

      INDArray input = StateEncoder.encode(record.getSteps());

      inputs.add(input);
      outputs.add(output);
    }

    INDArray input = Nd4j.vstack(inputs);
    INDArray output = Nd4j.vstack(outputs);

    System.out.println("input shape: " + Arrays.toString(input.shape()));
    System.out.println("output shape: " + Arrays.toString(output.shape()));

    DataSet dataSet = new DataSet(input, output);
    List<DataSet> listDs = dataSet.asList();
    Collections.shuffle(listDs);
    return new ListDataSetIterator(listDs, batchSize);
  }
}
