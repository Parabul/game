package kz.ninestones.game.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import com.google.common.math.DoubleMath;
import com.google.mu.util.stream.BiStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ModelUtils {

  public static final double PRECISION = 0.001;

  public static int anyMaximizingKey(Map<Integer, Double> outcomes) {
    checkArgument(!outcomes.isEmpty(), "outcomes");

    double maxOutcome = Collections.max(outcomes.values());

    ImmutableList<Integer> maximizingMoves =
        BiStream.from(outcomes)
            .filterValues(outcome -> DoubleMath.fuzzyEquals(outcome, maxOutcome, PRECISION))
            .keys()
            .collect(toImmutableList());

    return maximizingMoves.get(ThreadLocalRandom.current().nextInt(maximizingMoves.size()));
  }

  public static int firstMaximizingKey(Map<Integer, Double> outcomes) {
    checkArgument(!outcomes.isEmpty(), "outcomes");

    double maxOutcome = Collections.max(outcomes.values());

    ImmutableList<Integer> maximizingMoves =
        BiStream.from(outcomes)
            .filterValues(outcome -> DoubleMath.fuzzyEquals(outcome, maxOutcome, PRECISION))
            .keys()
            .collect(toImmutableList());

    return maximizingMoves.get(0);
  }
}
