package kz.ninestones.game.strategy;

import java.io.Serializable;
import kz.ninestones.game.core.State;

public interface Strategy extends Serializable {

  int suggestNextMove(State state);
}
