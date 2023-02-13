package kz.ninestones.game.core;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Optional;
import java.util.stream.IntStream;

public class Policy {

  private static int nextCell(int cell) {
    if (cell == 0) {
      return 9;
    }

    if (cell < 9) {
      return cell - 1;
    }

    if (cell == 17) {
      return 8;
    }

    return cell + 1;
  }

  public static boolean isAllowedMove(State state, int move) {
    checkArgument(move > 0 && move < 10, "Move should be in [1,9]");

    int cell = state.nextMove.boardCell(move);

    return state.cells[cell] != 0;
  }

  public static Optional<Player> isGameOver(State state) {

    if(state.score[Player.ONE.index] > 81){
      return Optional.of(Player.ONE);
    }

    if(state.score[Player.TWO.index] > 81){
      return Optional.of(Player.TWO);
    }

    boolean hasMoves = IntStream.rangeClosed(1, 9).anyMatch(move -> isAllowedMove(state, move));

    if(!hasMoves){
      return Optional.of(state.nextMove.opponent);
    }

    return Optional.empty();
  }

  // Returns Player relevant move by cell index;
  private static int moveByCell(int cell) {
    // 8 -> 1
    // 0 -> 9
    if (cell < 9) {
      return 9 - cell;
    }
    // 9 -> 1
    // 17 -> 1;
    return cell - 8;
  }

  public static State makeMove(final State state, int move) {
    checkArgument(!isGameOver(state).isPresent(), "Game over!");
    checkArgument(isAllowedMove(state, move), "The move is not allowed!");

    State newState = new State(state);

    Player player = state.nextMove;

    int cell = player.boardCell(move);

    int hand = newState.cells[cell];

    newState.cells[cell] = 0;

    // Rule A
    int currentCell = hand == 1 ? nextCell(cell) : cell;

    while (hand > 0) {
      hand--;

      Optional<Player> special = newState.isSpecial(currentCell);

      // Rule C
      if (!special.isPresent()) {
        newState.cells[currentCell]++;
      } else {
        newState.score[special.get().index]++;
      }

      // Rule B
      if (hand == 0 && isReachable(player, currentCell)) {
        if (newState.cells[currentCell] % 2 == 0) {
          newState.score[player.index] += newState.cells[currentCell];
          newState.cells[currentCell] = 0;
        }

        // Rule D
        if (newState.cells[currentCell] == 3) {
          int possibleSpecialCellMove = moveByCell(currentCell);

          Optional<Integer> opponentSpecialCellMove =
              state.specialCells[player.opponent.index] == -1 ? Optional.empty()
                  : Optional.of(moveByCell(state.specialCells[player.opponent.index]));

          boolean eligibleForSpecial =
              newState.specialCells[player.index] == -1 && // Does not have special cell yet;
                  possibleSpecialCellMove != 9 && // 9th (most right cell) can not be special;
                  (!opponentSpecialCellMove.isPresent() || !opponentSpecialCellMove.get()
                      .equals(possibleSpecialCellMove)); // Can not mirror opponent's special;

          if(eligibleForSpecial){
            newState.score[player.index] += 3;
            newState.cells[currentCell] = 0;
            newState.specialCells[player.index] = currentCell;
          }
        }
      }

      currentCell = nextCell(currentCell);
    }

    // Pass move to the next
    newState.nextMove = state.nextMove.opponent;

    return newState;
  }

  private static boolean isReachable(Player player, int cell) {
    if (Player.ONE.equals(player)) {
      return cell > 8;
    }
    return cell < 9;
  }

}
