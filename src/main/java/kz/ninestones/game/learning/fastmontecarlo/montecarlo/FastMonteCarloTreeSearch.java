package kz.ninestones.game.learning.fastmontecarlo.montecarlo;

import java.util.*;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.MathUtils;

public class FastMonteCarloTreeSearch {

    private static final int NUM_SIMULATIONS = 1;
    private static final double EXPLORATION_WEIGHT = Math.sqrt(2);

    private final GameSimulator gameSimulator;
    private final GameStateNode root;

    public FastMonteCarloTreeSearch(GameSimulator gameSimulator) {
        this(gameSimulator, new GameStateNode(new State()));
    }

    public FastMonteCarloTreeSearch(GameSimulator gameSimulator, GameStateNode root) {
        this.gameSimulator = gameSimulator;
        this.root = root;
    }

    public GameStateNode getRoot() {
        return root;
    }

    public void expand() {
        GameStateNode currentNode = root;

        Stack<NodeStats> backProgagationStack = new Stack<>();

        while (!Policy.isGameOver(currentNode.getState())) {

            if (currentNode.getChildStates().isEmpty()) {
                currentNode.initChildren();
            }

            SimulationResult simulationResultToPropagate = new SimulationResult();
            for (GameStateNode childNode : currentNode.getChildStates()) {
                SimulationResult simulationResult = gameSimulator.playOut(childNode.getState(), NUM_SIMULATIONS);
                childNode.update(simulationResult);
                simulationResultToPropagate.merge(simulationResult);
            }

            backProgagationStack.push(new NodeStats(currentNode, simulationResultToPropagate));

            final long simulations = currentNode.getSimulations() + simulationResultToPropagate.getObservedWinners().values().stream().mapToInt(Integer::intValue).sum();

            currentNode = Collections.max(currentNode.getChildStates(), Comparator.comparing(node -> getWeight(simulations, node)));
        }

        SimulationResult simulationResultToPropagate = new SimulationResult();
        while (!backProgagationStack.empty()) {
            NodeStats nodeStats = backProgagationStack.pop();
            simulationResultToPropagate.merge(nodeStats.simulationResult);
            nodeStats.node.update(simulationResultToPropagate);
        }
    }

    private Double getWeight(long parentSimulations, GameStateNode childNode) {
        long childNodeSimulations = childNode.getSimulations();
        Player nextMovePlayer = childNode.getState().getNextMove().opponent;

        double exploration = (parentSimulations > 0 && childNodeSimulations > 0) ? (Math.sqrt(MathUtils.ln(parentSimulations) / childNodeSimulations)) : 0;
        double exploitation = childNodeSimulations > 0 ? 1.0 * childNode.getObservedOutcomes().get(nextMovePlayer) / childNodeSimulations : 0;

        return exploitation + EXPLORATION_WEIGHT * exploration;
    }

    public List<GameStateNode.GameStateNodeInfo> traverse() {
        List<GameStateNode.GameStateNodeInfo> nodes = new ArrayList<>();
        Stack<GameStateNode> stack = new Stack<GameStateNode>();
        GameStateNode current = root;
        stack.push(root);

        while (!stack.isEmpty()) {
            current = stack.pop();
            nodes.add(new GameStateNode.GameStateNodeInfo(current.getState(),current.getObservedOutcomes()));

            current.getChildStates().forEach(stack::push);
        }

        return nodes;
    }

    private static class NodeStats {
        final GameStateNode node;
        final SimulationResult simulationResult;

        NodeStats(GameStateNode node, SimulationResult simulationResult) {
            this.node = node;
            this.simulationResult = simulationResult;
        }
    }
}
