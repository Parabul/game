package kz.ninestones.game.modeling.strategy;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.function.Function.identity;

import java.util.Map;
import java.util.stream.IntStream;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.evaluation.StateEvaluator;
import kz.ninestones.game.utils.ModelUtils;

public class SampledMoveStrategy implements Strategy {

  private final StateEvaluator stateEvaluator;

  public SampledMoveStrategy(StateEvaluator stateEvaluator) {
    this.stateEvaluator = stateEvaluator;
  }

  @Override
  public int suggestNextMove(State state) {
    Map<Integer, Double> outcomes =
        IntStream.rangeClosed(1, 9)
            .filter(move -> Policy.isAllowedMove(state, move))
            .boxed()
            .collect(
                toImmutableMap(
                    identity(),
                    move -> stateEvaluator.evaluate(Policy.makeMove(state, move), state.nextMove)));

    return ModelUtils.sampled(outcomes);
  }
}
