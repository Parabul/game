package kz.ninestones.game.model;

import java.util.Optional;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;

public class ScoreStateEvaluator implements StateEvaluator {


  @Override
  public double evaluate(State state, Player player) {
    Optional<Player> gameOver = Policy.isGameOver(state);

    if (gameOver.isPresent()) {
      return gameOver.get().equals(player) ? 1 : 0;
    }

    int diff = -state.score[player.opponent.index];
    if (diff > 81) {
      return 1;
    }
    if (diff < -81) {
      return 0;
    }
    return state.score[player.index] > 81 ? 1.0 : state.score[player.index] / 81.0;
  }
}