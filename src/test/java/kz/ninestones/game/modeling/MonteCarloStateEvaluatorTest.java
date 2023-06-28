package kz.ninestones.game.modeling;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.modeling.evaluation.MonteCartoStateEvaluator;
import kz.ninestones.game.modeling.evaluation.NeuralNetStateEvaluator;
import kz.ninestones.game.modeling.evaluation.StateEvaluator;
import org.junit.Test;

public class MonteCarloStateEvaluatorTest {

  @Test
  public void evaluateZeroDiff() throws IOException {

    StateEvaluator stateEvaluator = new MonteCartoStateEvaluator();

    assertThat(stateEvaluator.evaluate(new State(), Player.ONE)).isWithin(0.001).of(0.526);
    assertThat(stateEvaluator.evaluate(new State(), Player.TWO)).isWithin(0.001).of(0.473);
  }

  @Test
  public void evaluateMaxDiff() throws IOException {
    State state = new State(ImmutableMap.of(0, 1, 1, 2, 2, 3),
        ImmutableMap.of(Player.ONE, 82, Player.TWO, 0), ImmutableMap.of(Player.ONE, 12),
        Player.ONE);

    StateEvaluator stateEvaluator = new MonteCartoStateEvaluator();

    assertThat(stateEvaluator.evaluate(state, Player.ONE)).isWithin(0.001).of(0.999);
    assertThat(stateEvaluator.evaluate(state, Player.TWO)).isWithin(0.001).of(0.001);
  }

  @Test
  public void evaluateNegativeDiff() throws IOException {
    State state = new State(ImmutableMap.of(0, 1, 1, 2, 2, 3),
        ImmutableMap.of(Player.ONE, 10, Player.TWO, 50), ImmutableMap.of(Player.ONE, 12),
        Player.ONE);

    StateEvaluator stateEvaluator = new MonteCartoStateEvaluator();

    assertThat(stateEvaluator.evaluate(state, Player.ONE)).isWithin(0.001).of(0.089);
    assertThat(stateEvaluator.evaluate(state, Player.TWO)).isWithin(0.001).of(0.910);
  }
}
