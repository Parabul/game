package kz.ninestones.game.learning.fastmontecarlo.montecarlo;

import com.google.common.base.Objects;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.TensorFlowUtils;
import org.tensorflow.example.Example;
import org.tensorflow.example.Features;

import java.io.Serializable;
import java.util.EnumMap;

public class GameStateNodeInfo implements Serializable {
  private final State state;
  private final EnumMap<Player, Integer> observedOutcomes;

  public GameStateNodeInfo(State state, EnumMap<Player, Integer> observedOutcomes) {
    this.state = state;
    this.observedOutcomes = observedOutcomes;
  }

  public GameStateNodeInfo(GameStateNodeInfo other) {
    this.state = new State(other.state);
    this.observedOutcomes = new EnumMap<>(other.observedOutcomes);
  }

  public long getSimulations() {
    return observedOutcomes.values().stream().mapToInt(Integer::intValue).sum();
  }

  public Example toTFExample(final StateEncoder stateEncoder, boolean direct) {

    float simulations = (float) getSimulations();

    float[] output =
        new float[] {
          1.0f * observedOutcomes.get(Player.ONE) / simulations,
          1.0f * observedOutcomes.get(Player.TWO) / simulations,
          1.0f * observedOutcomes.get(Player.NONE) / simulations
        };

    Example.Builder ex =
        Example.newBuilder()
            .setFeatures(
                Features.newBuilder().putFeature("output", TensorFlowUtils.floatList(output)));
    stateEncoder
        .featuresOf(state, direct)
        .forEach((name, feature) -> ex.getFeaturesBuilder().putFeature(name, feature));
    return ex.build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GameStateNodeInfo gameStateNode = (GameStateNodeInfo) o;
    return Objects.equal(state, gameStateNode.state)
        && Objects.equal(observedOutcomes, gameStateNode.observedOutcomes);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(state, observedOutcomes);
  }

  public void update(SimulationResult simulationResult) {
    for (Player player : Player.values()) {
      observedOutcomes.put(
          player, observedOutcomes.get(player) + simulationResult.getObservedWinners().get(player));
    }
  }

  public State getState() {
    return state;
  }

  public EnumMap<Player, Integer> getObservedOutcomes() {
    return observedOutcomes;
  }
}
