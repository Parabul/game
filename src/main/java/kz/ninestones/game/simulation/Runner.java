package kz.ninestones.game.simulation;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import java.util.concurrent.ThreadLocalRandom;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;

public class Runner {


  public static void main(String[] args) {

    State state = new State();

    int steps = 0;

    Stopwatch watch = Stopwatch.createStarted();

    while (!Policy.isGameOver(state).isPresent()) {
      steps++;
      //System.out.println(state);

      ImmutableList.Builder<Integer> allowedMovesBuilder = ImmutableList.builder();

      for (int move = 1; move < 10; move++) {
        if (Policy.isAllowedMove(state, move)) {
          allowedMovesBuilder.add(move);
        }
      }

      ImmutableList<Integer> allowedMoves = allowedMovesBuilder.build();

      int randomNum = ThreadLocalRandom.current().nextInt(0, allowedMoves.size());

      int randomMove = allowedMoves.get(randomNum);

      //System.out.println(state.nextMove + " -> " + randomMove);

      state = Policy.makeMove(state, randomMove);

      //System.out.println();
    }

    watch.stop();

    System.out.println();
    System.out.println("Stopwatch: " + watch);
    System.out.println(
        "Final state in " + steps + " steps. Won: " + Policy.isGameOver(state).get());
    System.out.println(state);
  }
}
