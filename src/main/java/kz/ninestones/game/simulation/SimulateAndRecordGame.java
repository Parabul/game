package kz.ninestones.game.simulation;

import com.google.common.collect.ImmutableList;
import java.util.EnumMap;
import java.util.Map;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.model.Model;

public class SimulateAndRecordGame {

  private final EnumMap<Player, Model> models;

  public SimulateAndRecordGame(Map<Player, Model> models) {
    this.models = new EnumMap<>(models);
  }

  public RecordedGame record() {

    ImmutableList.Builder<State> states = ImmutableList.builder();

    State state = new State();

    while (!Policy.isGameOver(state).isPresent()) {

      states.add(state);

      int move = models.get(state.nextMove).suggestNextMove(state);

      state = Policy.makeMove(state, move);
    }

    return new RecordedGame(Policy.isGameOver(state).get(), states.build());
  }
}
