package kz.ninestones.game.core;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.hash.Funnel;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class State implements Serializable {

  // 0-8  player one
  // 9-17 player two
  public final int[] cells;
  public final EnumMap<Player, Integer> score;
  // -1 special cell not set
  // For ONE possible range: [9-17]
  // For TWO possible range: [0-8]
  public final EnumMap<Player, Integer> specialCells;
  public Player nextMove;

  public State() {
    cells = new int[18];
    Arrays.fill(cells, 9);

    score = new EnumMap(Player.class);
    score.put(Player.ONE, 0);
    score.put(Player.TWO, 0);
    specialCells = new EnumMap(Player.class);

    nextMove = Player.ONE;
  }

  public State(State original) {
    cells = Arrays.copyOf(original.cells, original.cells.length);
    score = new EnumMap(original.score);
    specialCells = new EnumMap(original.specialCells);
    nextMove = original.nextMove;
  }

  // Sparse init
  public State(Map<Integer, Integer> nonZeroValues, Map<Player, Integer> score,
      Map<Player, Integer> specialCells, Player nextMove) {
    checkArgument(nonZeroValues.keySet().stream().allMatch(key -> key >= 0 && key < 18),
        "Value key out of range");

    if (specialCells.containsValue(Player.ONE)) {
      checkArgument((specialCells.get(Player.ONE) > 8 && specialCells.get(Player.ONE) < 17),
          "Special one out of range");
    }

    if (specialCells.containsValue(Player.TWO)) {
      checkArgument(specialCells.containsValue(Player.TWO) && (specialCells.get(Player.TWO) > 0
          && specialCells.get(Player.TWO) < 9), "Special two out of range");
    }

    this.cells = new int[18];
    Arrays.fill(this.cells, 0);

    for (Map.Entry<Integer, Integer> cellValue : nonZeroValues.entrySet()) {
      this.cells[cellValue.getKey()] = cellValue.getValue();
    }

    this.score = new EnumMap(score);
    if (specialCells.isEmpty()) {
      this.specialCells = new EnumMap(Player.class);
    } else {
      this.specialCells = new EnumMap(specialCells);
    }

    this.nextMove = nextMove;
  }

  Optional<Player> isSpecial(int cell) {
    if (!specialCells.containsValue(cell)) {
      return Optional.empty();
    }

    if (specialCells.getOrDefault(Player.ONE, -1).equals(cell)) {
      return Optional.of(Player.ONE);
    }

    if (specialCells.getOrDefault(Player.TWO, -1).equals(cell)) {
      return Optional.of(Player.TWO);
    }

    throw new IllegalStateException("Unknown isSpecial for " + cell);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("-------------------------------\n");

    sb.append(score.get(Player.ONE));
    sb.append(":");
    sb.append(score.get(Player.TWO));
    sb.append("\n");

    sb.append("|");
    for (int i = 0; i < 9; i++) {
      sb.append(
          Strings.padStart(isSpecial(i).isPresent() ? cells[i] + "*" : cells[i] + "", 4, ' '));
      sb.append("|");
    }
    sb.append("\n");

    sb.append("|");
    for (int i = 9; i < 18; i++) {
      sb.append(
          Strings.padStart(isSpecial(i).isPresent() ? cells[i] + "*" : cells[i] + "", 4, ' '));
      sb.append("|");
    }
    sb.append("\n");

    sb.append("Next: ");

    sb.append(nextMove);

    sb.append("\n");

    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    State state = (State) o;
    return Objects.equal(cells, state.cells) && Objects.equal(score, state.score) && Objects.equal(
        specialCells, state.specialCells) && nextMove == state.nextMove;
  }

  @Override
  public int hashCode() {
    return getHashCode().asInt();
  }

  public HashCode getHashCode() {
    return Hashing.sha384().newHasher().putObject(this, StateFunnel.INSTANCE).hash();
  }

  public enum StateFunnel implements Funnel<State> {
    INSTANCE;

    public void funnel(State from, PrimitiveSink into) {
      Arrays.stream(from.cells).forEachOrdered(into::putInt);
      into.putInt(from.score.get(Player.ONE));
      into.putInt(from.score.get(Player.TWO));

      into.putInt(from.specialCells.getOrDefault(Player.ONE, -1));
      into.putInt(from.specialCells.getOrDefault(Player.TWO, -1));

      into.putString(from.nextMove.name(), StandardCharsets.UTF_8);
    }
  }

}
