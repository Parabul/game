package kz.ninestones.game.core;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import org.junit.Test;

public class StateTest {

  @Test
  public void stateDefaultConstructor() {
    State state = new State();

    assertThat(state.cells).hasLength(18);
    assertThat(state.cells).asList()
        .containsExactly(9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9).inOrder();

    assertThat(state.score.get(Player.ONE)).isEqualTo(0);
    assertThat(state.score.get(Player.TWO)).isEqualTo(0);

    assertThat(state.specialCells.get(Player.ONE)).isNull();
    assertThat(state.specialCells.get(Player.TWO)).isNull();

    assertThat(state.isSpecial(0).isPresent()).isFalse();
    assertThat(state.isSpecial(1).isPresent()).isFalse();

    assertThat(state.nextMove).isEqualTo(Player.ONE);
  }

  @Test
  public void sparseValuesConstructor() {
    State state = new State(ImmutableMap.of(0, 1, 1, 2, 2, 3),
        ImmutableMap.of(Player.ONE, 24, Player.TWO, 21), ImmutableMap.of(Player.ONE, 12),
        Player.TWO);

    assertThat(state.cells).hasLength(18);
    assertThat(state.cells).asList()
        .containsExactly(1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0).inOrder();

    assertThat(state.score.get(Player.ONE)).isEqualTo(24);
    assertThat(state.score.get(Player.TWO)).isEqualTo(21);

    assertThat(state.specialCells.get(Player.ONE)).isEqualTo(12);
    assertThat(state.specialCells.get(Player.TWO)).isNull();
    assertThat(state.isSpecial(12)).isEqualTo(Optional.of(Player.ONE));

    assertThat(state.nextMove).isEqualTo(Player.TWO);
  }

  @Test
  public void specialCellEmptyByDefault() {
    State state = new State();

    for (int i = 0; i < 18; i++) {
      assertThat(state.isSpecial(i).isPresent()).isFalse();
    }

    state.specialCells.put(Player.ONE, 12);
    state.specialCells.put(Player.TWO, 5);
    assertThat(state.isSpecial(7)).isEqualTo(Optional.empty());
    assertThat(state.isSpecial(12)).isEqualTo(Optional.of(Player.ONE));
    assertThat(state.isSpecial(5)).isEqualTo(Optional.of(Player.TWO));
  }
}
