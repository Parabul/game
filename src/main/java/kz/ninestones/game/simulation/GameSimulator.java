package kz.ninestones.game.simulation;

import com.google.common.collect.ImmutableList;
import java.util.EnumMap;
import java.util.Map;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.RecordedGame;
import kz.ninestones.game.core.State;
import kz.ninestones.game.modeling.strategy.Strategy;

public class GameSimulator {

  private final EnumMap<Player, Strategy> models;

  public GameSimulator(Map<Player, Strategy> models) {
    this.models = new EnumMap<>(models);
  }

  public RecordedGame simulate() {

    ImmutableList.Builder<State> states = ImmutableList.builder();

    State state = new State();

    while (!Policy.isGameOver(state)) {

      states.add(state);

      int move = models.get(state.nextMove).suggestNextMove(state);

      state = Policy.makeMove(state, move);
    }

    return new RecordedGame(Policy.winnerOf(state).orElse(null), states.build());
  }
}
