package kz.ninestones.game.modeling;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.AtomicLongMap;
import org.junit.Test;

public class DistributionSamplingTest {

  @Test
  public void samplesFromTwo() {
    DistributionSampling distributionSampling = new DistributionSampling(
        ImmutableMap.of(1, 0.8, 2, 0.2));

    AtomicLongMap<Integer> observed = AtomicLongMap.create();

    double n = 1000000.0;

    for (int i = 0; i < n; i++) {
      observed.incrementAndGet(distributionSampling.getDistributedRandomNumber());
    }

    assertThat(observed.size()).isEqualTo(2);

    assertThat(observed.get(1) / n).isWithin(0.01).of(0.8);
    assertThat(observed.get(2) / n).isWithin(0.01).of(0.2);
  }


  @Test
  public void samplesFromThreeSumOfOne() {
    DistributionSampling distributionSampling = new DistributionSampling(
        ImmutableMap.of(1, 0.6, 3, 0.2, 2, 0.2));

    AtomicLongMap<Integer> observed = AtomicLongMap.create();

    double n = 1000000.0;

    for (int i = 0; i < n; i++) {
      observed.incrementAndGet(distributionSampling.getDistributedRandomNumber());
    }

    assertThat(observed.size()).isEqualTo(3);

    assertThat(observed.get(1) / n).isWithin(0.01).of(0.6);
    assertThat(observed.get(2) / n).isWithin(0.01).of(0.2);
    assertThat(observed.get(3) / n).isWithin(0.01).of(0.2);
  }

  @Test
  public void samplesFromFourSumNotOne() {
    DistributionSampling distributionSampling = new DistributionSampling(
        ImmutableMap.of(1, 5.0, 3, 5.0, 2, 5.0, 7, 5.0));

    AtomicLongMap<Integer> observed = AtomicLongMap.create();

    double n = 1000000.0;

    for (int i = 0; i < n; i++) {
      observed.incrementAndGet(distributionSampling.getDistributedRandomNumber());
    }

    assertThat(observed.size()).isEqualTo(4);

    assertThat(observed.get(1) / n).isWithin(0.01).of(0.25);
    assertThat(observed.get(2) / n).isWithin(0.01).of(0.25);
    assertThat(observed.get(3) / n).isWithin(0.01).of(0.25);
    assertThat(observed.get(7) / n).isWithin(0.01).of(0.25);
  }
}
