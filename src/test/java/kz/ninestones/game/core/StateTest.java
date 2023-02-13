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

    assertThat(state.score).hasLength(2);
    assertThat(state.score[Player.ONE.index]).isEqualTo(0);
    assertThat(state.score[Player.TWO.index]).isEqualTo(0);

    assertThat(state.specialCells).hasLength(2);
    assertThat(state.specialCells[Player.ONE.index]).isEqualTo(-1);
    assertThat(state.specialCells[Player.TWO.index]).isEqualTo(-1);

    assertThat(state.nextMove).isEqualTo(Player.ONE);
  }

  @Test
  public void sparseValuesConstructor() {
    State state = new State(ImmutableMap.of(0, 1, 1, 2, 2, 3), new int[]{24, 21}, new int[]{12, -1},
        Player.TWO);

    assertThat(state.cells).hasLength(18);
    assertThat(state.cells).asList()
        .containsExactly(1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0).inOrder();

    assertThat(state.score).hasLength(2);
    assertThat(state.score[Player.ONE.index]).isEqualTo(24);
    assertThat(state.score[Player.TWO.index]).isEqualTo(21);

    assertThat(state.specialCells).hasLength(2);
    assertThat(state.specialCells[Player.ONE.index]).isEqualTo(12);
    assertThat(state.specialCells[Player.TWO.index]).isEqualTo(-1);
    assertThat(state.isSpecial(12)).isEqualTo(Optional.of(Player.ONE));

    assertThat(state.nextMove).isEqualTo(Player.TWO);
  }

  @Test
  public void specialCellEmptyByDefault() {
    State state = new State();

    for (int i = 0; i < 18; i++) {
      assertThat(state.isSpecial(i).isPresent()).isFalse();
    }

    state.specialCells[0] = 12;
    state.specialCells[1] = 5;
    assertThat(state.isSpecial(7)).isEqualTo(Optional.empty());
    assertThat(state.isSpecial(12)).isEqualTo(Optional.of(Player.ONE));
    assertThat(state.isSpecial(5)).isEqualTo(Optional.of(Player.TWO));
  }
}
