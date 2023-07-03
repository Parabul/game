package kz.ninestones.game.core;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import kz.ninestones.game.proto.Game;
import org.junit.Test;

public class StateTest {

  @Test
  public void stateDefaultConstructor() {
    State state = new State();

    assertThat(state.cells).hasLength(18);
    assertThat(state.cells)
        .asList()
        .containsExactly(9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9)
        .inOrder();

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
    State state =
        new State(
            ImmutableMap.of(0, 1, 1, 2, 2, 3),
            ImmutableMap.of(Player.ONE, 24, Player.TWO, 21),
            ImmutableMap.of(Player.ONE, 12),
            Player.TWO);

    assertThat(state.cells).hasLength(18);
    assertThat(state.cells)
        .asList()
        .containsExactly(1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        .inOrder();

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

  @Test
  public void fromProtoConstructor() {
    State state =
        new State(
            ImmutableMap.of(0, 1, 1, 2, 2, 3),
            ImmutableMap.of(Player.ONE, 24, Player.TWO, 21),
            ImmutableMap.of(Player.ONE, 12),
            Player.TWO);

    Game.StateProto stateProto =
        Game.StateProto.newBuilder()
            .addAllCells(ImmutableList.of(1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
            .setNextMove(Game.PlayerProto.ONE)
            .putScore(Player.ONE.name(), 24)
            .putScore(Player.TWO.name(), 21)
            .putSpecialCells(Player.ONE.name(), 12)
            .setNextMove(Game.PlayerProto.TWO)
            .build();

    State stateFromProto = new State(stateProto);

    assertThat(stateFromProto).isEqualTo(state);
    assertThat(state.toProto()).isEqualTo(stateProto);

    assertThat(new State(state.toProto())).isEqualTo(state);
  }

  @Test
  public void validatesProto() {
    IllegalArgumentException exception1 =
        assertThrows(
            IllegalArgumentException.class, () -> new State(Game.StateProto.getDefaultInstance()));
    assertThat(exception1).hasMessageThat().contains("Cells should have length 18, but 0");

    IllegalArgumentException exception2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new State(
                    Game.StateProto.newBuilder()
                        .addAllCells(
                            ImmutableList.of(1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
                        .build()));
    assertThat(exception2).hasMessageThat().contains("Scores should have length 2, but 0");

    IllegalArgumentException exception3 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new State(
                    Game.StateProto.newBuilder()
                        .addAllCells(
                            ImmutableList.of(1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
                        .putScore(Player.ONE.name(), 24)
                        .putScore(Player.TWO.name(), 21)
                        .build()));
    assertThat(exception3).hasMessageThat().contains("Player is not set");

    assertThat(
            new State(
                    Game.StateProto.newBuilder()
                        .addAllCells(
                            ImmutableList.of(1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
                        .putScore(Player.ONE.name(), 24)
                        .putScore(Player.TWO.name(), 21)
                        .setNextMove(Game.PlayerProto.TWO)
                        .build())
                .specialCells)
        .isEmpty();
  }
}
