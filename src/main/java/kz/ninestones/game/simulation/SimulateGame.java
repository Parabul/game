package kz.ninestones.game.simulation;

import java.util.EnumMap;
import java.util.Map;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.model.Model;

public class SimulateGame {

  private final EnumMap<Player, Model> models;

  public SimulateGame(Map<Player, Model> models) {
    this.models = new EnumMap(models);
  }

  public SimulationResult simulate() {

    State state = new State();

    int steps = 0;

    while (!Policy.isGameOver(state).isPresent()) {
      steps++;

      int move = models.get(state.nextMove).suggestNextMove(state);

      state = Policy.makeMove(state, move);
    }

    return new SimulationResult(Policy.isGameOver(state).get(), steps);
  }
}
