package kz.ninestones.game.learning.encode;

import com.google.common.annotations.VisibleForTesting;
import kz.ninestones.game.core.State;

public interface StateEncoder {

  @VisibleForTesting
  static float[] oneHot(int index) {
    float[] encoding = new float[9];
    if (index >= 0 && index < 9) encoding[index] = 1.0f;
    return encoding;
  }

  float[] encode(State state);

  @VisibleForTesting
  float[] encodeSpecialCells(State state);

  int numFeatures();
}
