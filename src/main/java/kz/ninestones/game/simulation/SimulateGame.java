package kz.ninestones.game.simulation;

import com.google.common.hash.BloomFilter;
import java.util.EnumMap;
import java.util.Map;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.modeling.strategy.Strategy;

public class SimulateGame {

  private final EnumMap<Player, Strategy> models;

  public SimulateGame(Map<Player, Strategy> models) {
    this.models = new EnumMap<>(models);
  }

  public SimulationResult simulate(BloomFilter<State> bloomFilter) {

    State state = new State();

    int steps = 0;

    while (!Policy.isGameOver(state).isPresent()) {
      steps++;
      bloomFilter.put(state);

      int move = models.get(state.nextMove).suggestNextMove(state);

      state = Policy.makeMove(state, move);
    }

    return new SimulationResult(Policy.isGameOver(state).get(), steps);
  }
}
