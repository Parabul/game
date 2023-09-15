package kz.ninestones.game.modeling.strategy;

import kz.ninestones.game.modeling.evaluation.ScoreDiffStateEvaluator;
import kz.ninestones.game.modeling.evaluation.TensforFlowStateEvaluator;

public class Strategies {

  public static final Strategy RANDOM = new RandomMoveStrategy();
  public static final Strategy FIRST_ALLOWED_MOVE = new FirstAllowedMoveStrategy();
  public static final Strategy MIN_MAX_SCORE_DIFF =
      new MatrixMinMaxStrategy(new ScoreDiffStateEvaluator());

  public static final Strategy MAX_SCORE_DIFF = new MaxStrategy(new ScoreDiffStateEvaluator());

  public static final Strategy TENSOR_FLOW =
      new MatrixMinMaxStrategy(new TensforFlowStateEvaluator());
}
