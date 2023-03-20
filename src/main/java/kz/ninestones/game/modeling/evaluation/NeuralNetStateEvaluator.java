package kz.ninestones.game.modeling.evaluation;

import java.io.File;
import java.io.IOException;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.GameEncoder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public class NeuralNetStateEvaluator implements StateEvaluator {

  private final MultiLayerNetwork model;

  public NeuralNetStateEvaluator() throws IOException {
    this("/home/anarbek/projects/ninestones/models/first.model");
  }

  public NeuralNetStateEvaluator(String modelPath) {
    try {
      this.model = MultiLayerNetwork.load(new File(modelPath), false);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    System.out.println("modelPath: "+ modelPath);
  }

  @Override
  public double evaluate(State state, Player player) {
    double score = model.output(GameEncoder.toINDArray(state), false).getDouble(0, 0);

    return player.equals(Player.ONE) ? score : 1 - score;
  }
}
