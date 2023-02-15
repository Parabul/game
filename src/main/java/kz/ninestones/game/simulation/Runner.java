package kz.ninestones.game.simulation;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.BloomFilter;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.model.RandomModel;

public class Runner {


  public static void main(String[] args) {

    SimulateGame simulator = new SimulateGame(
        ImmutableMap.of(Player.ONE, new RandomModel(), Player.TWO, new RandomModel()));

    BloomFilter<State> bloomFilter = BloomFilter.create(State.stateFunnel, 1000000, 0.0001);

    Stopwatch watch = Stopwatch.createStarted();

    SimulationResult result = simulator.simulate(bloomFilter);

    watch.stop();

    System.out.println("approxStates: " + bloomFilter.approximateElementCount());

    System.out.println(result);
  }
}
