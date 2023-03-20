package kz.ninestones.game.modeling.strategy;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.function.Function.identity;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.modeling.evaluation.StateEvaluator;

public class MaxStrategy implements Strategy {

  private final StateEvaluator stateEvaluator;

  public MaxStrategy(StateEvaluator stateEvaluator) {
    this.stateEvaluator = stateEvaluator;
  }

  @Override
  public int suggestNextMove(State state) {
    new HashMap<>();

    Player player = state.nextMove;

    Map<Integer, Double> outcomes = IntStream.rangeClosed(1, 9)
        .filter(move -> Policy.isAllowedMove(state, move)).boxed().collect(
            toImmutableMap(identity(), move ->
                stateEvaluator.evaluate(Policy.makeMove(state, move), player)
            ));

    return ModelUtils.anyMaximizingKey(outcomes);
  }
}