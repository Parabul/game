package kz.ninestones.game.simulation;

import kz.ninestones.game.modeling.strategy.MonteCarloTreeNode;

public interface MonteCarloPlayOutSimulator {
    void playOut(MonteCarloTreeNode currentNode, long numSimulations);
}
