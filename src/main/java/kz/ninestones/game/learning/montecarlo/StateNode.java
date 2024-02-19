package kz.ninestones.game.learning.montecarlo;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.io.Serializable;
import java.util.EnumMap;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.TensorFlowUtils;
import org.tensorflow.example.Example;
import org.tensorflow.example.Features;

public class StateNode implements Serializable {
  private final State state;
  private final EnumMap<Player, Integer> observedOutcomes;

  public StateNode(State state) {
    this.state = state;
    observedOutcomes = new EnumMap<>(Player.class);
    for (Player player : Player.values()) {
      observedOutcomes.put(player, 0);
    }
  }

  public StateNode(StateNode other) {
    this.state = other.state;
    this.observedOutcomes = new EnumMap<>(other.observedOutcomes);
  }

  public StateNode merge(StateNode other) {
    for (Player player : Player.values()) {
      observedOutcomes.put(
          player, observedOutcomes.get(player) + other.getObservedOutcomes().get(player));
    }
    return this;
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
    return observedOutcomes.values().stream().mapToInt(Integer::intValue).sum();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("state", state)
        .add("observedOutcomes", observedOutcomes)
        .toString();
  }

  public Example toTFExample(final StateEncoder stateEncoder, boolean direct) {
    long simulations = getSimulations();

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
    StateNode stateNode = (StateNode) o;
    return Objects.equal(state, stateNode.state)
        && Objects.equal(observedOutcomes, stateNode.observedOutcomes);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(state, observedOutcomes);
  }
}
