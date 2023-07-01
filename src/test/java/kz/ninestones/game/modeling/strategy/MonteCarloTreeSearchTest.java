package kz.ninestones.game.modeling.strategy;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.simulation.GameSimulator;
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

    for (int i = 0; i < 10; i++) {
      monteCarloTreeSearch.expand();
    }

    List<MonteCarloTreeNode> traversal = monteCarloTreeSearch.traverse();

    long gameEnds = traversal.stream().filter(node -> Policy.isGameOver(node.getState())).count();

    assertThat(gameEnds).isAtLeast(10);

    State init = new State();

    long root = traversal.stream().filter(node -> node.getState().equals(init)).count();

    assertThat(root).isEqualTo(1);
    assertThat(monteCarloTreeSearch.getRoot().getState()).isEqualTo(init);

    System.out.println(monteCarloTreeSearch.getRoot().getObservedWinners());
    System.out.println("traversal size: " + traversal.size());
  }

  @Test
  public void expandsWithCustomConstructor() {
    MonteCarloTreeSearch monteCarloTreeSearch =
        new MonteCarloTreeSearch(2, new GameSimulator(Strategies.RANDOM, Strategies.RANDOM));

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
  public void expandsWithCustomSimulations() {
    MonteCarloTreeSearch monteCarloTreeSearch =
        new MonteCarloTreeSearch(
            1, new GameSimulator(Strategies.MIN_MAX_SCORE_DIFF, Strategies.MIN_MAX_SCORE_DIFF));

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
}
