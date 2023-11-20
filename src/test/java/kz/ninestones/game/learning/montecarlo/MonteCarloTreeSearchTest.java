package kz.ninestones.game.learning.montecarlo;

import static com.google.common.truth.Truth.assertThat;

import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.simulation.GameSimulator;
import org.junit.Test;

public class MonteCarloTreeSearchTest {

  @Test
  public void expandsFewTimes() {
    MonteCarloTreeSearch monteCarloTreeSearchNew =
        new MonteCarloTreeSearch(GameSimulator.RANDOM_VS_RANDOM);

    for (int i = 0; i < 10; i++) {
      monteCarloTreeSearchNew.expand();
    }

    StateNode rootNew = monteCarloTreeSearchNew.getTreeData().getIndex().get((new State()).getId());

    System.out.println(rootNew.getObservedOutcomes());

    assertThat(1.0 * rootNew.getObservedOutcomes().get(Player.ONE) / rootNew.getSimulations())
        .isWithin(0.1)
        .of(0.5);
    assertThat(1.0 * rootNew.getObservedOutcomes().get(Player.TWO) / rootNew.getSimulations())
        .isWithin(0.1)
        .of(0.5);
    assertThat(1.0 * rootNew.getObservedOutcomes().get(Player.NONE) / rootNew.getSimulations())
        .isWithin(0.1)
        .of(0.0);
  }
}
