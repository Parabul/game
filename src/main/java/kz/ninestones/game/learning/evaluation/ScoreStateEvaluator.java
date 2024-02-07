package kz.ninestones.game.learning.evaluation;

import java.util.Optional;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;

public class ScoreStateEvaluator implements StateEvaluator {


  @Override
  public double evaluate(State state, Player player) {
    if (Policy.isGameOver(state)) {
      Optional<Player> winner = Policy.winnerOf(state);
      return winner.isPresent() && winner.get().equals(player) ? 1 : 0;
    }

    return state.getScore().get(player) > 81 ? 1.0 : state.getScore().get(player) / 82.0;
  }
}
