package kz.ninestones.game.simulation;

import kz.ninestones.game.core.Player;

public class SimulationResult {

  private final Player winner;
  private final int steps;

  public SimulationResult(Player winner, int steps) {
    this.winner = winner;
    this.steps = steps;
  }


  public Player getWinner() {
    return winner;
  }

  public int getSteps() {
    return steps;
  }
}
