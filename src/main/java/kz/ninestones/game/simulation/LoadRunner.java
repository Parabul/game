package kz.ninestones.game.simulation;

import com.google.common.hash.BloomFilter;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.RecordedGame;
import kz.ninestones.game.core.State;
import kz.ninestones.game.core.State.StateFunnel;
import kz.ninestones.game.strategy.Strategies;
import kz.ninestones.game.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadRunner {
  private static final Logger logger = LoggerFactory.getLogger(LoadRunner.class);

  public static void main(String[] args) {
    run(100, Strategies.MINIMAX_TF_V5_DIRECT, Strategies.MINIMAX_SCORE_DIFF);
  }

  public static void run(int times, Strategy playerOneStrategy, Strategy playerTwoStrategy) {

    int playerOneWon = 0;
    int playerTwoWon = 0;
    int totalSteps = 0;

    BloomFilter<State> bloomFilter = BloomFilter.create(StateFunnel.INSTANCE, 20000000, 0.0001);

    GameSimulator simulator = new GameSimulator(playerOneStrategy, playerTwoStrategy);

    for (int i = 0; i < times; i++) {
      logger.info("step: " + i);
      RecordedGame recordedGame = simulator.recordedPlayOut(GameSimulator.randomState());

      if (Player.ONE.equals(recordedGame.getWinner())) {
        playerOneWon++;
      } else if (Player.TWO.equals(recordedGame.getWinner())) {
        playerTwoWon++;
      }

      totalSteps += recordedGame.getSteps().size();
      recordedGame.getSteps().forEach(bloomFilter::put);
    }

    logger.info("Score (as first only)" + playerOneWon + " : " + playerTwoWon);

    GameSimulator invertedSimulator = new GameSimulator(playerTwoStrategy, playerOneStrategy);

    for (int i = 0; i < times; i++) {
      logger.info("(inverted) step: " + i);
      RecordedGame recordedGame = invertedSimulator.recordedPlayOut(GameSimulator.randomState());

      if (Player.ONE.equals(recordedGame.getWinner())) {
        playerTwoWon++;
      } else if (Player.TWO.equals(recordedGame.getWinner())) {
        playerOneWon++;
      }

      totalSteps += recordedGame.getSteps().size();
      recordedGame.getSteps().forEach(bloomFilter::put);
    }

    logger.info("Score (total) {} : {}", playerOneWon, playerTwoWon);

    logger.info(
        "Approximate unique states per game: {}",
        1.0 * bloomFilter.approximateElementCount() / times);
    logger.info("Average # of steps per game: {}", 1.0 * totalSteps / times);

    double winningRate = 0.5 * playerOneWon / times;
    double marginOfError = 1.96 * Math.sqrt(winningRate * (1 - winningRate) / (2.0 * times));

    logger.info(
        "Win rate: [{}, {}]",
        String.format("%.2f", winningRate - marginOfError),
        String.format("%.2f", winningRate + marginOfError));
  }
}
