package kz.ninestones.game.simulation;

import kz.ninestones.game.modeling.strategy.MonteCarloTreeNode;
import kz.ninestones.game.modeling.strategy.Strategies;

public class LocalMonteCarloPlayOutSimulator implements MonteCarloPlayOutSimulator {

  private final GameSimulator gameSimulator;

  public LocalMonteCarloPlayOutSimulator() {
    this(new GameSimulator(Strategies.RANDOM, Strategies.RANDOM));
  }

  public LocalMonteCarloPlayOutSimulator(GameSimulator gameSimulator) {
    this.gameSimulator = gameSimulator;
  }

  @Override
  public void playOut(MonteCarloTreeNode currentNode) {
    for (MonteCarloTreeNode childNode : currentNode.getChildren()) {
      SimulationResult simulationResult =
          gameSimulator.playOut(childNode.getState(), getNumsimulations());
      childNode.update(simulationResult);
      childNode.backPropagate(simulationResult);
    }
  }
}
