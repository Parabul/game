package kz.ninestones.game.learning.encode;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import org.junit.Test;

public class NormalizedStateEncoderTest {

  @Test
  public void shouldEncodeSpecialCells() {
    NormalizedStateEncoder normalizedStateEncoder = new NormalizedStateEncoder();

    State state =
        new State(
            ImmutableMap.of(0, 6, 1, 7, 9, 4),
            ImmutableMap.of(Player.ONE, 0, Player.TWO, 0),
            /* specialCells= */ ImmutableMap.of(Player.ONE, 10),
            Player.ONE);

    assertThat(normalizedStateEncoder.encodeSpecialCells(state))
        .isEqualTo(new float[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

    assertThat(normalizedStateEncoder.encode(state))
        .usingTolerance(0.001)
        .containsExactly(
            new float[] {
              0.3529f, 0.4117f, 0, 0, 0, 0, 0, 0, 0, 0.2352f, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.0f, 0, 0,
              0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
            });
  }
}
