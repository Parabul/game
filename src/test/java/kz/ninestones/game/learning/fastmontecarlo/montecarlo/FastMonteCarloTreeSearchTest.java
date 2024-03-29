package kz.ninestones.game.learning.fastmontecarlo.montecarlo;

import static com.google.common.truth.Truth.assertThat;

import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.montecarlo.MonteCarloTreeSearch;
import kz.ninestones.game.learning.montecarlo.StateNode;
import kz.ninestones.game.simulation.GameSimulator;
import org.junit.Test;

public class FastMonteCarloTreeSearchTest {

  @Test
  public void expandsFewTimes() {
    FastMonteCarloTreeSearch monteCarloTreeSearchNew =
        new FastMonteCarloTreeSearch(GameSimulator.RANDOM);

    for (int i = 0; i < 10; i++) {
      monteCarloTreeSearchNew.expand();
    }

    GameStateNode rootNew = monteCarloTreeSearchNew.getRoot();

    assertThat(rootNew.getState()).isEqualTo(new State());

    //        assertThat(rootNew.getSimulations()).isEqualTo(100);

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

  @Test
  public void regression() {
    FastMonteCarloTreeSearch monteCarloTreeSearchNew =
        new FastMonteCarloTreeSearch(GameSimulator.DETEMINISTIC);
    MonteCarloTreeSearch monteCarloTreeSearchOld =
        new MonteCarloTreeSearch(GameSimulator.DETEMINISTIC);

    for (int i = 0; i < 10; i++) {
      monteCarloTreeSearchNew.expand();

      monteCarloTreeSearchOld.expand();
    }

    //        List<GameStateNode> nodes = monteCarloTreeSearchNew.traverse();

    StateNode rootOld = monteCarloTreeSearchOld.getTreeData().getIndex().get((new State()).getId());

    GameStateNode rootNew = monteCarloTreeSearchNew.getRoot();

    assertThat(rootNew).isEqualTo(rootOld);
  }
}
