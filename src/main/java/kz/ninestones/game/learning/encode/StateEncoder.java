package kz.ninestones.game.learning.encode;

import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.stream.IntStream;
import kz.ninestones.game.core.State;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public interface StateEncoder {

  @VisibleForTesting
  static double[] oneHot(int index) {
    return IntStream.range(0, 8).mapToDouble(i -> i == index ? 1.0 : 0.0).toArray();
  }

  default INDArray toINDArray(List<State> states) {
    return Nd4j.create(encode(states));
  }

  default INDArray toINDArray(State state) {
    return Nd4j.create(encode(state), 1, numFeatures());
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
