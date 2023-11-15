package kz.ninestones.game.learning.montecarlo;

import com.google.common.collect.ArrayListMultimap;
import java.io.Serializable;
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

  private final TreeData treeData;

  public String getRootId() {
    return rootId;
  }

  private final String rootId;

  public MonteCarloTreeSearch(GameSimulator gameSimulator) {
    this(gameSimulator, new TreeData());
  }

  public MonteCarloTreeSearch(GameSimulator gameSimulator, TreeData treeData) {
    this(gameSimulator, treeData, new State());
  }

  public MonteCarloTreeSearch(GameSimulator gameSimulator, TreeData treeData, State rootState) {
    this.gameSimulator = gameSimulator;
    this.treeData = treeData;
    this.rootId = rootState.getId();
    this.treeData.index.putIfAbsent(rootId, new StateNode(rootState));
  }

  public void expand() {
    StateNode currentNode = this.treeData.index.get(rootId);

    Stack<NodeStats> backProgagationStack = new Stack<>();

    while (!Policy.isGameOver(currentNode.getState())) {

      String currentNodeId = currentNode.getState().getId();

      if (!this.treeData.parentToChild.containsKey(currentNodeId)) {
        initChildren(currentNode, currentNodeId);
      }

      List<StateNode> childNodes =
          this.treeData.parentToChild.get(currentNodeId).stream()
              .map(this.treeData.index::get)
              .collect(Collectors.toList());

      SimulationResult simulationResultToPropagate = new SimulationResult();
      for (StateNode childNode : childNodes) {
        SimulationResult simulationResult =
            gameSimulator.playOut(childNode.getState(), NUM_SIMULATIONS);
        childNode.update(simulationResult);
        simulationResultToPropagate.merge(simulationResult);
      }

      backProgagationStack.push(new NodeStats(currentNode, simulationResultToPropagate));

      final long simulations =
          currentNode.getSimulations()
              + simulationResultToPropagate.getObservedWinners().values().stream()
                  .collect(Collectors.summingInt(Integer::intValue));

      currentNode =
          Collections.max(childNodes, Comparator.comparing(node -> getWeight(simulations, node)));
    }

    SimulationResult simulationResultToPropagate = new SimulationResult();
    while (!backProgagationStack.empty()) {
      NodeStats nodeStats = backProgagationStack.pop();
      simulationResultToPropagate.merge(nodeStats.simulationResult);
      nodeStats.node.update(simulationResultToPropagate);
    }
  }

  private Double getWeight(long parentSimulations, StateNode childNode) {
    long childNodeSimulations = childNode.getSimulations();
    Player nextMovePlayer = childNode.getState().nextMove.opponent;

    double exploration =
        (parentSimulations > 0 && childNodeSimulations > 0)
            ? (Math.sqrt(MathUtils.ln(parentSimulations) / childNodeSimulations))
            : 0;
    double exploitation =
        childNodeSimulations > 0
            ? 1.0 * childNode.getObservedOutcomes().get(nextMovePlayer) / childNodeSimulations
            : 0;

    return exploitation + EXPLORATION_WEIGHT * exploration;
  }

  private void initChildren(final StateNode stateNode, final String stateId) {
    State parentState = stateNode.getState();
    IntStream.rangeClosed(1, 9)
        .filter(move -> Policy.isAllowedMove(parentState, move))
        .forEach(
            move -> {
              State childState = Policy.makeMove(parentState, move);
              String childStateId = childState.getId();
              this.treeData.index.putIfAbsent(childStateId, new StateNode(childState));
              this.treeData.parentToChild.put(stateId, childStateId);
            });
  }

  public TreeData getTreeData() {
    return treeData;
  }

  private static class NodeStats {
    final StateNode node;
    final SimulationResult simulationResult;

    NodeStats(StateNode node, SimulationResult simulationResult) {
      this.node = node;
      this.simulationResult = simulationResult;
    }
  }

  public static class TreeData implements Serializable {
    private final Map<String, StateNode> index;

    private final ArrayListMultimap<String, String> parentToChild;

    public TreeData(){
      index = new HashMap<>();
      parentToChild  = ArrayListMultimap.create();
    }

    public TreeData(TreeData other){
      index = new HashMap<>();
      other.index.forEach((key, value) -> index.put(key, new StateNode(value)));
      parentToChild  = ArrayListMultimap.create(other.parentToChild);
    }

    public void merge(TreeData other) {
      other.getIndex().forEach((key, value) -> this.index.merge(key, value, (o, n) -> o.merge(n)));
      this.parentToChild.putAll(other.parentToChild);
    }

    public Map<String, StateNode> getIndex() {
      return index;
    }

    public ArrayListMultimap<String, String> getParentToChild() {
      return parentToChild;
    }
  }
}
