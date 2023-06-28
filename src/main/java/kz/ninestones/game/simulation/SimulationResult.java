package kz.ninestones.game.simulation;

import com.google.common.util.concurrent.AtomicLongMap;
import kz.ninestones.game.core.Player;

public class SimulationResult {

  private final AtomicLongMap<Player> observedWinners = AtomicLongMap.create();

  public void addWinner(Player player) {
    observedWinners.incrementAndGet(player);
  }

  public AtomicLongMap<Player> getObservedWinners() {
    return observedWinners;
  }
}
