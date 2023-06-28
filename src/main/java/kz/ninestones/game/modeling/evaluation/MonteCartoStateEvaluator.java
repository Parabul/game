package kz.ninestones.game.modeling.evaluation;

import java.io.File;
import java.io.IOException;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.GameEncoder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

public class MonteCartoStateEvaluator implements StateEvaluator {

  private final MultiLayerNetwork model;

  public MonteCartoStateEvaluator() {
    this("/home/anarbek/projects/ninestones/models/monte_carlo_01.model");
  }

  private MonteCartoStateEvaluator(String modelPath) {
    try {
      this.model = MultiLayerNetwork.load(new File(modelPath), false);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    System.out.println("modelPath: " + modelPath);
  }

  @Override
  public double evaluate(State state, Player player) {
    INDArray prediction = model.output(GameEncoder.toINDArray(state), false);
    double playerOneScore = prediction.getDouble(0, 0);
    double playerTwoScore = prediction.getDouble(0, 1);

    return player.equals(Player.ONE) ? playerOneScore : playerTwoScore;
  }
}
