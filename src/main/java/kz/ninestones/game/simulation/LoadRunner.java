package kz.ninestones.game.simulation;

import com.google.common.base.Stopwatch;
import com.google.common.hash.BloomFilter;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.RecordedGame;
import kz.ninestones.game.core.State;
import kz.ninestones.game.core.State.StateFunnel;
import kz.ninestones.game.modeling.strategy.Strategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadRunner {
  private static final Logger logger = LoggerFactory.getLogger(LoadRunner.class);

  public static void main(String[] args) {
    run(10);
  }

  public static void run(int times) {

    GameSimulator simulator =
        new GameSimulator(Strategies.MIN_MAX_SCORE_DIFF, Strategies.TENSOR_FLOW);

    int playerOneWon = 0;
    int playerTwoWon = 0;
    int totalSteps = 0;

    BloomFilter<State> bloomFilter = BloomFilter.create(StateFunnel.INSTANCE, 20000000, 0.0001);

    Stopwatch watch = Stopwatch.createStarted();

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

    watch.stop();

    logger.info("Stopwatch: " + watch);
    logger.info("Score " + playerOneWon + " : " + playerTwoWon);
    logger.info("Draws " + (times - playerOneWon - playerTwoWon));

    logger.info(
        "Approximate unique states per game: "
            + 1.0 * bloomFilter.approximateElementCount() / times);
    logger.info("Average # of steps per game: " + 1.0 * totalSteps / times);
  }
}

//  maxModel vs minMaxModel (ScoreDiff)
//    1000
//
//    Stopwatch: 1.911 s
//    Score 13 : 987
//    approxStates: 60501
//    avgSteps: 68.068
//    -----
//    10000
//
//    Stopwatch: 6.607 s
//    Score 156 : 9844
//    approxStates: 566069
//    avgSteps: 68.573
//    -----
//    100000
//
//    Stopwatch: 1.090 min
//    Score 1650 : 98350
//    approxStates: 5258749
//    avgSteps: 68.42552

// Random vs Random
//    1000
//
//    Stopwatch: 850.9 ms
//    Score 552 : 448
//    approxStates: 121365
//    avgSteps: 124.887
//    -----
//    10000
//
//    Stopwatch: 2.690 s
//    Score 5416 : 4584
//    approxStates: 1206957
//    avgSteps: 125.3069
//    -----
//    100000
//
//    Stopwatch: 20.74 s
//    Score 54504 : 45496
//    approxStates: 11894258
//    avgSteps: 124.67691

// NN vs ScoreDiff MiniMax
// Stopwatch: 3.323 s
//    Score 0 : 1
//    approxStates: 130
//    avgSteps: 130.0
//    -----
//    10
//    init complete
//
//    Stopwatch: 14.11 s
//    Score 3 : 7
//    approxStates: 1566
//    avgSteps: 164.5
//    -----
//    100
//    init complete
//
//    Stopwatch: 2.628 min
//    Score 30 : 70
//    approxStates: 16904
//    avgSteps: 182.91
//
//    Process finished with exit code 0
