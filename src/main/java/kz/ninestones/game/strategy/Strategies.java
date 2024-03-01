package kz.ninestones.game.strategy;

import kz.ninestones.game.learning.evaluation.ScoreDiffStateEvaluator;
import kz.ninestones.game.learning.evaluation.TensorFlowStateEvaluator;

public class Strategies {

  public static final Strategy RANDOM = new RandomMoveStrategy();
  public static final Strategy FIRST_ALLOWED_MOVE = new FirstAllowedMoveStrategy();
  public static final Strategy MINIMAX_SCORE_DIFF =
      new RecursiveMinMax(new ScoreDiffStateEvaluator(),2 );

  public static final Strategy MINIMAX_TF =
      new MatrixMinMaxStrategy(new TensorFlowStateEvaluator());

  public static final Strategy BATCH_MINIMAX_TF =
          new BatchMinMaxStrategy();


  public static final Strategy MINIMAX_TF_CANDIDATE =
      new MatrixMinMaxStrategy(
          new TensorFlowStateEvaluator("/home/anarbek/tmp/models/v5/direct/tf", /* direct= */ true));

  public static final Strategy MINIMAX_TF_CANDIDATE_2 =
      new RecursiveMinMax(
          new TensorFlowStateEvaluator("/var/shared_disk/models/v8/direct/tf", /* direct= */ true),2);


}
