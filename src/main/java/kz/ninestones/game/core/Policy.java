package kz.ninestones.game.core;

import java.util.Optional;

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

  public static boolean isAllowedMove(Player player, State state, int move) {
    if (state.nextMove != player) {
      return false;
    }

    int cell = player.boardCell(move);

    return state.cells[cell] != 0;
  }

  public static State makeMove(Player player, State state, int move) {
    if (!isAllowedMove(player, state, move)) {
      throw new IllegalArgumentException("Not allowed!");
    }

    State newState = new State(state);

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

        if (newState.cells[currentCell] == 3) {
          // TODO new special?
        }
      }

      currentCell = nextCell(currentCell);
    }

    // Pass move to the next
    newState.nextMove = state.nextMove.next;

    return newState;
  }

  private static boolean isReachable(Player player, int cell) {
    if (Player.ONE.equals(player)) {
      return cell > 8;
    }
    return cell < 9;
  }

}
