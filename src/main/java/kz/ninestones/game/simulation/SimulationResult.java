package kz.ninestones.game.simulation;

import com.google.common.util.concurrent.AtomicLongMap;
import com.google.mu.util.stream.BiStream;
import kz.ninestones.game.core.Player;

import java.util.Map;
import java.util.stream.Collectors;

public class SimulationResult {

  private final AtomicLongMap<Player> observedWinners;

  public SimulationResult(){
    observedWinners= AtomicLongMap.create();
  }

  public SimulationResult(Map<String, Long> protoMap){
    observedWinners = AtomicLongMap.create(BiStream.from(protoMap).mapKeys(player -> Player.valueOf(player)).toMap());
  }

  public void addWinner(Player player) {
    observedWinners.incrementAndGet(player);
  }

  public AtomicLongMap<Player> getObservedWinners() {
    return observedWinners;
  }
}
