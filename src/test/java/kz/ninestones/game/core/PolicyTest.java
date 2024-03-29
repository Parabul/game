package kz.ninestones.game.core;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PolicyTest {

  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  private static ImmutableMap<Player, Integer> scoreOf(int playerOne, int playerTwo) {
    return ImmutableMap.of(Player.ONE, playerOne, Player.TWO, playerTwo);
  }

  private static ImmutableMap<Player, Integer> specialOne(int playerOneSpecial) {
    return ImmutableMap.of(Player.ONE, playerOneSpecial);
  }

  private static ImmutableMap<Player, Integer> specialTwo(int playerTwoSpecial) {
    return ImmutableMap.of(Player.TWO, playerTwoSpecial);
  }

  @Test
  public void cannotMoveZero() {
    State state =
        new State(
            ImmutableMap.of(0, 1, 1, 2, 2, 3),
            ImmutableMap.of(Player.ONE, 24, Player.TWO, 21),
            ImmutableMap.of(Player.ONE, 12),
            Player.ONE);

    assertThat(Policy.isAllowedMove(state, 1)).isFalse();
    assertThat(Policy.isAllowedMove(state, 2)).isFalse();
    assertThat(Policy.isAllowedMove(state, 6)).isFalse();
    assertThat(Policy.isAllowedMove(state, 7)).isTrue();
    assertThat(Policy.isAllowedMove(state, 9)).isTrue();
  }

  @Test
  public void onlyAllowedMoves() {
    State state =
        new State(
            ImmutableMap.of(0, 1),
            ImmutableMap.of(Player.ONE, 24, Player.TWO, 21),
            ImmutableMap.of(Player.ONE, 12),
            Player.ONE);

    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("The move is not allowed!");

    Policy.makeMove(state, 5);
  }

  @Test
  public void firstMoveOne() {
    State state = new State();

    State newState = Policy.makeMove(state, 1);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(ImmutableMap.of(Player.ONE, 0, Player.TWO, 0));
    assertThat(newState.specialCells).isEqualTo(ImmutableMap.of());
    assertThat(newState.cells)
        .asList()
        .containsExactly(10, 10, 10, 10, 10, 10, 10, 10, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9)
        .inOrder();
  }

  @Test
  public void firstMoveTwo() {
    State state = new State();

    State newState = Policy.makeMove(state, 2);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(scoreOf(10, 0));
    assertThat(newState.specialCells).isEqualTo(ImmutableMap.of());
    assertThat(newState.cells)
        .asList()
        .containsExactly(10, 10, 10, 10, 10, 10, 10, 1, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9)
        .inOrder();
  }

  @Test
  public void firstMoveTwoSecondMoveTwo() {
    State state = new State();

    State newState = Policy.makeMove(state, 2);

    newState = Policy.makeMove(newState, 2);

    assertThat(newState.nextMove).isEqualTo(Player.ONE);
    assertThat(newState.score).isEqualTo(scoreOf(10, 10));
    assertThat(newState.specialCells).isEqualTo(ImmutableMap.of());
    assertThat(newState.cells)
        .asList()
        .containsExactly(10, 10, 10, 10, 10, 10, 10, 1, 0, 0, 1, 10, 10, 10, 10, 10, 10, 10)
        .inOrder();
  }

  @Test
  public void specialCellHappyPath() {
    State state =
        new State(
            ImmutableMap.of(0, 1, 1, 1, 9, 2, 10, 9),
            ImmutableMap.of(Player.ONE, 0, Player.TWO, 0),
            ImmutableMap.of(),
            Player.ONE);

    State newState = Policy.makeMove(state, 9);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(scoreOf(3, 0));
    assertThat(newState.specialCells).isEqualTo(specialOne(9));
    assertThat(newState.cells)
        .asList()
        .containsExactly(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0)
        .inOrder();
  }

  @Test
  public void specialCellNotAllowedInMirror() {
    State state =
        new State(
            ImmutableMap.of(0, 1, 1, 1, 9, 2, 10, 9),
            ImmutableMap.of(Player.ONE, 0, Player.TWO, 0),
            ImmutableMap.of(Player.TWO, 8),
            Player.ONE);

    State newState = Policy.makeMove(state, 9);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(ImmutableMap.of(Player.ONE, 0, Player.TWO, 0));
    assertThat(newState.specialCells).isEqualTo(specialTwo(8));
    assertThat(newState.cells)
        .asList()
        .containsExactly(0, 1, 0, 0, 0, 0, 0, 0, 0, 3, 9, 0, 0, 0, 0, 0, 0, 0)
        .inOrder();
  }

  @Test
  public void specialCellIsFinal() {
    State state =
        new State(
            ImmutableMap.of(0, 1, 1, 1, 9, 2, 10, 9),
            ImmutableMap.of(Player.ONE, 0, Player.TWO, 0),
            ImmutableMap.of(Player.ONE, 16),
            Player.ONE);

    State newState = Policy.makeMove(state, 9);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(ImmutableMap.of(Player.ONE, 0, Player.TWO, 0));
    assertThat(newState.specialCells).isEqualTo(specialOne(16));
    assertThat(newState.cells)
        .asList()
        .containsExactly(0, 1, 0, 0, 0, 0, 0, 0, 0, 3, 9, 0, 0, 0, 0, 0, 0, 0)
        .inOrder();
  }

  @Test
  public void specialCellIsNotNine() {
    State state =
        new State(
            ImmutableMap.of(0, 10, 1, 4, 9, 2, 17, 2),
            ImmutableMap.of(Player.ONE, 0, Player.TWO, 0),
            ImmutableMap.of(),
            Player.ONE);

    State newState = Policy.makeMove(state, 9);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(ImmutableMap.of(Player.ONE, 0, Player.TWO, 0));
    assertThat(newState.specialCells).isEqualTo(ImmutableMap.of());
    assertThat(newState.cells)
        .asList()
        .containsExactly(1, 4, 0, 0, 0, 0, 0, 0, 0, 3, 1, 1, 1, 1, 1, 1, 1, 3)
        .inOrder();
  }

  @Test
  public void specialCellIncrementsScore() {
    State state =
        new State(
            ImmutableMap.of(0, 6, 1, 7, 9, 4),
            ImmutableMap.of(Player.ONE, 0, Player.TWO, 0),
            ImmutableMap.of(Player.ONE, 10),
            Player.ONE);

    State newState = Policy.makeMove(state, 8);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(scoreOf(1, 0));
    assertThat(newState.specialCells).isEqualTo(specialOne(10));
    assertThat(newState.cells)
        .asList()
        .containsExactly(7, 1, 0, 0, 0, 0, 0, 0, 0, 5, 0, 1, 1, 1, 0, 0, 0, 0)
        .inOrder();
  }

  @Test
  public void defaultStateIsNotGameOver() {
    State state = new State();

    assertThat(Policy.isGameOver(state)).isFalse();
    assertThat(Policy.winnerOf(state).isPresent()).isFalse();
  }

  @Test
  public void noMovesGameOverTwo() {
    State state =
        new State(
            ImmutableMap.of(0, 7),
            ImmutableMap.of(Player.ONE, 0, Player.TWO, 0),
            ImmutableMap.of(Player.ONE, 10),
            Player.TWO);

    assertThat(Policy.isGameOver(state)).isTrue();
    assertThat(Policy.winnerOf(state).get()).isEqualTo(Player.ONE);
  }

  @Test
  public void noMovesGameOverOne() {
    State state =
        new State(
            ImmutableMap.of(11, 7),
            ImmutableMap.of(Player.ONE, 0, Player.TWO, 0),
            ImmutableMap.of(Player.ONE, 10),
            Player.ONE);

    assertThat(Policy.isGameOver(state)).isTrue();
    assertThat(Policy.winnerOf(state).get()).isEqualTo(Player.TWO);
  }

  @Test
  public void grawGameOver() {
    State state =
        new State(
            ImmutableMap.of(0, 0),
            ImmutableMap.of(Player.ONE, 81, Player.TWO, 81),
            ImmutableMap.of(Player.ONE, 10),
            Player.TWO);

    assertThat(Policy.isGameOver(state)).isTrue();
    assertThat(Policy.winnerOf(state).isPresent()).isTrue();
    assertThat(Policy.winnerOf(state).get()).isEqualTo(Player.NONE);
  }
}
