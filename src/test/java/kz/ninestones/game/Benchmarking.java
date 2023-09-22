package kz.ninestones.game;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kz.ninestones.game.learning.evaluation.TensforFlowStateEvaluator;
import kz.ninestones.game.learning.montecarlo.TreeSearch;
import kz.ninestones.game.modeling.strategy.SampledMoveStrategy;
import kz.ninestones.game.modeling.strategy.Strategy;
import kz.ninestones.game.simulation.GameSimulator;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

public class Benchmarking {

  public static void main(String[] args) throws Exception {
    Main.main(args);
  }

  @Fork(value = 1, warmups = 2)
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

    public TreeSearch treeSearch;
    private Strategy strategy;
    private List<kz.ninestones.game.core.State> gameStates;

    @Setup(Level.Trial)
    public void setUp() {
      //      treeSearch = new TreeSearch(new MinMaxMonteCarloPlayOutSimulator());
      //      this.strategy = new MatrixMinMaxStrategy(new ScoreDiffStateEvaluator());
      //      this.strategy = new RecursiveMinMax(new ScoreDiffStateEvaluator());
      this.strategy = new SampledMoveStrategy(new TensforFlowStateEvaluator());
      this.gameStates =
          IntStream.rangeClosed(1, 100)
              .mapToObj(i -> GameSimulator.randomState())
              .collect(Collectors.toList());
    }
  }
}
