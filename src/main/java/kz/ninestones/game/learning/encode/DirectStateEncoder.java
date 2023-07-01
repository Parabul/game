package kz.ninestones.game.learning.encode;

import com.google.common.primitives.Doubles;
import java.util.Arrays;
import java.util.stream.Stream;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;

/**
 * Transforms a collection of states into double[36] array and wraps it into INDArray:
 *
 * <p>2 X 9 X 8 (cells) + 2 X 8 (special cell) + 2 X 1 (score) + 1 (nextMove Player)
 */
public class DirectStateEncoder implements StateEncoder {

  private static final int NUM_FEATURES = 23;

  public double[] encodeSpecialCells(State state) {
    return Stream.of(Player.ONE, Player.TWO)
        .map(player -> state.specialCells.getOrDefault(player, 0))
        .mapToDouble(cell -> cell == 0 ? 0 : Policy.moveByCell(cell))
        .toArray();
  }

  @Override
  public int numFeatures() {
    return NUM_FEATURES;
  }

  public double[] encode(State state) {

    double[] cells = Arrays.stream(state.cells).asDoubleStream().toArray();

    double[] scores = state.score.values().stream().mapToDouble(Double::valueOf).toArray();

    double[] specialCells = encodeSpecialCells(state);

    double[] nextMove = new double[] {state.nextMove.ordinal()};

    return Doubles.concat(cells, specialCells, scores, nextMove);
  }
}
