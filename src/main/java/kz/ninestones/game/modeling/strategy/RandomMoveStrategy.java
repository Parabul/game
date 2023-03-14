package kz.ninestones.game.modeling.strategy;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import java.util.stream.IntStream;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;

public class RandomMoveStrategy implements Strategy {

  @Override
  public int suggestNextMove(final State state) {
    ImmutableMap<Integer, Double> allowedMoves = IntStream.rangeClosed(1, 9).boxed()
        .filter(move -> Policy.isAllowedMove(state, move)).collect(ImmutableMap.toImmutableMap(
            Function.identity(), move -> 1.0));

    return ModelUtils.anyMaximizingKey(allowedMoves);
  }
}
