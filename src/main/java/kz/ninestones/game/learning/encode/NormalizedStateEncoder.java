package kz.ninestones.game.learning.encode;

import com.google.common.primitives.Doubles;
import java.util.Arrays;
import java.util.stream.Stream;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;

/**
 * Transforms a collection of states into double[36] array and wraps it into INDArray:
 *
 * <p>2 X 9 X 8 (cells) + 2 X 8 (special cell) + 2 X 1 (score) + 1 (nextMove Player)
 */
public class NormalizedStateEncoder implements StateEncoder {
  private static final int NUM_FEATURES = 36;

  public double[] encodeSpecialCells(State state) {
    int playerOneSpecial = state.specialCells.getOrDefault(Player.ONE, -1);
    int playerTwoSpecial = state.specialCells.getOrDefault(Player.TWO, -1);

    double[] specialCells =
        Stream.of(playerOneSpecial, playerTwoSpecial)
            .map(specialCell -> specialCell > 8 ? specialCell - 9 : specialCell)
            .map(StateEncoder::oneHot)
            .flatMapToDouble(Arrays::stream)
            .toArray();
    return specialCells;
  }

  @Override
  public int numFeatures() {
    return NUM_FEATURES;
  }

  public double[] encode(State state) {
    // 2 X 9 (cells) + 2 X 8 (special cell) + 2 X 1 (score) + 1 (nextMove)

    double sum = Arrays.stream(state.cells).sum();

    double[] cells = Arrays.stream(state.cells).mapToDouble(cell -> cell / sum).toArray();

    double[] scores =
        state.score.values().stream()
            .mapToDouble(score -> score > 81 ? 1.0 : 1.0 * score / 82.0)
            .toArray();

    double[] specialCells = encodeSpecialCells(state);

    return Doubles.concat(cells, specialCells, scores);
  }
}
