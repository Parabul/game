package kz.ninestones.game.modeling;

import java.util.Map;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

public class DistributionSampling {

  private final EnumeratedIntegerDistribution distribution;

  public DistributionSampling(Map<Integer, Double> outcomeScores) {
    this.distribution =
        new EnumeratedIntegerDistribution(
            outcomeScores.keySet().stream().mapToInt(Integer::intValue).toArray(),
            outcomeScores.values().stream().mapToDouble(Double::doubleValue).toArray());
  }

  public int getDistributedRandomNumber() {
    return distribution.sample();
  }
}
