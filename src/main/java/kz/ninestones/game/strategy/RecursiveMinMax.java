package kz.ninestones.game.strategy;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.evaluation.StateEvaluator;
import org.apache.commons.math3.util.Pair;

public class RecursiveMinMax implements Strategy {

  private final LoadingCache<State, Map<Integer, State>> childStatesCache =
      CacheBuilder.newBuilder()
          .maximumSize(10000)
          .expireAfterWrite(60, TimeUnit.SECONDS)
          .build(
              new CacheLoader<State, Map<Integer, State>>() {
                @Override
                public Map<Integer, State> load(State state) {
                  Map<Integer, State> childStates = new HashMap<>(9);
                  for (int move = 1; move <= 9; move++) {
                    if (Policy.isAllowedMove(state, move)) {

                      State childState = Policy.makeMove(state, move);
                      childStates.put(move, childState);
                    }
                  }

                  return childStates;
                }
              });
  private final StateEvaluator stateEvaluator;
  private final int depth;

  public RecursiveMinMax(StateEvaluator stateEvaluator, int depth) {
    this.stateEvaluator = stateEvaluator;
    this.depth = depth;
  }

  private Map<Integer, State> getChildStates(State state) {
    return childStatesCache.getUnchecked(state);
  }

  public Pair<Integer, Double> minimax(
      int depth, int move, State state, Player maximizingPlayer, double alpha, double beta) {

    if (depth == this.depth || Policy.isGameOver(state)) {
      return Pair.create(move, stateEvaluator.evaluate(state, maximizingPlayer));
    }

    Pair<Integer, Double> optimalMove = null;
    Map<Integer, State> childStates = getChildStates(state);

    if (maximizingPlayer.equals(state.getNextMove())) {
      for (Map.Entry<Integer, State> childState : childStates.entrySet()) {
        Pair<Integer, Double> alternative =
            minimax(
                depth + 1,
                childState.getKey(),
                childState.getValue(),
                maximizingPlayer,
                alpha,
                beta);
        if (optimalMove == null || alternative.getValue() > optimalMove.getValue()) {
          // TODO: Consider all alternative moves with optimal value;
          optimalMove = Pair.create(childState.getKey(), alternative.getValue());
          alpha = Math.max(alpha, optimalMove.getValue());
        }

        // Alpha Beta Pruning
        if (beta <= alpha) break;
      }
      return optimalMove;
    } else {
      for (Map.Entry<Integer, State> childState : childStates.entrySet()) {
        Pair<Integer, Double> alternative =
            minimax(
                depth + 1,
                childState.getKey(),
                childState.getValue(),
                maximizingPlayer,
                alpha,
                beta);

        if (optimalMove == null || alternative.getValue() < optimalMove.getValue()) {
          // TODO: Consider all alternative moves with optimal value;
          optimalMove = Pair.create(childState.getKey(), alternative.getValue());
          beta = Math.min(beta, optimalMove.getValue());
        }
        // Alpha Beta Pruning
        if (beta <= alpha) break;
      }
      return optimalMove;
    }
  }

  @Override
  public int suggestNextMove(State state) {
    return minimax(
            0, 0, state, state.getNextMove(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
        .getKey();
  }
}
