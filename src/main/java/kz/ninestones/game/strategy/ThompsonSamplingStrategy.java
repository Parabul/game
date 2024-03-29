package kz.ninestones.game.strategy;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.evaluation.BatchTensorFlowStateEvaluator;
import org.apache.beam.vendor.guava.v32_1_2_jre.com.google.common.collect.Maps;
import org.apache.commons.math3.distribution.BetaDistribution;

public class ThompsonSamplingStrategy implements Strategy {

    private final BatchTensorFlowStateEvaluator stateEvaluator;

    public ThompsonSamplingStrategy() {
        this.stateEvaluator = new BatchTensorFlowStateEvaluator();
    }


    @Override
    public int suggestNextMove(State state) {
        Map<Integer, State> childStates = IntStream.rangeClosed(1, 9).filter(move -> Policy.isAllowedMove(state, move)).boxed().collect(Collectors.toMap(Function.identity(), move -> Policy.makeMove(state, move)));


        final Player player = state.getNextMove();


        for (Map.Entry<Integer, State> childState:childStates.entrySet()) {
            if (Policy.isGameOver(childState.getValue())) {
                Player winner = Policy.winnerOf(childState.getValue()).get();
                if (winner.equals(player)) {
                    return childState.getKey();
                }
            }
        }



        Map<String, Double> outcomes = stateEvaluator.evaluate(new ArrayList<>(childStates.values()), player);

        Map<Integer, Double> moves = Maps.transformValues(childStates, childState -> outcomes.get(childState.getId()));

        Map<Integer, Double> alternatives = Maps.transformValues(moves, outcome -> new BetaDistribution(30 * outcome, 30 * (1 - outcome)).sample());

        return Collections.max(alternatives.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}
