package kz.ninestones.game.learning.montecarlo;

import com.google.common.collect.ArrayListMultimap;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.MathUtils;

public class MonteCarloTreeSearch {

  private static final int NUM_SIMULATIONS = 1;
  private static final double EXPLORATION_WEIGHT = Math.sqrt(2);

  private final GameSimulator gameSimulator;

  private final Map<String, StateNode> index = new HashMap<>();

  private final ArrayListMultimap<String, String> parentToChild = ArrayListMultimap.create();

  private final String rootId;

  public MonteCarloTreeSearch(GameSimulator gameSimulator) {
    this.gameSimulator = gameSimulator;
    State rootState = new State();
    this.rootId = rootState.getId();
    index.put(rootId, new StateNode(rootState));
  }

  public void expand() {
    StateNode currentNode = index.get(rootId);

    Stack<NodeStats> backProgagationStack = new Stack<>();

    while (!Policy.isGameOver(currentNode.getState())) {
//      System.out.println(currentNode.getState());

      String currentNodeId = currentNode.getState().getId();

      if (!parentToChild.containsKey(currentNodeId)) {
        initChildren(currentNodeId);
      }

      SimulationResult simulationResultToPropagate = new SimulationResult();
      for (String childNodeId : parentToChild.get(currentNodeId)) {
        StateNode childNode = index.get(childNodeId);
        SimulationResult simulationResult =
            gameSimulator.playOut(childNode.getState(), NUM_SIMULATIONS);
        childNode.update(simulationResult);
        simulationResultToPropagate.merge(simulationResult);
      }

      backProgagationStack.push(new NodeStats(currentNodeId, simulationResultToPropagate));

      final Player nextMovePlayer = currentNode.getState().nextMove;

      final long simulations =
          currentNode.getSimulations()
              + simulationResultToPropagate.getObservedWinners().values().stream()
                  .collect(Collectors.summingInt(Integer::intValue));


      currentNode =
              parentToChild.get(currentNodeId).stream()
              .map(index::get)
              .max(Comparator.comparing(node -> getWeight(simulations, node, nextMovePlayer)))
              .get();
    }

    SimulationResult simulationResultToPropagate = new SimulationResult();
    while (!backProgagationStack.empty()) {
      NodeStats nodeStats = backProgagationStack.pop();
      simulationResultToPropagate.merge(nodeStats.simulationResult);
      index.get(nodeStats.nodeId).update(simulationResultToPropagate);
    }
  }

  private Double getWeight(long parentSimulations, StateNode childNode, Player player) {
    long childNodeSimulations = childNode.getSimulations();

    double exploration =
        (parentSimulations > 0 && childNodeSimulations > 0)
            ? (Math.sqrt(MathUtils.ln(parentSimulations) / childNodeSimulations))
            : 0;
    double exploitation =
        childNodeSimulations > 0
            ? 1.0 * childNode.getObservedOutcomes().get(player) / childNodeSimulations
            : 0;

    return exploitation + EXPLORATION_WEIGHT * exploration;
  }

  private Collection<String> initChildren(String stateId) {
    final StateNode currentStateNode = index.get(stateId);
    IntStream.rangeClosed(1, 9)
        .filter(move -> Policy.isAllowedMove(currentStateNode.getState(), move))
        .forEach(
            move -> {
              State childState = Policy.makeMove(currentStateNode.getState(), move);
              index.putIfAbsent(childState.getId(), new StateNode(childState));
              parentToChild.put(stateId, childState.getId());
            });

    return parentToChild.get(stateId);
  }

  public Map<String, StateNode> getIndex() {
    return index;
  }

  private static class NodeStats {
    final String nodeId;
    final SimulationResult simulationResult;

    NodeStats(String nodeId, SimulationResult simulationResult) {
      this.nodeId = nodeId;
      this.simulationResult = simulationResult;
    }
  }
}
