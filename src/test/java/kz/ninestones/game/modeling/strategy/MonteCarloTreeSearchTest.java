package kz.ninestones.game.modeling.strategy;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import org.junit.Test;

public class MonteCarloTreeSearchTest {
  @Test
  public void expands() {
    MonteCarloTreeSearch monteCarloTreeSearch = new MonteCarloTreeSearch();

    monteCarloTreeSearch.expand();

    List<MonteCarloTreeNode> traversal = monteCarloTreeSearch.traverse();

    long gameEnds = traversal.stream().filter(node -> Policy.isGameOver(node.getState())).count();

    assertThat(gameEnds).isAtLeast(1);

    State init = new State();

    long root = traversal.stream().filter(node -> node.getState().equals(init)).count();

    assertThat(root).isEqualTo(1);

    System.out.println(monteCarloTreeSearch.getRoot().getObservedWinners());
    System.out.println("traversal size: " + traversal.size());
  }

  @Test
  public void expandsFewTimes() {
    MonteCarloTreeSearch monteCarloTreeSearch = new MonteCarloTreeSearch();

    for (int i = 0; i < 3; i++) {
      monteCarloTreeSearch.expand();
    }

    List<MonteCarloTreeNode> traversal = monteCarloTreeSearch.traverse();

    long gameEnds = traversal.stream().filter(node -> Policy.isGameOver(node.getState())).count();

    assertThat(gameEnds).isAtLeast(3);

    State init = new State();

    long root = traversal.stream().filter(node -> node.getState().equals(init)).count();

    assertThat(root).isEqualTo(1);
    assertThat(monteCarloTreeSearch.getRoot().getState()).isEqualTo(init);
  }
}
