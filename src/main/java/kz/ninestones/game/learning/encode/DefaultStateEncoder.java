package kz.ninestones.game.learning.encode;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.utils.TensorFlowUtils;
import org.tensorflow.example.Feature;
import org.tensorflow.example.FloatList;

public class DefaultStateEncoder implements StateEncoder {
  private static Feature ofSpecialCells(State state) {
    return TensorFlowUtils.intList(
        Stream.of(Player.ONE, Player.TWO)
            .mapToInt(player -> state.getSpecialCells().getOrDefault(player, -1) + 1)
            .toArray());
  }

  private static Feature ofScore(State state) {
    return TensorFlowUtils.doubleList(
        Stream.of(Player.ONE, Player.TWO)
            .mapToDouble(player -> state.getScore().getOrDefault(player, 0) / 82.0)
            .toArray());
  }

  private static Feature ofNextMove(State state) {
    return TensorFlowUtils.intList(state.getNextMove().ordinal());
  }

  private static Feature ofBoard(State state) {
    return TensorFlowUtils.doubleList(
        Arrays.stream(state.getCells()).mapToDouble(cell -> 1.0 * cell / 162).toArray());
  }

  @Override
  public Map<String, Feature> featuresOf(State state, boolean direct) {
    if (direct) {
      return ImmutableMap.of(
          StateEncoder.INPUT, Feature.newBuilder().setFloatList(direct(state)).build());
    } else {
      return ImmutableMap.of(
          "board",
          ofBoard(state),
          "score",
          ofScore(state),
          "special",
          ofSpecialCells(state),
          "next",
          ofNextMove(state));
    }
  }

  @VisibleForTesting
  public FloatList direct(State state) {
    FloatList.Builder list = FloatList.newBuilder();

    // Board [18]
    Arrays.stream(state.getCells())
        .mapToDouble(cell -> 1.0 * cell / 162.0)
        .forEachOrdered(cell -> list.addValue((float) cell));

    // Special cells [18]
    float[] specials = new float[18];
    Stream.of(Player.ONE, Player.TWO)
            .filter(state.getSpecialCells()::containsKey)
            .forEach(player -> specials[state.getSpecialCells().get(player)] = 1.0f);

    for (float special : specials) {
      list.addValue(special);
    }

    // Score [2]
    Stream.of(Player.ONE, Player.TWO)
            .mapToDouble(player -> state.getScore().getOrDefault(player, 0) / 82.0)
            .forEachOrdered(score -> list.addValue((float) score));

    // Next [1]
    list.addValue(state.getNextMove().ordinal());

    return list.build();
  }
}
