package kz.ninestones.game.strategy;

import kz.ninestones.game.core.State;

import java.io.Serializable;

public interface Strategy extends Serializable {

  int suggestNextMove(State state);
}
