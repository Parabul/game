package kz.ninestones.game.simulation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.RecordedGame;
import kz.ninestones.game.core.State;
import kz.ninestones.game.modeling.strategy.Strategy;

public class GameSimulator {

  private final EnumMap<Player, Strategy> models;

  public GameSimulator(Strategy playerOneStrategy, Strategy playerTwoStrategy) {
    this(playerOneStrategy, playerTwoStrategy, new State());
  }

  public GameSimulator(Strategy playerOneStrategy, Strategy playerTwoStrategy, State initialState) {
    this.models =
        new EnumMap<>(
            ImmutableMap.of(Player.ONE, playerOneStrategy, Player.TWO, playerTwoStrategy));
  }

  public RecordedGame recordedPlayOut() {
    return recordedPlayOut(new State());
  }

  public RecordedGame recordedPlayOut(State state) {

    ImmutableList.Builder<State> states = ImmutableList.builder();

    while (!Policy.isGameOver(state)) {

      states.add(state);

      int move = models.get(state.nextMove).suggestNextMove(state);

      state = Policy.makeMove(state, move);
    }

    return new RecordedGame(Policy.winnerOf(state).orElse(null), states.build());
  }

  public Player playOut() {
    return playOut(new State());
  }

  public Player playOut(State state) {

    while (!Policy.isGameOver(state)) {

      int move = models.get(state.nextMove).suggestNextMove(state);

      state = Policy.makeMove(state, move);
    }

    return Policy.winnerOf(state).orElseThrow(IllegalStateException::new);
  }

  public SimulationResult playOut(long n) {
    return playOut(new State(), n);
  }

  public SimulationResult playOut(State state, long n) {
    SimulationResult simulationResult = new SimulationResult();

    for (int i = 0; i < n; i++) {
      simulationResult.addWinner(playOut(state));
    }

    return simulationResult;
  }
}
