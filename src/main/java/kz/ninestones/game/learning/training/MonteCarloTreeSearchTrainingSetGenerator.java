package kz.ninestones.game.learning.training;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.function.Supplier;
import kz.ninestones.game.modeling.strategy.MonteCarloTreeNode;
import kz.ninestones.game.modeling.strategy.MonteCarloTreeSearch;
import kz.ninestones.game.persistence.ProtoFiles;
import kz.ninestones.game.simulation.DistributedMonteCarloPlayOutSimulator;

public class MonteCarloTreeSearchTrainingSetGenerator {

  public static final String RANDOM_TRAINING_DAT =
      "/home/anarbek/projects/ninestones/data/random/training.dat";
  public static final String RANDOM_TEST_DAT =
      "/home/anarbek/projects/ninestones/data/random/test.dat";
  public static final String MINIMAX_TRAINING_DAT =
      "/home/anarbek/projects/ninestones/data/minimax/small_training.dat";
  public static final String MINIMAX_TEST_DAT =
      "/home/anarbek/projects/ninestones/data/minimax/small_test.dat";
  private final Supplier<MonteCarloTreeSearch> monteCarloTreeSearchSupplier;

  public MonteCarloTreeSearchTrainingSetGenerator(
      Supplier<MonteCarloTreeSearch> monteCarloTreeSearchSupplier) {
    this.monteCarloTreeSearchSupplier = monteCarloTreeSearchSupplier;
  }

  public MonteCarloTreeSearchTrainingSetGenerator() {
    this(MonteCarloTreeSearch::new);
  }

  public static void main(String[] args) throws IOException {
//    MonteCarloTreeSearchTrainingSetGenerator generator =
//        new MonteCarloTreeSearchTrainingSetGenerator();
//
//    //    generator.generateGameSamples(500, RANDOM_TRAINING_DAT);
//    generator.generateGameSamples(50, RANDOM_TEST_DAT);

    MonteCarloTreeSearchTrainingSetGenerator generatorMinimax =
        new MonteCarloTreeSearchTrainingSetGenerator(
            () -> new MonteCarloTreeSearch(new DistributedMonteCarloPlayOutSimulator()));
    //
        generatorMinimax.generateGameSamples(500, MINIMAX_TRAINING_DAT);
        generatorMinimax.generateGameSamples(50, MINIMAX_TEST_DAT);
  }

  public void generateGameSamples(int expansions, String file) throws IOException {
    MonteCarloTreeSearch monteCarloTreeSearch = monteCarloTreeSearchSupplier.get();

    for (int i = 0; i < expansions; i++) {
      System.out.println("expansion: " + i);
      monteCarloTreeSearch.expand();
    }

    ProtoFiles.write(
        file, Lists.transform(monteCarloTreeSearch.traverse(), MonteCarloTreeNode::toGameSample));
  }
}
