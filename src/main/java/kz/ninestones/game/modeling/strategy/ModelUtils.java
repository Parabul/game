package kz.ninestones.game.modeling.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import com.google.common.math.DoubleMath;
import com.google.mu.util.stream.BiStream;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import kz.ninestones.game.core.Constants;
import kz.ninestones.game.modeling.DistributionSampling;

public class ModelUtils {

  public static final Random RANDOM = new Random(Constants.SEED);

  public static int anyMaximizingKey(Map<Integer, Double> outcomes) {
    checkArgument(!outcomes.isEmpty(), "outcomes");

    double maxOutcome = Collections.max(outcomes.values());

    ImmutableList<Integer> maximizingMoves = BiStream.from(outcomes)
        .filterValues(outcome -> DoubleMath.fuzzyEquals(outcome, maxOutcome, Constants.PRECISION))
        .keys().collect(toImmutableList());

    return maximizingMoves.get(ThreadLocalRandom.current().nextInt(maximizingMoves.size()));
  }

  public static int sampled(Map<Integer, Double> outcomes) {
    checkArgument(!outcomes.isEmpty(), "outcomes");

    return (new DistributionSampling(outcomes)).getDistributedRandomNumber();
  }
}
