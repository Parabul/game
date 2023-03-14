package kz.ninestones.game.modeling.strategy;

import kz.ninestones.game.core.State;

public interface Strategy {

  int suggestNextMove(State state);
}
