package kz.ninestones.game.learning.encode;

import com.google.common.primitives.Floats;
import java.util.Arrays;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;

/**
 * Transforms a collection of states into double[36] array and wraps it into INDArray:
 *
 * <p>2 X 9 X 8 (cells) + 2 X 8 (special cell) + 2 X 1 (score) + 1 (nextMove Player)
 */
public class NormalizedStateEncoder implements StateEncoder {
  private static final int NUM_FEATURES = 39;

  public float[] encodeSpecialCells(State state) {
    int playerOneSpecial = state.specialCells.getOrDefault(Player.ONE, -1);
    int playerTwoSpecial = state.specialCells.getOrDefault(Player.TWO, -1);
    return Floats.concat(encodeSpecialCell(playerOneSpecial), encodeSpecialCell(playerTwoSpecial));
  }

  public float[] encodeSpecialCell(int cell) {
    return StateEncoder.oneHot(cell > 8 ? cell - 9 : cell);
  }

  @Override
  public int numFeatures() {
    return NUM_FEATURES;
  }

  public float[] encode(State state) {
    // 2 X 9 (cells) + 2 X 8 (special cell) + 2 X 1 (score) + 1 (nextMove)

    float sum = (float) Arrays.stream(state.cells).sum();

    float[] cells = new float[state.cells.length];

    for (int i = 0; i < state.cells.length; i++) {
      cells[i] = state.cells[i] / sum;
    }

    float[] scores =
        new float[] {state.score.get(Player.ONE) / 82.0f, state.score.get(Player.TWO) / 82.0f};

    float[] specialCells = encodeSpecialCells(state);

    float[] nextMove = new float[] {(float) state.nextMove.ordinal()};
    return Floats.concat(cells, specialCells, scores, nextMove);
  }
}
