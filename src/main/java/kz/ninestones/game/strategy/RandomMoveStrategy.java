package kz.ninestones.game.strategy;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;

public class RandomMoveStrategy implements Strategy {

  @Override
  public int suggestNextMove(final State state) {
    int[] moves =
        IntStream.rangeClosed(1, 9).filter(move -> Policy.isAllowedMove(state, move)).toArray();

    return moves[ThreadLocalRandom.current().nextInt(moves.length)];
  }
}
