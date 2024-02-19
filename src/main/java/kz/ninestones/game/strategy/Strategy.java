package kz.ninestones.game.strategy;

import kz.ninestones.game.core.State;

public interface Strategy {

  int suggestNextMove(State state);
}
