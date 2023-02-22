package kz.ninestones.game.simulation;

import java.util.List;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;

public class RecordedGame {

  private final Player winner;
  private final List<State> steps;

  public RecordedGame(Player winner, List<State> steps) {
    this.winner = winner;
    this.steps = steps;
  }


  public Player getWinner() {
    return winner;
  }

  public List<State> getSteps() {
    return steps;
  }
}
