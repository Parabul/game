package kz.ninestones.game.core;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Optional;
import java.util.stream.IntStream;

public class Policy {

  public static boolean isAllowedMove(State state, int move) {
    checkArgument(move > 0 && move < 10, "Move should be in [1,9]");

    int cell = state.nextMove.boardCell(move);

    return state.cells[cell] != 0;
  }

  public static boolean isGameOver(State state) {

    if (state.score.get(Player.ONE) > 81 || state.score.get(Player.TWO) > 81) {
      return true;
    }

    if (state.score.get(Player.ONE) == 81 && state.score.get(Player.TWO) == 81) {
      return true;
    }

    return IntStream.rangeClosed(1, 9).noneMatch(move -> isAllowedMove(state, move));
  }

  public static Optional<Player> winnerOf(State state) {
    if (!isGameOver(state)) {
      return Optional.empty();
    }

    if (state.score.get(Player.ONE) > 81) {
      return Optional.of(Player.ONE);
    }

    if (state.score.get(Player.TWO) > 81) {
      return Optional.of(Player.TWO);
    }

    // Draw
    if (state.score.get(Player.ONE) == 81 && state.score.get(Player.TWO) == 81) {
      return Optional.of(Player.NONE);
    }

    boolean hasNoMoves = IntStream.rangeClosed(1, 9).noneMatch(move -> isAllowedMove(state, move));

    if (hasNoMoves) {
      return Optional.of(state.nextMove.opponent);
    }

    return Optional.empty();
  }

  public static State makeMove(final State state, int move) {
    checkArgument(!isGameOver(state), "Game over: \n %s\n", state.toString());
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
        newState.score.merge(special.get(), 1, Integer::sum);
      }

      // Rule B
      if (hand == 0 && isReachable(player, currentCell)) {
        if (newState.cells[currentCell] % 2 == 0) {
          newState.score.merge(player, newState.cells[currentCell], Integer::sum);
          newState.cells[currentCell] = 0;
        }

        // Rule D
        if (newState.cells[currentCell] == 3) {
          int possibleSpecialCellMove = moveByCell(currentCell);

          Optional<Integer> opponentSpecialCellMove =
              Optional.ofNullable(state.specialCells.get(player.opponent)).map(Policy::moveByCell);

          boolean eligibleForSpecial =
              !newState.specialCells.containsKey(player)
                  && // Does not have special cell yet;
                  possibleSpecialCellMove != 9
                  && // 9th (most right cell) can not be special;
                  (!opponentSpecialCellMove.isPresent()
                      || !opponentSpecialCellMove
                          .get()
                          .equals(possibleSpecialCellMove)); // Can not mirror opponent's special;

          if (eligibleForSpecial) {
            newState.score.merge(player, 3, Integer::sum);
            newState.cells[currentCell] = 0;
            newState.specialCells.put(player, currentCell);
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

  // Returns Player relevant move by cell index;
  public static int moveByCell(int cell) {
    // 8 -> 1
    // 0 -> 9
    if (cell < 9) {
      return 9 - cell;
    }
    // 9 -> 1
    // 17 -> 1;
    return cell - 8;
  }

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

}
