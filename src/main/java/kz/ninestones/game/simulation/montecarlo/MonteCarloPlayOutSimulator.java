package kz.ninestones.game.simulation.montecarlo;

import kz.ninestones.game.learning.montecarlo.TreeNode;

public interface MonteCarloPlayOutSimulator {

  long NUM_SIMULATIONS = 1;

  default long getNumSimulations() {
    return NUM_SIMULATIONS;
  }

  void playOut(TreeNode currentNode);
}
