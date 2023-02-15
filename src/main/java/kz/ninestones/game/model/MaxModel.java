package kz.ninestones.game.model;

import java.util.HashMap;
import java.util.Map;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;

public class MaxModel implements Model {

  private final StateEvaluator stateEvaluator;

  public MaxModel(StateEvaluator stateEvaluator) {
    this.stateEvaluator = stateEvaluator;
  }

  @Override
  public int suggestNextMove(State state) {
    Map<Integer, Double> outcomes = new HashMap<>();

    Player player = state.nextMove;

    for (int move = 1; move < 10; move++) {
      if (Policy.isAllowedMove(state, move)) {
        State possibleState = Policy.makeMove(state, move);
        outcomes.put(move, stateEvaluator.evaluate(possibleState, player));
      }
    }

    return ModelUtils.anyMaximizingKey(outcomes);
  }
}
