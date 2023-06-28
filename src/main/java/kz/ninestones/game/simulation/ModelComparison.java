package kz.ninestones.game.simulation;

import com.google.common.collect.ImmutableList;

import java.util.List;

import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.RecordedGame;
import kz.ninestones.game.modeling.strategy.Strategies;
import kz.ninestones.game.modeling.strategy.Strategy;

public class ModelComparison {

  private static final int ITERATIONS = 1000;

  public static void main(String[] args) {
    List<Strategy> strategies = ImmutableList.of(Strategies.RANDOM, Strategies.FIRST_ALLOWED_MOVE, Strategies.MIN_MAX_SCORE_DIFF);
    int n = strategies.size();

    double[][] comparisons = new double[n][n];

    for (int i = 0; i < n; i++) {
      System.out.println();
      for (int j = 0; j < n; j++) {
        Strategy playerOneStrategy = strategies.get(i);
        Strategy playerTwoStrategy = strategies.get(j);
        comparisons[i][j] = compare(playerOneStrategy, playerTwoStrategy);
        System.out.printf("| %.2f ", comparisons[i][j]);
      }
    }
  }


  public static double compare(Strategy playerOneStrategy, Strategy playerTwoStrategy) {

    GameSimulator simulator = new GameSimulator(playerOneStrategy, playerTwoStrategy);

    int playerOneWon = 0;
//    int playerTwoWon = 0;
//    int totalSteps = 0;

//    BloomFilter<State> bloomFilter = BloomFilter.create(StateFunnel.INSTANCE, 20000000, 0.0001);

    for (int i = 0; i < ITERATIONS; i++) {
      RecordedGame recordedGame = simulator.recordedPlayOut();

      if (Player.ONE.equals(recordedGame.getWinner())) {
        playerOneWon++;
//      } else if (Player.TWO.equals(recordedGame.getWinner())) {
//        playerTwoWon++;
      }

//      totalSteps += recordedGame.getSteps().size();
//      recordedGame.getSteps().forEach(bloomFilter::put);
    }

//    System.out.println("Score " + playerOneWon + " : " + playerTwoWon);
//    System.out.println("Draws " + (ITERATIONS - playerOneWon - playerTwoWon));
//
//    System.out.println("Approximate unique states per game: "
//        + 1.0 * bloomFilter.approximateElementCount() / times);
//    System.out.println("Average # of steps per game: " + 1.0 * totalSteps / times);

    return 1.0 * playerOneWon / ITERATIONS;
  }
}

//    0 -> RANDOM
//    1 -> MIN_MAX_SCORE_DIFF
//    2 -> NEURAL_NET_2
//    3 -> NEURAL_NET_3
//    4 -> LATEST
//
//    | 0.53 | 0.00 | 0.00 | 0.00 | 0.00
//    | 1.00 | 0.64 | 0.32 | 0.37 | 0.37
//    | 1.00 | 0.83 | 0.00 | 0.01 | 0.00
//    | 1.00 | 0.73 | 1.00 | 0.00 | 0.00
//    | 1.00 | 0.93 | 0.00 | 1.00 | 0.51