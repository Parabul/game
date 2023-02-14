package kz.ninestones.game.core;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class State implements Serializable {

  // 0-8  player one
  // 9-17 player two
  final int[] cells;

  final int[] score;

  // -1 special cell not set
  // For ONE possible range: [9-17]
  // For TWO possible range: [0-8]
  final int[] specialCells;

  public Player nextMove;

  public State() {
    cells = new int[18];
    Arrays.fill(cells, 9);

    score = new int[]{0, 0};
    specialCells = new int[]{-1, -1};

    nextMove = Player.ONE;
  }

  public State(State original) {
    cells = Arrays.copyOf(original.cells, original.cells.length);
    score = Arrays.copyOf(original.score, original.score.length);
    specialCells = Arrays.copyOf(original.specialCells, original.specialCells.length);
    nextMove = original.nextMove;
  }


  // Sparse init
  public State(Map<Integer, Integer> nonZeroValues, int[] score, int[] specialCells, Player nextMove) {
    checkArgument(score.length == 2, "Score length != 2");
    checkArgument(specialCells.length == 2, "SpecialCells length != 2");
    checkArgument(nonZeroValues.keySet().stream().allMatch(key -> key >= 0 && key < 18),
        "Value key out of range");

    checkArgument(specialCells[0] == -1 || (specialCells[0] > 8 && specialCells[0] < 17),
        "Special one out of range");

    checkArgument(specialCells[1] == -1 || (specialCells[1] > 0 && specialCells[1] < 9),
        "Special two out of range");

    this.cells = new int[18];
    Arrays.fill(this.cells, 0);

    for (Map.Entry<Integer, Integer> cellValue : nonZeroValues.entrySet()) {
      this.cells[cellValue.getKey()] = cellValue.getValue();
    }

    this.score = Arrays.copyOf(score, score.length);

    this.specialCells = Arrays.copyOf(specialCells, specialCells.length);

    this.nextMove = nextMove;
  }

  Optional<Player> isSpecial(int cell) {
    if (specialCells[0] == cell) {
      return Optional.of(Player.ONE);
    }

    if (specialCells[1] == cell) {
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
      sb.append(Strings.padStart(specialCells[1] == i ? cells[i] + "*" : cells[i] + "", 4, ' '));
      sb.append("|");
    }
    sb.append("\n");

    sb.append("|");
    for (int i = 9; i < 18; i++) {
      sb.append(Strings.padStart(specialCells[0] == i ? cells[i] + "*" : cells[i] + "", 4, ' '));
      sb.append("|");
    }
    sb.append("\n");

    sb.append("Next: ");

    sb.append(nextMove);

    sb.append("\n");

    return sb.toString();
  }
}
