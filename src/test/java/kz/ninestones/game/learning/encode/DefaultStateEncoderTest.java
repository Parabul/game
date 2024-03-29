package kz.ninestones.game.learning.encode;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import org.junit.Test;

public class DefaultStateEncoderTest {

  @Test
  public void shouldExtractFeatures() {
    DefaultStateEncoder stateEncoder = new DefaultStateEncoder();

    State state =
        new State(
            ImmutableMap.of(0, 6, 1, 7, 9, 4),
            ImmutableMap.of(Player.ONE, 0, Player.TWO, 82),
            /* specialCells= */ ImmutableMap.of(Player.ONE, 10),
            Player.TWO);

    assertThat(stateEncoder.featuresOf(state, false).keySet())
        .containsExactly("board", "score", "special", "next");

    assertThat(stateEncoder.featuresOf(state, true).keySet()).containsExactly("input");
  }

  @Test
  public void shouldEncodeArbitraryState() {
    DefaultStateEncoder stateEncoder = new DefaultStateEncoder();

    State state =
        new State(
            ImmutableMap.of(0, 6, 1, 7, 9, 4),
            ImmutableMap.of(Player.ONE, 0, Player.TWO, 41),
            /* specialCells= */ ImmutableMap.of(Player.ONE, 10),
            Player.TWO);

    assertThat(DefaultStateEncoder.direct(state).getValueList())
        .containsExactly(
            0.0f,
            0.5f,
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
            0.037037037f,
            0.043209877f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.024691358f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            1.0f);
  }

  @Test
  public void shouldEncodeInitialState() {
    DefaultStateEncoder stateEncoder = new DefaultStateEncoder();

    State state = new State();

    assertThat(DefaultStateEncoder.direct(state).getValueList())
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
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.055555556f,
            0.0f);
  }
}
