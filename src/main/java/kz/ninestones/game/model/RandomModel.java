package kz.ninestones.game.model;

import com.google.common.collect.ImmutableList;
import java.util.concurrent.ThreadLocalRandom;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;

public class RandomModel implements Model{

  @Override
  public int suggestNextMove(State state) {
    ImmutableList.Builder<Integer> allowedMovesBuilder = ImmutableList.builder();

    for (int move = 1; move < 10; move++) {
      if (Policy.isAllowedMove(state, move)) {
        allowedMovesBuilder.add(move);
      }
    }

    ImmutableList<Integer> allowedMoves = allowedMovesBuilder.build();

    int randomNum = ThreadLocalRandom.current().nextInt(0, allowedMoves.size());

    return allowedMoves.get(randomNum);
  }
}
