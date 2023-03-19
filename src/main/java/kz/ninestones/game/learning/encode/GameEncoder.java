package kz.ninestones.game.learning.encode;

import com.google.common.primitives.Doubles;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Transforms a collection of states into double[36] array and wraps it into INDArray:
 * <p>
 * 2 X 9 X 8 (cells) + 2 X 8 (special cell) + 2 X 1 (score) + 1 (nextMove Player)
 */
public class GameEncoder {

  public static INDArray toINDArray(List<State> states) {
    return Nd4j.create(encode(states));
  }

  public static INDArray toINDArray(State state) {
    return Nd4j.create(encode(state), 1, 36);
  }

  public static double[][] encode(List<State> states) {
    double[][] encodedState = new double[states.size()][36];

    for (int i = 0; i < states.size(); i++) {
      encodedState[i] = encode(states.get(i));
    }

    return encodedState;
  }

  public static double[] encode(State state) {
    // 2 X 9 (cells) + 2 X 8 (special cell) + 2 X 1 (score) + 1 (nextMove)

    double sum = Arrays.stream(state.cells).sum();

    double[] cells = Arrays.stream(state.cells).mapToDouble(cell -> cell / sum).toArray();

    double[] scores = state.score.values().stream()
        .mapToDouble(score -> score > 81 ? 1.0 : 1.0 * score / 82.0).toArray();

    int playerOneSpecial = state.specialCells.getOrDefault(Player.ONE, -1);
    int playerTwoSpecial = state.specialCells.getOrDefault(Player.TWO, -1);

    double[] specialCells = Stream.of(playerOneSpecial, playerTwoSpecial)
        .map(specialCell -> specialCell > 8 ? specialCell - 9 : specialCell)
        .map(GameEncoder::oneHot).flatMapToDouble(Arrays::stream).toArray();

    return Doubles.concat(cells, specialCells, scores);
  }

  private static double[] oneHot(int index) {
    return IntStream.range(0, 8).mapToDouble(i -> i == index ? 1.0 : 0.0).toArray();
  }
}
