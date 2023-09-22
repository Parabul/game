package kz.ninestones.game.modeling.strategy;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.truth.Truth;
import java.util.Map;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.evaluation.ScoreDiffStateEvaluator;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.utils.ModelUtils;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;

public class RecursiveMinMaxTest {

  @Test
  public void samplesFromTwo() {
    RecursiveMinMax recursiveMinMax = new RecursiveMinMax(new ScoreDiffStateEvaluator(), 2);
    MatrixMinMaxStrategy matrixMinMax = new MatrixMinMaxStrategy(new ScoreDiffStateEvaluator());

    for (int i = 0; i < 100; i++) {
      State state = GameSimulator.randomState();

      Pair<Integer, Double> rec =
          recursiveMinMax.minimax(
              0, 0, state, state.nextMove, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

      Map<Integer, Double> alts = matrixMinMax.alternatives(state);

      int opt = ModelUtils.anyMaximizingKey(alts);

      Truth.assertThat(alts.get(opt)).isEqualTo(rec.getValue());

      Truth.assertThat(matrixMinMax.alternatives(state))
          .containsEntry(rec.getKey(), rec.getValue());

      System.out.println(alts);
      System.out.println(rec);

      Truth.assertThat(matrixMinMax.suggestNextMove(state))
          .isEqualTo(recursiveMinMax.suggestNextMove(state));
    }
  }
}
