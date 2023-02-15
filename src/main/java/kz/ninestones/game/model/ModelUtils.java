package kz.ninestones.game.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ModelUtils {

  public static int anyMaximizingKey(Map<Integer, Double> outcomes){
    Preconditions.checkArgument(!outcomes.isEmpty(), "outcomes");

    double maxOutcome = outcomes.values().stream().max(Comparator.naturalOrder()).get();

    ImmutableList<Integer> maximizingMoves = ImmutableList.copyOf(
        Maps.filterValues(outcomes, outcome -> outcome == maxOutcome).keySet());

    int randomIndex = ThreadLocalRandom.current().nextInt(0, maximizingMoves.size());

    return maximizingMoves.get(randomIndex);
  }
}
