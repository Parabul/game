package kz.ninestones.game.learning.evaluation;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import org.junit.Test;

public class ScoreDiffStateEvaluatorTest {

  @Test
  public void evaluateZeroDiff() {

    ScoreDiffStateEvaluator stateEvaluator = new ScoreDiffStateEvaluator();

    assertThat(stateEvaluator.evaluate(new State(), Player.ONE)).isWithin(0.001).of(0.5);
    assertThat(stateEvaluator.evaluate(new State(), Player.TWO)).isWithin(0.001).of(0.5);
  }

  @Test
  public void evaluateMaxDiff() {
    State state =
        new State(
            ImmutableMap.of(0, 1, 1, 2, 2, 3),
            ImmutableMap.of(Player.ONE, 82, Player.TWO, 0),
            ImmutableMap.of(Player.ONE, 12),
            Player.ONE);

    ScoreDiffStateEvaluator stateEvaluator = new ScoreDiffStateEvaluator();

    assertThat(stateEvaluator.evaluate(state, Player.ONE)).isWithin(0.001).of(1);
    assertThat(stateEvaluator.evaluate(state, Player.TWO)).isWithin(0.001).of(0);
  }

  @Test
  public void evaluateNegativeDiff() {
    State state =
        new State(
            ImmutableMap.of(0, 1, 1, 2, 2, 3),
            ImmutableMap.of(Player.ONE, 10, Player.TWO, 50),
            ImmutableMap.of(Player.ONE, 12),
            Player.ONE);

    ScoreDiffStateEvaluator stateEvaluator = new ScoreDiffStateEvaluator();

    assertThat(stateEvaluator.evaluate(state, Player.ONE)).isWithin(0.001).of(0.253);
    assertThat(stateEvaluator.evaluate(state, Player.TWO)).isWithin(0.001).of(0.747);
  }
}
