package kz.ninestones.game.learning.montecarlo;

import java.util.EnumMap;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.MathUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class StateNode {
  private final State state;
  private final EnumMap<Player, Integer> observedOutcomes = new EnumMap(Player.class);

  public StateNode(State state) {
    this.state = state;
    for (Player player : Player.values()) {
      observedOutcomes.put(player, 0);
    }
  }

  public State getState() {
    return state;
  }

  public EnumMap<Player, Integer> getObservedOutcomes() {
    return observedOutcomes;
  }

  public void update(SimulationResult simulationResult) {
    for (Player player : Player.values()) {
      observedOutcomes.put(
          player, observedOutcomes.get(player) + simulationResult.getObservedWinners().get(player));
    }
  }

  public long getSimulations() {
    return observedOutcomes.values().stream().collect(Collectors.summingInt(Integer::intValue));
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("state", state)
            .add("observedOutcomes", observedOutcomes)
            .toString();
  }
}
