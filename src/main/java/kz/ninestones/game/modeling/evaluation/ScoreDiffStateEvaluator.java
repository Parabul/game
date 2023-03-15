package kz.ninestones.game.modeling.evaluation;

import java.util.Optional;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;

public class ScoreDiffStateEvaluator implements StateEvaluator {


  @Override
  public double evaluate(State state, Player player) {
    if (Policy.isGameOver(state)) {
      Optional<Player> winner = Policy.winnerOf(state);
      return winner.isPresent() && winner.get().equals(player) ? 1 : 0;
    }

    int diff = state.score.get(player) - state.score.get(player.opponent);
    if (diff > 81) {
      return 1;
    }
    if (diff < -81) {
      return 0;
    }
    return ((diff / 81.0) + 1.0) / 2;
  }
}
