package kz.ninestones.game.core;

import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

// TODO: Consider short/byte instead of int;
public class State implements Serializable {

  // 0-8  player one
  // 9-17 player two
  final int[] cells;

  final int[] score;

  // -1 special cell not set, possible range: [9-17]
  int playerOneSpecialCell;

  // -1 special cell not set, possible range: [0-8]
  int playerTwoSpecialCell;

  Player nextMove;

  State() {
    cells = new int[18];
    Arrays.fill(cells, 9);

    score = new int[]{0, 0};

    playerOneSpecialCell = -1;
    playerTwoSpecialCell = -1;
    nextMove = Player.ONE;
  }

  State(State original) {
    cells = Arrays.copyOf(original.cells, original.cells.length);
    score = Arrays.copyOf(original.score, original.score.length);
    playerOneSpecialCell = original.playerOneSpecialCell;
    playerTwoSpecialCell = original.playerTwoSpecialCell;
    nextMove = original.nextMove;
  }

  Optional<Player> isSpecial(int cell) {
    if (playerOneSpecialCell == cell) {
      return Optional.of(Player.ONE);
    }

    if (playerTwoSpecialCell == cell) {
      return Optional.of(Player.TWO);
    }

    return Optional.empty();
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("-------------------------------\n");

    sb.append(score[0]);
    sb.append(":");
    sb.append(score[1]);
    sb.append("\n");

    sb.append("|");
    for (int i = 0; i < 9; i++) {
      sb.append(
          Strings.padStart(playerTwoSpecialCell == i ? cells[i] + "*" : cells[i] + "", 4, ' '));
      sb.append("|");
    }
    sb.append("\n");

    sb.append("|");
    for (int i = 9; i < 18; i++) {
      sb.append(
          Strings.padStart(playerOneSpecialCell == i ? cells[i] + "*" : cells[i] + "", 4, ' '));
      sb.append("|");
    }
    sb.append("\n");

    sb.append("Next: ");

    sb.append(nextMove);

    sb.append("\n");

    return sb.toString();
  }
}
