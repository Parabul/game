package kz.ninestones.game.learning.montecarlo;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.AtomicLongMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.MathUtils;

public class LightTreeNode {

  public static final Supplier<LightTreeNode> ROOT = LightTreeNode::new;

  public static final double EXPLORATION_WEIGHT = Math.sqrt(2);

  private final LightTreeNode parent;
  private final List<LightTreeNode> children = new ArrayList<>(9);
  private final int move;

  // this.state := parent.state + this.move
  private final State state;
  private final AtomicLongMap<Player> observedWinners = AtomicLongMap.create();

  LightTreeNode(LightTreeNode parent, int move) {
    this.parent = parent;
    this.move = move;
    this.state = Policy.makeMove(parent.state, move);
  }

  // Root
  private LightTreeNode() {
    this.parent = null;
    this.move = -1;
    this.state = new State();
  }

  void initChildren() {
    IntStream.rangeClosed(1, 9)
        .boxed()
        .filter(move -> Policy.isAllowedMove(this.state, move))
        .map(move -> new LightTreeNode(this, move))
        .forEach(children::add);
  }

  @VisibleForTesting
  public void update(SimulationResult simulationResult) {
    simulationResult
        .getObservedWinners()
        .asMap()
        .entrySet()
        .forEach(entry -> this.observedWinners.addAndGet(entry.getKey(), entry.getValue()));
  }

  @VisibleForTesting
  public void backPropagate(SimulationResult simulationResult) {
    LightTreeNode current = this;
    while (current.parent != null) {
      current.parent.update(simulationResult);
      current = current.parent;
    }
  }

  public double getWeight(Player player) {
    checkNotNull(parent, "Root has no weight");

    long parentSimulations = parent.getSimulations();
    long simulations = getSimulations();

    double exploration =
        (parentSimulations > 0 && simulations > 0)
            ? (Math.sqrt(MathUtils.ln(parentSimulations) / simulations))
            : 0;
    double exploitation = simulations > 0 ? 1.0 * observedWinners.get(player) / simulations : 0;

    return exploitation + EXPLORATION_WEIGHT * exploration;
  }

  public AtomicLongMap<Player> getObservedWinners() {
    return observedWinners;
  }

  public List<LightTreeNode> getChildren() {
    return children;
  }

  public long getSimulations() {
    return observedWinners.sum();
  }

  public LightTreeNode getParent() {
    return parent;
  }

  public int getMove() {
    return move;
  }

  public State getState() {
    return state;
  }

//  public Game.GameSample toGameSample() {
//    return Game.GameSample.newBuilder()
//        .setState(state.toProto())
//        .putAllObservedWinners(BiStream.from(observedWinners.asMap()).mapKeys(Player::name).toMap())
//        .build();
//  }
//
//  public Example toTFExample(final StateEncoder stateEncoder) {
//    List<Double> input = Doubles.asList(stateEncoder.encode(state));
//
//    long simulations = getSimulations();
//
//    List<Double> output =
//        ImmutableList.of(
//            1.0 * observedWinners.asMap().getOrDefault(Player.ONE.name(), 0L) / simulations,
//            1.0 * observedWinners.asMap().getOrDefault(Player.TWO.name(), 0L) / simulations,
//            1.0 * observedWinners.asMap().getOrDefault(Player.NONE.name(), 0L) / simulations);
//
//    return Example.newBuilder()
//        .setFeatures(
//            Features.newBuilder()
//                .putFeature("input", TensorFlowUtils.floatList(input))
//                .putFeature("output", TensorFlowUtils.floatList(output)))
//        .build();
//  }
}
