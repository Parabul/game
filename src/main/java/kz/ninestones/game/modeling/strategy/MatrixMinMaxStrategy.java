package kz.ninestones.game.modeling.strategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.modeling.evaluation.StateEvaluator;

public class MatrixMinMaxStrategy implements Strategy {
  private final StateEvaluator stateEvaluator;
  public MatrixMinMaxStrategy(StateEvaluator stateEvaluator) {
    this.stateEvaluator = stateEvaluator;
  }

  @Override
  public int suggestNextMove(State state) {
    double[][] outcomes = new double[9][9];

    Player player = state.nextMove;

    for (int firstMove = 0; firstMove < 9; firstMove++) {
      if (!Policy.isAllowedMove(state, firstMove + 1)) {
        Arrays.fill(outcomes[firstMove], -1);
        continue;
      }

      State levelOneState = Policy.makeMove(state, firstMove + 1);

      Optional<Player> gameOver = Policy.isGameOver(levelOneState);

      if (gameOver.isPresent()) {
        Arrays.fill(outcomes[firstMove], gameOver.get().equals(player) ? 1 : 0);
        continue;
      }

      for (int secondMove = 0; secondMove < 9; secondMove++) {
        if (!Policy.isAllowedMove(levelOneState, secondMove + 1)) {
          outcomes[firstMove][secondMove] = -1;
          continue;
        }

        State levelTwoState = Policy.makeMove(levelOneState, secondMove + 1);
        outcomes[firstMove][secondMove] = stateEvaluator.evaluate(levelTwoState, player);
      }
    }

    Map<Integer, Double> minimumOutcomes = new HashMap<>();

    for (int firstMove = 0; firstMove < 9; firstMove++) {
      OptionalDouble minOutcome = Arrays.stream(outcomes[firstMove])
          .filter(outcome -> outcome != -1).min();

      if (minOutcome.isPresent()) {
        minimumOutcomes.put(firstMove + 1, minOutcome.getAsDouble());
      }
    }

    return ModelUtils.anyMaximizingKey(minimumOutcomes);
  }
}
