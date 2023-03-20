package kz.ninestones.game.simulation;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.RecordedGame;
import kz.ninestones.game.modeling.strategy.Strategy;

public class ModelComparison {

  private static final int ITERATIONS = 100;

  public static void main(String[] args) {
    List<Strategy> strategies = Arrays.stream(KnownStrategy.values())
        .map(KnownStrategy::getStrategy).collect(Collectors.toList());
    int n = strategies.size();

    double[][] comparisons = new double[n][n];

    for (KnownStrategy knownStrategy: KnownStrategy.values()) {
      System.out.println(knownStrategy.ordinal() + " -> " + knownStrategy.name());
    }

    for (int i = 0; i < n; i++) {
      System.out.println();
      for (int j = 0; j < n; j++) {
        Strategy playerOneStrategy = strategies.get(i);
        Strategy playerTwoStrategy = strategies.get(j);
        comparisons[i][j] = compare(playerOneStrategy, playerTwoStrategy);
        System.out.print(String.format("| %.2f ", comparisons[i][j]));
      }
    }
  }


  public static double compare(Strategy playerOneStrategy, Strategy playerTwoStrategy) {

    GameSimulator simulator = new GameSimulator(
        ImmutableMap.of(Player.ONE, playerOneStrategy, Player.TWO, playerTwoStrategy));

    int playerOneWon = 0;
//    int playerTwoWon = 0;
//    int totalSteps = 0;

//    BloomFilter<State> bloomFilter = BloomFilter.create(StateFunnel.INSTANCE, 20000000, 0.0001);

    for (int i = 0; i < ITERATIONS; i++) {
      RecordedGame recordedGame = simulator.simulate();

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
