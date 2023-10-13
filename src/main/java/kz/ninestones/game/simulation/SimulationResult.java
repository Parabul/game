package kz.ninestones.game.simulation;

import java.util.EnumMap;
import kz.ninestones.game.core.Player;

public class SimulationResult {

  private final EnumMap<Player, Integer> observedWinners = new EnumMap<>(Player.class);

  public SimulationResult() {
    for (Player player : Player.values()) {
      observedWinners.put(player, 0);
    }
  }

  public void merge(SimulationResult simulationResult) {
    for (Player player : Player.values()) {
      observedWinners.put(
          player, observedWinners.get(player) + simulationResult.getObservedWinners().get(player));
    }
  }

  public void addWinner(Player player) {
    observedWinners.put(player, observedWinners.getOrDefault(player, 0) + 1);
  }

  public EnumMap<Player, Integer> getObservedWinners() {
    return observedWinners;
  }
}
