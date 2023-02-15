package kz.ninestones.game.model;

import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;

/**
 * Evaluates a state of the game and conditions favoring player ONE.
 */
public interface StateEvaluator {

  /**
   * Returns value in [0, 1] range, where "1" implies best state favoring a given player and "0"
   * implies state favoring their opponent.
   */
  double evaluate(State state, Player player);
}
