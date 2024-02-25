package kz.ninestones.game.strategy;

import kz.ninestones.game.learning.evaluation.ScoreDiffStateEvaluator;
import kz.ninestones.game.learning.evaluation.TensorFlowStateEvaluator;

public class Strategies {

  public static final Strategy RANDOM = new RandomMoveStrategy();
  public static final Strategy FIRST_ALLOWED_MOVE = new FirstAllowedMoveStrategy();
  public static final Strategy MINIMAX_SCORE_DIFF =
      new MatrixMinMaxStrategy(new ScoreDiffStateEvaluator());

  public static final Strategy MINIMAX_TF =
      new MatrixMinMaxStrategy(new TensorFlowStateEvaluator());

  public static final Strategy MINIMAX_TF_CANDIDATE =
      new MatrixMinMaxStrategy(
          new TensorFlowStateEvaluator("/home/anarbek/tmp/models/v4/tf", /* direct= */ false));

  public static final Strategy MINIMAX_TF_DIRECT =
      new MatrixMinMaxStrategy(
          new TensorFlowStateEvaluator("/var/shared_disk/models/random/v2", /* direct= */ true));

  public static final Strategy MINIMAX_TF_V5_DIRECT=
          new MatrixMinMaxStrategy(
                  new TensorFlowStateEvaluator("/home/anarbek/tmp/models/v5/direct/tf", /* direct= */ true));
  public static final Strategy MINIMAX_TF_V5_EXP=
          new MatrixMinMaxStrategy(
                  new TensorFlowStateEvaluator("/home/anarbek/tmp/models/v5/experimental/tf", /* direct= */ false));


}
