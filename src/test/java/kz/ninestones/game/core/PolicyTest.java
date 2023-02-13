package kz.ninestones.game.core;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PolicyTest {

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();


  @Test
  public void cannotMoveZero() {
    State state = new State(ImmutableMap.of(0, 1, 1, 2, 2, 3), new int[]{24, 21}, new int[]{12, -1},
        Player.ONE);

    assertThat(Policy.isAllowedMove(state, 1)).isFalse();
    assertThat(Policy.isAllowedMove(state, 2)).isFalse();
    assertThat(Policy.isAllowedMove(state, 6)).isFalse();
    assertThat(Policy.isAllowedMove(state, 7)).isTrue();
    assertThat(Policy.isAllowedMove(state, 9)).isTrue();
  }

  @Test
  public void onlyAllowedMoves() {
    State state = new State(ImmutableMap.of(0, 1), new int[]{24, 21}, new int[]{12, -1},
        Player.ONE);

    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("The move is not allowed!");

    Policy.makeMove(state, 5);
  }

  @Test
  public void firstMoveOne() {
    State state = new State();

    State newState = Policy.makeMove( state, 1);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(new int[]{0, 0});
    assertThat(newState.specialCells).isEqualTo(new int[]{-1, -1});
    assertThat(newState.cells).asList()
        .containsExactly(10, 10, 10, 10, 10, 10, 10, 10, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9).inOrder();

  }


  @Test
  public void firstMoveTwo() {
    State state = new State();

    State newState = Policy.makeMove(state, 2);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(new int[]{10, 0});
    assertThat(newState.specialCells).isEqualTo(new int[]{-1, -1});
    assertThat(newState.cells).asList()
        .containsExactly(10, 10, 10, 10, 10, 10, 10, 1, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9).inOrder();
  }

  @Test
  public void firstMoveTwoSecondMoveTwo() {
    State state = new State();

    State newState = Policy.makeMove( state, 2);

    newState = Policy.makeMove(newState, 2);

    assertThat(newState.nextMove).isEqualTo(Player.ONE);
    assertThat(newState.score).isEqualTo(new int[]{10, 10});
    assertThat(newState.specialCells).isEqualTo(new int[]{-1, -1});
    assertThat(newState.cells).asList()
        .containsExactly(10, 10, 10, 10, 10, 10, 10, 1, 0, 0, 1, 10, 10, 10, 10, 10, 10, 10)
        .inOrder();
  }


  @Test
  public void specialCellHappyPath() {
    State state = new State(ImmutableMap.of(0, 1, 1, 1, 9, 2, 10, 9), new int[]{0, 0},
        new int[]{-1, -1}, Player.ONE);

    State newState = Policy.makeMove(state, 9);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(new int[]{3, 0});
    assertThat(newState.specialCells).isEqualTo(new int[]{9, -1});
    assertThat(newState.cells).asList()
        .containsExactly(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0).inOrder();
  }

  @Test
  public void specialCellNotAllowedInMirror() {
    State state = new State(ImmutableMap.of(0, 1, 1, 1, 9, 2, 10, 9), new int[]{0, 0},
        new int[]{-1, 8}, Player.ONE);

    State newState = Policy.makeMove(state, 9);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(new int[]{0, 0});
    assertThat(newState.specialCells).isEqualTo(new int[]{-1, 8});
    assertThat(newState.cells).asList()
        .containsExactly(0, 1, 0, 0, 0, 0, 0, 0, 0, 3, 9, 0, 0, 0, 0, 0, 0, 0).inOrder();
  }

  @Test
  public void specialCellIsFinal() {
    State state = new State(ImmutableMap.of(0, 1, 1, 1, 9, 2, 10, 9), new int[]{0, 0},
        new int[]{16, -1}, Player.ONE);

    State newState = Policy.makeMove(state, 9);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(new int[]{0, 0});
    assertThat(newState.specialCells).isEqualTo(new int[]{16, -1});
    assertThat(newState.cells).asList()
        .containsExactly(0, 1, 0, 0, 0, 0, 0, 0, 0, 3, 9, 0, 0, 0, 0, 0, 0, 0).inOrder();
  }

  @Test
  public void specialCellIsNotNine() {
    State state = new State(ImmutableMap.of(0, 10, 1, 4, 9, 2, 17, 2), new int[]{0, 0},
        new int[]{-1, -1}, Player.ONE);

    State newState = Policy.makeMove(state, 9);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(new int[]{0, 0});
    assertThat(newState.specialCells).isEqualTo(new int[]{-1, -1});
    assertThat(newState.cells).asList()
        .containsExactly(1, 4, 0, 0, 0, 0, 0, 0, 0, 3, 1, 1, 1, 1, 1, 1, 1, 3).inOrder();
  }

  @Test
  public void specialCellIncrementsScore() {
    State state = new State(ImmutableMap.of(0, 6, 1, 7, 9, 4), new int[]{0, 0},
        new int[]{10, -1}, Player.ONE);

    State newState = Policy.makeMove(state, 8);

    assertThat(newState.nextMove).isEqualTo(Player.TWO);
    assertThat(newState.score).isEqualTo(new int[]{1, 0});
    assertThat(newState.specialCells).isEqualTo(new int[]{10, -1});
    assertThat(newState.cells).asList()
        .containsExactly(7, 1, 0, 0, 0, 0, 0, 0, 0, 5, 0, 1, 1, 1, 0, 0, 0, 0).inOrder();
  }

  @Test
  public void noMovesGameOverTwo() {
    State state = new State(ImmutableMap.of(0, 7), new int[]{0, 0},
        new int[]{10, -1}, Player.TWO);


    Optional<Player> gameOver = Policy.isGameOver(state);

    assertThat(gameOver.isPresent()).isTrue();
    assertThat(gameOver.get()).isEqualTo(Player.ONE);
  }

  @Test
  public void noMovesGameOverOne() {
    State state = new State(ImmutableMap.of(11, 7), new int[]{0, 0},
        new int[]{10, -1}, Player.ONE);


    Optional<Player> gameOver = Policy.isGameOver(state);

    assertThat(gameOver.isPresent()).isTrue();
    assertThat(gameOver.get()).isEqualTo(Player.TWO);
  }
}
