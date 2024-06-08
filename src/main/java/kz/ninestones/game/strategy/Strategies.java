package kz.ninestones.game.strategy;

import kz.ninestones.game.learning.encode.CompactStateEncoder;
import kz.ninestones.game.learning.evaluation.ScoreDiffStateEvaluator;
import kz.ninestones.game.learning.evaluation.TensorFlowStateEvaluator;

public class Strategies {

  public static final Strategy RANDOM = new RandomMoveStrategy();
  public static final Strategy FIRST_ALLOWED_MOVE = new FirstAllowedMoveStrategy();
  public static final Strategy MINIMAX_SCORE_DIFF =
      new MatrixMinMaxStrategy(new ScoreDiffStateEvaluator());

  public static final Strategy BATCH_MINIMAX_SCORE_DIFF =
          new BatchMinMaxStrategy(new ScoreDiffStateEvaluator());


  public static final Strategy MINIMAX_TF =
      new RecursiveMinMax(new TensorFlowStateEvaluator(),2);

  public static final Strategy COMPACT_TF = new MatrixMinMaxStrategy(new TensorFlowStateEvaluator("/home/anarbek/tmp/models/compact_test/direct/tf",true,new CompactStateEncoder()));

  public static final Strategy BATCH_MINIMAX_TF = new BatchMinMaxStrategy();

  public static final Strategy MINIMAX_TF_CURRENT =
          new RecursiveMinMax(
                  new TensorFlowStateEvaluator("/var/shared_disk/iter/v7/model/direct/tf", /* direct= */ true), 2);

  public static final Strategy MINIMAX_TF_CANDIDATE_3 =
      new MatrixMinMaxStrategy(
          new TensorFlowStateEvaluator("/var/shared_disk/models/v9/direct/tf", /* direct= */ true));
}
