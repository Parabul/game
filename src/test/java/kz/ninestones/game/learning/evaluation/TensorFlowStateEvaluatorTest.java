package kz.ninestones.game.learning.evaluation;

import static com.google.common.truth.Truth.assertThat;

import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import org.junit.Test;

public class TensorFlowStateEvaluatorTest {
  //
  @Test
  public void shouldEvaluateDefaultState() {
    TensorFlowStateEvaluator stateEvaluator = new TensorFlowStateEvaluator();
    assertThat(stateEvaluator.evaluate(new State(), Player.ONE)).isWithin(1).of(0.5);

    assertThat(stateEvaluator.evaluate(new State(), Player.TWO)).isWithin(1).of(0.5);
  }
}
