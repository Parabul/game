package kz.ninestones.game.learning.encode;

import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.stream.IntStream;
import kz.ninestones.game.core.State;

public interface StateEncoder {

  @VisibleForTesting
  static double[] oneHot(int index) {
    return IntStream.range(0, 9).mapToDouble(i -> i == index ? 1.0 : 0.0).toArray();
  }

  default double[][] encode(List<State> states) {
    double[][] encodedState = new double[states.size()][36];

    for (int i = 0; i < states.size(); i++) {
      encodedState[i] = encode(states.get(i));
    }

    return encodedState;
  }

  double[] encode(State state);

  @VisibleForTesting
  double[] encodeSpecialCells(State state);

  int numFeatures();
}