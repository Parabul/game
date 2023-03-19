package kz.ninestones.game.learning;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.RecordedGame;
import kz.ninestones.game.learning.encode.GameEncoder;
import kz.ninestones.game.modeling.evaluation.ScoreDiffStateEvaluator;
import kz.ninestones.game.modeling.evaluation.StateEvaluator;
import kz.ninestones.game.modeling.strategy.MatrixMinMaxStrategy;
import kz.ninestones.game.simulation.GameSimulator;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

public class TrainingSetGenerator {

  private final GameSimulator gameSimulator;

  public TrainingSetGenerator() {
    StateEvaluator stateEvaluator = new ScoreDiffStateEvaluator();
    MatrixMinMaxStrategy model = new MatrixMinMaxStrategy(stateEvaluator);

    this.gameSimulator = new GameSimulator(
        ImmutableMap.of(Player.ONE, model, Player.TWO, model));
  }

  public DataSetIterator generateTrainingData(int samples, int batchSize) {
    List<INDArray> inputs = new ArrayList<>(samples);
    List<INDArray> outputs = new ArrayList<>(samples);

    for (int i = 0; i < samples; i++) {
      RecordedGame record = gameSimulator.simulate();
      int steps = record.getSteps().size();

      double outcome = Player.ONE.equals(record.getWinner()) ? 1.0 : 0.0;

      INDArray output = Nd4j.valueArrayOf(steps, 1, outcome);

      INDArray input = GameEncoder.toINDArray(record.getSteps());

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
    return new ListDataSetIterator<>(listDs, batchSize);
  }
}
