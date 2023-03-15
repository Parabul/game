package kz.ninestones.game.core;

import java.util.List;
import javax.annotation.Nullable;

public class RecordedGame {

  @Nullable
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
