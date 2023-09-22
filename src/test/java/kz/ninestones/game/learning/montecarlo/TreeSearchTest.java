package kz.ninestones.game.learning.montecarlo;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.stream.Stream;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import org.junit.Test;

public class TreeSearchTest {
  @Test
  public void expands() {
    TreeSearch monteCarloTreeSearch = new TreeSearch();

    monteCarloTreeSearch.expand();

    List<TreeNode> traversal = monteCarloTreeSearch.traverse();

    long gameEnds = traversal.stream().filter(node -> Policy.isGameOver(node.getState())).count();

    assertThat(gameEnds).isAtLeast(1);

    State init = new State();

    long root = traversal.stream().filter(node -> node.getState().equals(init)).count();

    assertThat(root).isEqualTo(1);
  }

  @Test
  public void shouldMerge() {
    TreeSearch self = new TreeSearch();
    TreeSearch other = new TreeSearch();

    self.expand();
    self.expand();

    other.expand();

    List<TreeNode> selfTraversal = self.traverse();
    List<TreeNode> otherTraversal = other.traverse();

    long selfSimulations = self.getRoot().getSimulations();
    long otherSimulations = other.getRoot().getSimulations();

    long combinedUnique =
        Stream.concat(
                selfTraversal.stream().map(TreeNode::getState),
                otherTraversal.stream().map(TreeNode::getState))
            .distinct()
            .count();

    assertThat(combinedUnique)
        .isLessThan(
            selfTraversal.stream().map(TreeNode::getState).distinct().count()
                + otherTraversal.stream().map(TreeNode::getState).distinct().count());

    self.mergeFrom(other);

    List<TreeNode> freshSelfTraversal = self.traverse();

    assertThat(combinedUnique).isEqualTo(freshSelfTraversal.stream().distinct().count());

    assertThat(self.getRoot().getSimulations()).isEqualTo(selfSimulations + otherSimulations);
  }

  @Test
  public void expandsFewTimes() {
    TreeSearch monteCarloTreeSearch = new TreeSearch();

    for (int i = 0; i < 3; i++) {
      monteCarloTreeSearch.expand();
    }

    List<TreeNode> traversal = monteCarloTreeSearch.traverse();

    long gameEnds = traversal.stream().filter(node -> Policy.isGameOver(node.getState())).count();

    assertThat(gameEnds).isAtLeast(3);

    State init = new State();

    long root = traversal.stream().filter(node -> node.getState().equals(init)).count();

    assertThat(root).isEqualTo(1);
    assertThat(monteCarloTreeSearch.getRoot().getState()).isEqualTo(init);
  }
}
