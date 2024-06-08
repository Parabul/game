package kz.ninestones.game.learning.encode;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.mu.util.stream.BiStream;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.utils.TensorFlowUtils;
import org.tensorflow.example.Feature;
import org.tensorflow.example.FloatList;

public class CompactStateEncoder implements StateEncoder, Serializable {

  @VisibleForTesting
  public static FloatList direct(State state) {
    FloatList.Builder list = FloatList.newBuilder();

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
        .mapToDouble(player -> state.getScore().getOrDefault(player, 0) / 81.0)
        .forEachOrdered(score -> list.addValue((float) score));

    // Next [1]
    list.addValue(state.getNextMove().ordinal());

    // Board [4]
    int playerOneTotal = IntStream.rangeClosed(9, 17).map(i -> state.getCells()[i]).sum();
    int playerTwoTotal = IntStream.rangeClosed(0, 8).map(i -> state.getCells()[i]).sum();

    list.addValue((float) playerOneTotal / 162.0f);
    list.addValue((float) playerTwoTotal / 162.0f);

    int playerOneWeighedPosition =
        IntStream.rangeClosed(9, 17).map(i -> Policy.moveByCell(i) * state.getCells()[i]).sum();
    int playerTwoWeighedPosition =
        IntStream.rangeClosed(0, 8).map(i -> Policy.moveByCell(i) * state.getCells()[i]).sum();

    list.addValue(
        playerOneTotal > 0 ? (float) playerOneWeighedPosition / playerOneTotal / 9.0f : 0.0f);
    list.addValue(
        playerTwoTotal > 0 ? (float) playerTwoWeighedPosition / playerTwoTotal / 9.0f : 0.0f);

    return list.build();
  }

  @Override
  public Map<String, Feature> featuresOf(State state, boolean direct) {
    return ImmutableMap.of(
        StateEncoder.INPUT, Feature.newBuilder().setFloatList(direct(state)).build());
  }
}
