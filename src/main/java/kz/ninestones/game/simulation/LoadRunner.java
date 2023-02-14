package kz.ninestones.game.simulation;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.model.RandomModel;

public class LoadRunner {


  public static void main(String[] args) {

    SimulateGame simulator = new SimulateGame(
        ImmutableMap.of(Player.ONE, new RandomModel(), Player.TWO, new RandomModel()));

    int playerOneWon = 0;
    int playerTwoWon = 0;

    Stopwatch watch = Stopwatch.createStarted();

    for (int i = 0; i < 1000000; i++) {
      SimulationResult result = simulator.simulate();

      if (result.getWinner().equals(Player.ONE)) {
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
