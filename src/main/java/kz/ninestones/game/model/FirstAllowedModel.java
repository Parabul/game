package kz.ninestones.game.model;

import com.google.common.collect.ImmutableList;
import java.util.concurrent.ThreadLocalRandom;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;

public class FirstAllowedModel implements Model{

  @Override
  public int suggestNextMove(State state) {
    ImmutableList.Builder<Integer> allowedMovesBuilder = ImmutableList.builder();

    for (int move = 1; move < 10; move++) {
      if (Policy.isAllowedMove(state, move)) {
        return move;
      }
    }

    throw new IllegalStateException("No moves, game over!l");
  }
}
