package kz.ninestones.game.modeling.strategy;

import kz.ninestones.game.modeling.evaluation.MonteCartoStateEvaluator;
import kz.ninestones.game.modeling.evaluation.NeuralNetStateEvaluator;
import kz.ninestones.game.modeling.evaluation.ScoreDiffStateEvaluator;

public class Strategies {

  public static final Strategy RANDOM = new RandomMoveStrategy();
  public static final Strategy FIRST_ALLOWED_MOVE = new FirstAllowedMoveStrategy();
  public static final Strategy MIN_MAX_SCORE_DIFF =
      new MatrixMinMaxStrategy(new ScoreDiffStateEvaluator());

  public static final Strategy NEURAL_NET_2 =
      new MatrixMinMaxStrategy(
          new NeuralNetStateEvaluator("/home/anarbek/projects/ninestones/models/second.model"));

  public static final Strategy NEURAL_NET_1 =
      new MatrixMinMaxStrategy(
          new NeuralNetStateEvaluator("/home/anarbek/projects/ninestones/models/first.model"));

  public static final Strategy LATEST =
      new MatrixMinMaxStrategy(
          new NeuralNetStateEvaluator("/home/anarbek/projects/ninestones/models/3.1.model"));

  public static final Strategy MONTE_CARLO =
      new MatrixMinMaxStrategy(new MonteCartoStateEvaluator());
}
