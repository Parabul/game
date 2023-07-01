package kz.ninestones.game.learning.encode;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableMap;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import org.junit.Test;

public class DirectStateEncoderTest {

  @Test
  public void shouldEncodeDefaultState() {
    StateEncoder stateEncoder = new DirectStateEncoder();

    State state = new State();

    assertThat(stateEncoder.encodeSpecialCells(state)).isEqualTo(new double[] {0, 0});
    assertThat(stateEncoder.encode(state))
        .isEqualTo(
            new double[] {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 0, 0, 0, 0});
  }

  @Test
  public void shouldEncodeArbitraryState() {
    StateEncoder stateEncoder = new DirectStateEncoder();

    State state =
        new State(
            ImmutableMap.of(0, 6, 1, 7, 9, 4, 15, 10),
            ImmutableMap.of(Player.ONE, 35, Player.TWO, 60),
            ImmutableMap.of(Player.ONE, 10, Player.TWO, 3),
            Player.TWO);

    assertThat(stateEncoder.encodeSpecialCells(state)).isEqualTo(new double[] {2, 6});
    assertThat(stateEncoder.encode(state))
        .isEqualTo(
            new double[] {
              6, 7, 0, 0, 0, 0, 0, 0, 0, 4.0, 0, 0, 0, 0, 0, 10, 0, 0, 2.0, 6.0, 35, 60, 1
            });
  }
}
