package kz.ninestones.game.learning.montecarlo;

import java.util.*;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.simulation.montecarlo.MonteCarloPlayOutSimulator;
import kz.ninestones.game.simulation.montecarlo.RandomMonteCarloPlayOutSimulator;

public class TreeSearch {

  private final TreeNode root;

  private final MonteCarloPlayOutSimulator playOutSimulator;

  public TreeSearch() {
    this(new RandomMonteCarloPlayOutSimulator());
  }

  public TreeSearch(MonteCarloPlayOutSimulator playOutSimulator) {
    this.playOutSimulator = playOutSimulator;
    this.root = TreeNode.ROOT.get();
  }

  public void expand() {

    TreeNode currentNode = root;
    while (!Policy.isGameOver(currentNode.getState())) {

      if (currentNode.getChildren().isEmpty()) {
        currentNode.initChildren();
      }

      playOutSimulator.playOut(currentNode);

      final Player nextMovePlayer = currentNode.getState().nextMove;

      currentNode =
          Collections.max(
              currentNode.getChildren(),
              Comparator.comparing(node -> node.getWeight(nextMovePlayer)));
    }
  }

  public List<TreeNode> traverse() {
    Queue<TreeNode> queue = new LinkedList<>();
    queue.add(root);

    List<TreeNode> traversal = new ArrayList<>();

    while (!queue.isEmpty()) {
      TreeNode current = queue.poll();

      traversal.add(current);

      queue.addAll(current.getChildren());
    }

    return traversal;
  }

  public TreeNode getRoot() {
    return root;
  }

  public void mergeFrom(TreeSearch other) {
    getRoot().mergeFrom(other.getRoot());
  }
}
