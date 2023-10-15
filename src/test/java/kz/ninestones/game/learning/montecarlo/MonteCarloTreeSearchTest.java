package kz.ninestones.game.learning.montecarlo;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.simulation.GameSimulator;
import org.junit.Test;

public class MonteCarloTreeSearchTest {
  @Test
  public void expands() {
    TreeSearch monteCarloTreeSearch = new TreeSearch(GameSimulator.DETEMINISTIC);

    monteCarloTreeSearch.expand();

    List<String> traversal =
        monteCarloTreeSearch.traverse().stream()
            .map(node -> node.getState().getId())
            .distinct()
            .collect(Collectors.toList());
    assertThat(monteCarloTreeSearch.getRoot().getSimulations()).isEqualTo(1084);

    MonteCarloTreeSearch monteCarloTreeSearchNew =
        new MonteCarloTreeSearch(GameSimulator.DETEMINISTIC);
    monteCarloTreeSearchNew.expand();

    List<String> traversalNew =
        monteCarloTreeSearchNew.getTreeData().getIndex().values().stream()
            .map(node -> node.getState().getId())
            .collect(Collectors.toList());

    assertThat(traversalNew).containsExactlyElementsIn(traversal);

    StateNode rootNew = monteCarloTreeSearchNew.getTreeData().getIndex().get((new State()).getId());
    assertThat(rootNew.getSimulations()).isEqualTo(1084);
    assertThat(rootNew.getObservedOutcomes().get(Player.ONE)).isEqualTo(264);
    assertThat(rootNew.getObservedOutcomes().get(Player.TWO)).isEqualTo(820);
    assertThat(rootNew.getObservedOutcomes().get(Player.NONE)).isEqualTo(0);
  }

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
