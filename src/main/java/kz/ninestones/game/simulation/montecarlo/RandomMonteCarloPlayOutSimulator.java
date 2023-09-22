package kz.ninestones.game.simulation.montecarlo;

import kz.ninestones.game.learning.montecarlo.TreeNode;
import kz.ninestones.game.modeling.strategy.Strategies;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.simulation.SimulationResult;

public class RandomMonteCarloPlayOutSimulator implements MonteCarloPlayOutSimulator {

  private final GameSimulator gameSimulator;

  public RandomMonteCarloPlayOutSimulator() {
    this(new GameSimulator(Strategies.RANDOM, Strategies.RANDOM));
  }

  public RandomMonteCarloPlayOutSimulator(GameSimulator gameSimulator) {
    this.gameSimulator = gameSimulator;
  }

  @Override
  public void playOut(TreeNode currentNode) {
    for (TreeNode childNode : currentNode.getChildren()) {
      SimulationResult simulationResult =
          gameSimulator.playOut(childNode.getState(), getNumSimulations());
      childNode.update(simulationResult);
      childNode.backPropagate(simulationResult);
    }
  }
}
