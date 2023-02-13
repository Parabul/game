package kz.ninestones.game.core;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import java.util.concurrent.ThreadLocalRandom;

public class LoadRunner {


  public static void main(String[] args) {

    int playerOneWon = 0;
    int playerTwoWon = 0;

    Stopwatch watch = Stopwatch.createStarted();

    for (int i = 0; i < 1000000; i++) {

      State state = new State();
      while (!Policy.isGameOver(state).isPresent()) {

        ImmutableList.Builder<Integer> allowedMovesBuilder = ImmutableList.builder();

        for (int move = 1; move < 10; move++) {
          if (Policy.isAllowedMove(state, move)) {
            allowedMovesBuilder.add(move);
          }
        }

        ImmutableList<Integer> allowedMoves = allowedMovesBuilder.build();

        int randomNum = ThreadLocalRandom.current().nextInt(0, allowedMoves.size());

        int randomMove = allowedMoves.get(randomNum);

        state = Policy.makeMove(state, randomMove);
      }

      if (Policy.isGameOver(state).get().equals(Player.ONE)) {
        playerOneWon++;
      } else {
        playerTwoWon++;
      }
    }

    watch.stop();

    System.out.println();
    System.out.println("Stopwatch: " + watch);
    System.out.println("Score " + playerOneWon + " : " + playerTwoWon);
  }
}
