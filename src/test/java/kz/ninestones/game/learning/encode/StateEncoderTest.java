package kz.ninestones.game.learning.encode;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class StateEncoderTest {
  @Test
  public void oneHotShouldReturnArray() {
    assertThat(StateEncoder.oneHot(0)).isEqualTo(new float[] {1, 0, 0, 0, 0, 0, 0, 0, 0});
    assertThat(StateEncoder.oneHot(9)).isEqualTo(new float[] {0, 0, 0, 0, 0, 0, 0, 0, 0});
    assertThat(StateEncoder.oneHot(8)).isEqualTo(new float[] {0, 0, 0, 0, 0, 0, 0, 0, 1});
  }
}
