package kz.ninestones.game.model;

import kz.ninestones.game.core.State;

public interface Model {

  int suggestNextMove(State state);
}
