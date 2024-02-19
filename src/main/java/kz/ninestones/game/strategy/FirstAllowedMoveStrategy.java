package kz.ninestones.game.strategy;

import java.util.stream.IntStream;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;

public class FirstAllowedMoveStrategy implements Strategy {
  @Override
  public int suggestNextMove(State state) {
    return IntStream.rangeClosed(1, 9).filter(move -> Policy.isAllowedMove(state, move)).findFirst()
        .orElseThrow(() -> new IllegalStateException("No moves, game over!l"));
  }
}
