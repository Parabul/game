package kz.ninestones.game.modeling.strategy;

import kz.ninestones.game.learning.evaluation.ScoreDiffStateEvaluator;
import kz.ninestones.game.learning.evaluation.TensorFlowStateEvaluator;

public class Strategies {

  public static final Strategy RANDOM = new RandomMoveStrategy();
  public static final Strategy FIRST_ALLOWED_MOVE = new FirstAllowedMoveStrategy();
  public static final Strategy MIN_MAX_SCORE_DIFF =
      new MatrixMinMaxStrategy(new ScoreDiffStateEvaluator());

  public static final Strategy DEEP_MIN_MAX_SCORE_DIFF =
      new RecursiveMinMax(new ScoreDiffStateEvaluator(), 4);

  public static final Strategy TENSOR_FLOW = // new MaxStrategy(new ScoreDiffStateEvaluator());
          new RecursiveMinMax(new TensorFlowStateEvaluator(), 4);
}
