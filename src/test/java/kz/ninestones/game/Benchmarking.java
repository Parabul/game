package kz.ninestones.game;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.strategy.ThompsonSamplingStrategy;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

public class Benchmarking {

  public static void main(String[] args) throws Exception {
    Main.main(args);
  }

  @Fork(value = 2, warmups = 0)
  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  public void expand(ExecutionPlan executionPlan, Blackhole blackhole) {
    //    executionPlan.treeSearch.expand();
    List<Integer> moves =
        executionPlan.gameStates.stream()
            .map(executionPlan.strategy::suggestNextMove)
            .collect(Collectors.toList());

    blackhole.consume(moves);
  }

  @State(Scope.Benchmark)
  public static class ExecutionPlan {
    private ThompsonSamplingStrategy strategy;
    private List<kz.ninestones.game.core.State> gameStates;

    //        private FastMonteCarloTreeSearch treeSearch;

    @Setup(Level.Trial)
    public void setUp() {
      this.strategy = new ThompsonSamplingStrategy();
      this.gameStates =
          IntStream.rangeClosed(1, 100)
              .mapToObj(i -> GameSimulator.randomState())
              .collect(Collectors.toList());
    }
  }
}
