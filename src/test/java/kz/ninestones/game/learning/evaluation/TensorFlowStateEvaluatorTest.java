package kz.ninestones.game.learning.evaluation;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import org.junit.Test;

public class TensorFlowStateEvaluatorTest {

  @Test
  public void shouldEvaluateDefaultState() {
    StateEvaluator stateEvaluator = new TensorFlowStateEvaluator();
    assertThat(stateEvaluator.evaluate(new State(), Player.ONE)).isWithin(0.01).of(0.466);

    assertThat(stateEvaluator.evaluate(new State(), Player.TWO)).isWithin(0.01).of(0.533);
  }

  @Test
  public void evaluateGameOver() {
    State state =
        new State(
            ImmutableMap.of(0, 1, 1, 2, 2, 3),
            ImmutableMap.of(Player.ONE, 82, Player.TWO, 0),
            ImmutableMap.of(Player.ONE, 12),
            Player.ONE);


    StateEvaluator stateEvaluator = new TensorFlowStateEvaluator();

    assertThat(stateEvaluator.evaluate(state, Player.ONE)).isWithin(0.01).of(1.01);
    assertThat(stateEvaluator.evaluate(state, Player.TWO)).isWithin(0.01).of(0.00);
  }

  @Test
  public void evaluateNegativeDiff() {
    State state =
        new State(
            ImmutableMap.of(0, 10, 2, 10, 3, 10, 12, 10, 17, 10),
            ImmutableMap.of(Player.ONE, 10, Player.TWO, 70),
            ImmutableMap.of(),
            Player.TWO);


    StateEvaluator stateEvaluator = new TensorFlowStateEvaluator();

    assertThat(stateEvaluator.evaluate(state, Player.ONE)).isWithin(0.01).of(0.06);
    assertThat(stateEvaluator.evaluate(state, Player.TWO)).isWithin(0.01).of(0.94);
  }
}
