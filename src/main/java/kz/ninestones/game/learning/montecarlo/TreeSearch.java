package kz.ninestones.game.learning.montecarlo;

import java.util.*;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.simulation.SimulationResult;

public class TreeSearch {

  private static final int NUM_SIMULATIONS = 1;

  private final TreeNode root;

  private final GameSimulator gameSimulator;

  public TreeSearch() {
    this(GameSimulator.RANDOM_VS_RANDOM);
  }

  public TreeSearch(GameSimulator gameSimulator) {
    this(gameSimulator, TreeNode.ROOT.get());
  }

  public TreeSearch(GameSimulator gameSimulator, TreeNode root) {
    this.gameSimulator = gameSimulator;
    this.root = root;
  }


  public void expand() {

    TreeNode currentNode = root;
    while (!Policy.isGameOver(currentNode.getState())) {

      if (currentNode.getChildren().isEmpty()) {
        currentNode.initChildren();
      }

      for (TreeNode childNode : currentNode.getChildren()) {
        SimulationResult simulationResult =
                gameSimulator.playOut(childNode.getState(), NUM_SIMULATIONS);
        childNode.update(simulationResult);
        childNode.backPropagate(simulationResult);
      }

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
