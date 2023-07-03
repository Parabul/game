package kz.ninestones.game.modeling.strategy;

import java.util.*;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.simulation.LocalMonteCarloPlayOutSimulator;
import kz.ninestones.game.simulation.MonteCarloPlayOutSimulator;

public class MonteCarloTreeSearch {


  private final MonteCarloTreeNode root = MonteCarloTreeNode.ROOT.get();

  private final MonteCarloPlayOutSimulator playOutSimulator;

  public MonteCarloTreeSearch() {
    this(new LocalMonteCarloPlayOutSimulator());
  }

  public MonteCarloTreeSearch(MonteCarloPlayOutSimulator playOutSimulator) {
    this.playOutSimulator = playOutSimulator;
  }

  public void expand() {

    MonteCarloTreeNode currentNode = root;
    int i = 0;
    while (!Policy.isGameOver(currentNode.getState())) {
      i++;
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

  public List<MonteCarloTreeNode> traverse() {
    Queue<MonteCarloTreeNode> queue = new LinkedList<>();
    queue.add(root);

    List<MonteCarloTreeNode> traversal = new ArrayList<>();

    while (!queue.isEmpty()) {
      MonteCarloTreeNode current = queue.poll();

      traversal.add(current);

      queue.addAll(current.getChildren());
    }

    return traversal;
  }

  public MonteCarloTreeNode getRoot() {
    return root;
  }
}
