package kz.ninestones.game.learning.evaluation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;

public class ScoreDiffStateEvaluator implements BatchStateEvaluator, StateEvaluator {

  @Override
  public double evaluate(State state, Player player) {
    if (Policy.isGameOver(state)) {
      Optional<Player> winner = Policy.winnerOf(state);
      return winner.isPresent() && winner.get().equals(player) ? 1 : 0;
    }

    int diff = state.getScore().get(player) - state.getScore().get(player.opponent);
    if (diff > 81) {
      return 1;
    }
    if (diff < -81) {
      return 0;
    }
    return ((diff / 81.0) + 1.0) / 2;
  }

  @Override
  public Map<String, Double> evaluate(List<State> states, Player player) {
    Map<String, Double> scoreByStateId = new HashMap<>();
    for (State state : states) {
      scoreByStateId.put(
          state.getId(),
          Policy.isGameOver(state)
              ? (Policy.winnerOf(state).get().equals(player) ? 1.01 : 0)
              : evaluate(state, player));
    }

    return scoreByStateId;
  }
}
