package kz.ninestones.game.simulation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.RecordedGame;
import kz.ninestones.game.core.State;
import kz.ninestones.game.strategy.RandomMoveStrategy;
import kz.ninestones.game.strategy.Strategies;
import kz.ninestones.game.strategy.Strategy;

public class GameSimulator {

  public static final GameSimulator RANDOM =
      new GameSimulator(Strategies.RANDOM, Strategies.RANDOM);

  public static final GameSimulator MINIMAX =
      new GameSimulator(Strategies.MINIMAX_SCORE_DIFF, Strategies.MINIMAX_SCORE_DIFF);

  public static final GameSimulator DETEMINISTIC =
      new GameSimulator(Strategies.FIRST_ALLOWED_MOVE, Strategies.FIRST_ALLOWED_MOVE);

  private final EnumMap<Player, Strategy> models;

  public GameSimulator(Strategy playerOneStrategy, Strategy playerTwoStrategy) {
    this.models =
        new EnumMap<>(
            ImmutableMap.of(Player.ONE, playerOneStrategy, Player.TWO, playerTwoStrategy));
  }

  public static State randomState() {
    State state = new State();
    RandomMoveStrategy moveStrategy = new RandomMoveStrategy();

    int steps = ThreadLocalRandom.current().nextInt(10);

    for (int i = 0; i < steps; i++) {
      int move = moveStrategy.suggestNextMove(state);
      state = Policy.makeMove(state, move);
    }
    return state;
  }

  public RecordedGame recordedPlayOut() {
    return recordedPlayOut(new State());
  }

  public RecordedGame recordedPlayOut(State state) {

    ImmutableList.Builder<State> states = ImmutableList.builder();

    while (!Policy.isGameOver(state)) {

      states.add(state);

      int move = models.get(state.getNextMove()).suggestNextMove(state);

      state = Policy.makeMove(state, move);
    }

    return new RecordedGame(Policy.winnerOf(state).orElse(null), states.build());
  }

  public Player playOut() {
    return playOut(new State());
  }

  public Player playOut(State state) {

    while (!Policy.isGameOver(state)) {

      int move = models.get(state.getNextMove()).suggestNextMove(state);

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
