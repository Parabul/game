package kz.ninestones.game.model;

import java.io.File;
import java.io.IOException;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.StateEncoder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public class NeuralNetStateEvaluator implements StateEvaluator {

  private final MultiLayerNetwork model;

  public NeuralNetStateEvaluator() throws IOException {
    this.model = MultiLayerNetwork.load(
        new File("/home/anarbek/projects/ninestones/models/first.model"), false);
  }

  public NeuralNetStateEvaluator(String modelPath) throws IOException {
    this.model = MultiLayerNetwork.load(
        new File(modelPath), false);
  }

  @Override
  public double evaluate(State state, Player player) {
    double score = model.output(StateEncoder.leanEncode(state), false).getDouble(0, 0);

    return player.equals(Player.ONE) ? score : 1 - score;
  }
}
