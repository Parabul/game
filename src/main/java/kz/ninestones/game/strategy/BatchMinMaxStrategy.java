package kz.ninestones.game.strategy;

import java.util.*;
import java.util.stream.Collectors;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.evaluation.BatchTensorFlowStateEvaluator;
import kz.ninestones.game.utils.ModelUtils;

public class BatchMinMaxStrategy implements Strategy {

    private final BatchTensorFlowStateEvaluator stateEvaluator;

    public BatchMinMaxStrategy() {
        this.stateEvaluator = new BatchTensorFlowStateEvaluator();
    }


    @Override
    public int suggestNextMove(State state) {
        State[][] states = new State[9][9];

        final Player player = state.getNextMove();

        boolean hasNonDeterministicMove = false;

        for (int firstMove = 1; firstMove <= 9; firstMove++) {
            if (!Policy.isAllowedMove(state, firstMove)) {
                continue;
            }

            State levelOneState = Policy.makeMove(state, firstMove);

            if (Policy.isGameOver(levelOneState)) {
                Player winner = Policy.winnerOf(levelOneState).get();
                if (winner.equals(player)) {
                    return firstMove;
                }
                continue;
            }

            hasNonDeterministicMove = true;

            for (int secondMove = 1; secondMove <= 9; secondMove++) {
                if (!Policy.isAllowedMove(levelOneState, secondMove)) {
                    continue;
                }

                State levelTwoState = Policy.makeMove(levelOneState, secondMove);
                states[firstMove - 1][secondMove - 1] = levelTwoState;
            }
        }

        if (!hasNonDeterministicMove) {
            return Strategies.RANDOM.suggestNextMove(state);
        }

        List<State> reachableStates = Arrays.stream(states).flatMap(Arrays::stream).filter(Objects::nonNull).collect(Collectors.toList());

        Map<String, Double> outcomes = stateEvaluator.evaluate(reachableStates, player);

        Map<Integer, Double> minimumOutcomes = new HashMap<>();

        for (int firstMove = 1; firstMove <= 9; firstMove++) {
            OptionalDouble minOutcome = Arrays.stream(states[firstMove - 1]).filter(Objects::nonNull).mapToDouble(s -> outcomes.get(s.getId())).min();

            if (minOutcome.isPresent()) {
                minimumOutcomes.put(firstMove, minOutcome.getAsDouble());
            }
        }

        return ModelUtils.anyMaximizingKey(minimumOutcomes);
    }
}
