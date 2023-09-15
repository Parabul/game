package kz.ninestones.game.simulation;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.modeling.strategy.Strategies;
import org.junit.Test;

public class GameSimulatorTest {
  @Test
  public void fixedStrategySimulations() {
    GameSimulator simulator =
        new GameSimulator(Strategies.FIRST_ALLOWED_MOVE, Strategies.FIRST_ALLOWED_MOVE);

    assertThat(simulator.playOut()).isEqualTo(Player.TWO);

    assertThat(simulator.recordedPlayOut().getWinner()).isEqualTo(Player.TWO);
    assertThat(simulator.recordedPlayOut().getSteps()).hasSize(208);

    assertThat(simulator.playOut(100).getObservedWinners().asMap())
        .containsExactly(Player.TWO, 100L);
  }

  @Test
  public void randomStrategySimulations() {
    int playOuts = 10000;

    GameSimulator simulator = new GameSimulator(Strategies.RANDOM, Strategies.RANDOM);

    Map<Player, Long> winners = simulator.playOut(playOuts).getObservedWinners().asMap();

    assertThat(winners).containsKey(Player.ONE);
    assertThat(winners).containsKey(Player.TWO);
    assertThat(winners).containsKey(Player.NONE);

    assertThat(1.0 * winners.get(Player.ONE) / playOuts).isWithin(0.02).of(0.54);
    assertThat(1.0 * winners.get(Player.TWO) / playOuts).isWithin(0.02).of(0.45);
    assertThat(1.0 * winners.get(Player.NONE) / playOuts).isWithin(0.02).of(0.0);
  }

  @Test
  public void playOutsOnGameOver() {
    State state =
        new State(
            ImmutableMap.of(11, 7),
            ImmutableMap.of(Player.ONE, 0, Player.TWO, 0),
            ImmutableMap.of(Player.ONE, 10),
            Player.ONE);

    assertThat(Policy.isGameOver(state)).isTrue();
    assertThat(Policy.winnerOf(state).get()).isEqualTo(Player.TWO);

    int playOuts = 100;

    GameSimulator simulator = new GameSimulator(Strategies.RANDOM, Strategies.RANDOM);

    Map<Player, Long> winners = simulator.playOut(state, playOuts).getObservedWinners().asMap();

    assertThat(winners).containsExactly(Player.TWO, 100L);
  }
}
