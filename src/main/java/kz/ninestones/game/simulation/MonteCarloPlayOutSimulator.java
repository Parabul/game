package kz.ninestones.game.simulation;

import kz.ninestones.game.modeling.strategy.MonteCarloTreeNode;

public interface MonteCarloPlayOutSimulator {

  long NUM_SIMULATIONS = 10;

  default long getNumsimulations() {
    return NUM_SIMULATIONS;
  }

  void playOut(MonteCarloTreeNode currentNode);
}
