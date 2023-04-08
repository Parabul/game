package kz.ninestones.game.modeling;

import com.google.common.collect.Maps;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DistributionSampling {

  private final Map<Integer, Double> distribution;

  public DistributionSampling(Map<Integer, Double> outcomeScores) {
    DoubleSummaryStatistics statistics = outcomeScores.values().stream()
        .mapToDouble(Double::valueOf).summaryStatistics();

    double sum = statistics.getSum() - outcomeScores.size() * statistics.getMin();

    distribution = Maps.transformValues(outcomeScores,
        score -> (score - statistics.getMin()) / sum);
  }


  public int getDistributedRandomNumber() {
    double rand = ThreadLocalRandom.current().nextDouble();
    double tempDist = 0;
    for (Integer key : distribution.keySet()) {
      tempDist += distribution.get(key);
      if (rand <= tempDist) {
        return key;
      }
    }

    return distribution.keySet().stream().findFirst().get();
  }
}
