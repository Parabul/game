package kz.ninestones.game.modeling.evaluation;

import java.util.Optional;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.modeling.evaluation.StateEvaluator;

public class ScoreStateEvaluator implements StateEvaluator {


  @Override
  public double evaluate(State state, Player player) {
    Optional<Player> gameOver = Policy.isGameOver(state);

    if (gameOver.isPresent()) {
      return gameOver.get().equals(player) ? 1 : 0;
    }

    return state.score.get(player) > 81 ? 1.0 : state.score.get(player) / 82.0;
  }
}
