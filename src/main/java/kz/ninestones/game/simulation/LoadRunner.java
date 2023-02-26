package kz.ninestones.game.simulation;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.BloomFilter;
import java.io.IOException;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.model.MaxModel;
import kz.ninestones.game.model.MinMaxModel;
import kz.ninestones.game.model.Model;
import kz.ninestones.game.model.NeuralNetStateEvaluator;
import kz.ninestones.game.model.ScoreDiffStateEvaluator;
import kz.ninestones.game.model.ScoreStateEvaluator;
import kz.ninestones.game.model.StateEvaluator;

public class LoadRunner {


  public static void main(String[] args) throws IOException {
    run(1);
    run(10);
    run(100);
//    run(100000);
//    run(1000000);
  }

  public static void run(int times) throws IOException {
    System.out.println("-----");
    System.out.println(times);

    StateEvaluator diffStateEvaluator = new ScoreDiffStateEvaluator();
    StateEvaluator scoreStateEvaluator = new ScoreStateEvaluator();
    StateEvaluator neuralNetStateEvaluator = new NeuralNetStateEvaluator();

    Model minMaxScore = new MinMaxModel(diffStateEvaluator);

    Model minMaxNet = new MinMaxModel(neuralNetStateEvaluator);

    SimulateGame simulator = new SimulateGame(
        ImmutableMap.of(Player.ONE, minMaxNet, Player.TWO, minMaxScore));

    int playerOneWon = 0;
    int playerTwoWon = 0;
    int totalSteps = 0;

    BloomFilter<State> bloomFilter = BloomFilter.create(State.stateFunnel, 20000000, 0.0001);

    System.out.println("init complete");
    Stopwatch watch = Stopwatch.createStarted();

    for (int i = 0; i < times; i++) {
      SimulationResult result = simulator.simulate(bloomFilter);

      if (result.getWinner().equals(Player.ONE)) {
        playerOneWon++;
      } else {
        playerTwoWon++;
      }

      totalSteps += result.getSteps();
    }

    watch.stop();

    System.out.println();
    System.out.println("Stopwatch: " + watch);
    System.out.println("Score " + playerOneWon + " : " + playerTwoWon);

    System.out.println("approxStates: " + bloomFilter.approximateElementCount());
    System.out.println("avgSteps: " + 1.0 * totalSteps / times);
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
