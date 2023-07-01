package kz.ninestones.game.modeling.strategy;

import java.util.*;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.simulation.SimulationResult;

public class MonteCarloTreeSearch {

  private static final long NUM_SIMULATIONS = 10;

  private final long numSimulations;

  private final MonteCarloTreeNode root = MonteCarloTreeNode.ROOT.get();

  private final GameSimulator gameSimulator;

  public MonteCarloTreeSearch() {
    this(NUM_SIMULATIONS, new GameSimulator(Strategies.RANDOM, Strategies.RANDOM));
  }

  public MonteCarloTreeSearch(long numSimulations, GameSimulator gameSimulator) {
    this.numSimulations = numSimulations;
    this.gameSimulator = gameSimulator;
  }

  public void expand() {

    MonteCarloTreeNode currentNode = root;
    int i = 0;
    while (!Policy.isGameOver(currentNode.getState())) {
      i++;
      if (currentNode.getChildren().isEmpty()) {
        currentNode.initChildren();
      }

      for (MonteCarloTreeNode childNode : currentNode.getChildren()) {
        SimulationResult simulationResult =
            gameSimulator.playOut(childNode.getState(), numSimulations);
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
