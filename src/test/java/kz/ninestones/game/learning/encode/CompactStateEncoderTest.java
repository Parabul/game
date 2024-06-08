package kz.ninestones.game.learning.encode;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import org.junit.Test;

public class CompactStateEncoderTest {

  @Test
  public void shouldEncodeArbitraryState() {
    CompactStateEncoder stateEncoder = new CompactStateEncoder();

    State state =
        new State(
            ImmutableMap.of(0, 6, 1, 7, 9, 4),
            ImmutableMap.of(Player.ONE, 0, Player.TWO, 41),
            /* specialCells= */ ImmutableMap.of(Player.ONE, 10),
            Player.TWO);

    System.out.println(state);

    assertThat(CompactStateEncoder.direct(state).getValueList())
        .containsExactly(
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            1.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.50617284f,
            1.0f,
            0.024691358f,
            0.08024691f,
            0.11111111f,
            0.94017094f)
        .inOrder();
  }

  @Test
  public void shouldEncodeInitialState() {
    CompactStateEncoder stateEncoder = new CompactStateEncoder();

    State state = new State();

    assertThat(CompactStateEncoder.direct(state).getValueList())
        .containsExactly(
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.5f,
            0.5f,
            0.5555556f,
            0.5555556f)
        .inOrder();
  }
}
