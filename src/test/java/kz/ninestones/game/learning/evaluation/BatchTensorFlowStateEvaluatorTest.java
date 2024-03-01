package kz.ninestones.game.learning.evaluation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class BatchTensorFlowStateEvaluatorTest {

    @Test
    public void shouldEvaluateDefaultState() {
        State state = new State();
        BatchTensorFlowStateEvaluator stateEvaluator = new BatchTensorFlowStateEvaluator();
        assertThat(stateEvaluator.evaluate(ImmutableList.of(state), Player.ONE)).containsExactly(state.getId(), 0.567307710647583);
        assertThat(stateEvaluator.evaluate(ImmutableList.of(state), Player.TWO)).containsExactly(state.getId(), 0.43269217014312744);

    }

    @Test
    public void shouldEvaluateManyStates() {
        State gameOverState = new State(ImmutableMap.of(0, 1, 1, 2, 2, 3), ImmutableMap.of(Player.ONE, 82, Player.TWO, 0), ImmutableMap.of(Player.ONE, 12), Player.ONE);
        State defaultState = new State();

        BatchTensorFlowStateEvaluator stateEvaluator = new BatchTensorFlowStateEvaluator();
        assertThat(stateEvaluator.evaluate(ImmutableList.of(defaultState, gameOverState), Player.ONE)).containsExactly(defaultState.getId(), 0.567307710647583, gameOverState.getId(), 1.01);
    }


}