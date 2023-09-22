package kz.ninestones.game.modeling;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.evaluation.StateEvaluator;
import kz.ninestones.game.learning.evaluation.TensforFlowStateEvaluator;
import org.junit.Test;

public class TensorFlowStateEvaluatorTest {

  @Test
  public void evaluateZeroDiff() {

    StateEvaluator stateEvaluator = new TensforFlowStateEvaluator();

    assertThat(stateEvaluator.evaluate(new State(), Player.ONE)).isWithin(0.1).of(0.59);
    assertThat(stateEvaluator.evaluate(new State(), Player.TWO)).isWithin(0.1).of(0.41);
  }

  @Test
  public void evaluateMaxDiff() {
    State state =
        new State(
            ImmutableMap.of(0, 1, 1, 2, 2, 3),
            ImmutableMap.of(Player.ONE, 82, Player.TWO, 0),
            ImmutableMap.of(Player.ONE, 12),
            Player.ONE);

    StateEvaluator stateEvaluator = new TensforFlowStateEvaluator();

    assertThat(stateEvaluator.evaluate(state, Player.ONE)).isWithin(0.001).of(0.999);
    assertThat(stateEvaluator.evaluate(state, Player.TWO)).isWithin(0.001).of(0.001);
  }

  @Test
  public void evaluateNegativeDiff() {
    State state =
        new State(
            ImmutableMap.of(0, 1, 1, 2, 2, 3),
            ImmutableMap.of(Player.ONE, 10, Player.TWO, 50),
            ImmutableMap.of(Player.ONE, 12),
            Player.ONE);

    StateEvaluator stateEvaluator = new TensforFlowStateEvaluator();

    assertThat(stateEvaluator.evaluate(state, Player.ONE)).isWithin(0.1).of(0.05);
    assertThat(stateEvaluator.evaluate(state, Player.TWO)).isWithin(0.1).of(0.95);
  }
}
