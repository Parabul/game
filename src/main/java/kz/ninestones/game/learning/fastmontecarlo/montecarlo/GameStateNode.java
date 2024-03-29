package kz.ninestones.game.learning.fastmontecarlo.montecarlo;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.IntStream;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.TensorFlowUtils;
import org.tensorflow.example.Example;
import org.tensorflow.example.Features;

public class GameStateNode implements Serializable {

    private final State state;
    private final EnumMap<Player, Integer> observedOutcomes;
    private final List<GameStateNode> childStates = new ArrayList<>(9);

    public GameStateNode(State state) {
        this.state = state;
        observedOutcomes = new EnumMap<>(Player.class);
        for (Player player : Player.values()) {
            observedOutcomes.put(player, 0);
        }
    }

    public GameStateNode(GameStateNode other) {
        this.state = new State(other.state);
        this.observedOutcomes = new EnumMap<>(other.observedOutcomes);
    }

    public List<GameStateNode> getChildStates() {
        return childStates;
    }

    public State getState() {
        return state;
    }

    public EnumMap<Player, Integer> getObservedOutcomes() {
        return observedOutcomes;
    }

    public void update(SimulationResult simulationResult) {
        for (Player player : Player.values()) {
            observedOutcomes.put(player, observedOutcomes.get(player) + simulationResult.getObservedWinners().get(player));
        }
    }

    public long getSimulations() {
        return observedOutcomes.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("state", state).add("observedOutcomes", observedOutcomes).toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameStateNode gameStateNode = (GameStateNode) o;
        return Objects.equal(state, gameStateNode.state) && Objects.equal(observedOutcomes, gameStateNode.observedOutcomes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(state, observedOutcomes);
    }

    public void initChildren() {
        IntStream.rangeClosed(1, 9).filter(move -> Policy.isAllowedMove(state, move)).mapToObj(move -> Policy.makeMove(state, move)).map(GameStateNode::new).forEachOrdered(childStates::add);
    }



    public static class GameStateNodeInfo implements Serializable{
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

            long simulations = getSimulations();

            float[] output = new float[]{1.0f * observedOutcomes.get(Player.ONE) / simulations, 1.0f * observedOutcomes.get(Player.TWO) / simulations, 1.0f * observedOutcomes.get(Player.NONE) / simulations};

            Example.Builder ex = Example.newBuilder().setFeatures(Features.newBuilder().putFeature("output", TensorFlowUtils.floatList(output)));
            stateEncoder.featuresOf(state, direct).forEach((name, feature) -> ex.getFeaturesBuilder().putFeature(name, feature));
            return ex.build();
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GameStateNodeInfo gameStateNode = (GameStateNodeInfo) o;
            return Objects.equal(state, gameStateNode.state) && Objects.equal(observedOutcomes, gameStateNode.observedOutcomes);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(state, observedOutcomes);
        }



        public void update(SimulationResult simulationResult) {
            for (Player player : Player.values()) {
                observedOutcomes.put(player, observedOutcomes.get(player) + simulationResult.getObservedWinners().get(player));
            }
        }

        public State getState() {
            return state;
        }

        public EnumMap<Player, Integer> getObservedOutcomes() {
            return observedOutcomes;
        }
    }

}
