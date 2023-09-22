package kz.ninestones.game.learning.montecarlo;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicLongMap;
import com.google.mu.util.stream.BiStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.StateEncoder;
import kz.ninestones.game.proto.Game;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.MathUtils;
import kz.ninestones.game.utils.TensorFlowUtils;
import org.tensorflow.example.Example;
import org.tensorflow.example.Features;

public class TreeNode {

  public static final Supplier<TreeNode> ROOT = TreeNode::new;

  public static final double EXPLORATION_WEIGHT = Math.sqrt(2);
  private final List<TreeNode> children = new ArrayList<>(9);
  private final int move;
  // this.state := parent.state + this.move
  private final State state;
  private final AtomicLongMap<Player> observedWinners = AtomicLongMap.create();
  private TreeNode parent;

  TreeNode(TreeNode parent, int move) {
    this.parent = parent;
    this.move = move;
    this.state = Policy.makeMove(parent.state, move);
  }

  // Root
  private TreeNode() {
    this.parent = null;
    this.move = -1;
    this.state = new State();
  }

  void initChildren() {
    IntStream.rangeClosed(1, 9)
        .boxed()
        .filter(move -> Policy.isAllowedMove(this.state, move))
        .map(move -> new TreeNode(this, move))
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
    TreeNode current = this;
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

  public List<TreeNode> getChildren() {
    return children;
  }

  public long getSimulations() {
    return observedWinners.sum();
  }

  public TreeNode getParent() {
    return parent;
  }

  public int getMove() {
    return move;
  }

  public State getState() {
    return state;
  }

  public Game.GameSample toGameSample() {
    return Game.GameSample.newBuilder()
        .setState(state.toProto())
        .putAllObservedWinners(BiStream.from(observedWinners.asMap()).mapKeys(Player::name).toMap())
        .build();
  }

  public Example toTFExample(final StateEncoder stateEncoder) {
    float[] input = stateEncoder.encode(state);

    long simulations = getSimulations();

    float[] output =
        new float[]{
            1.0f * observedWinners.asMap().getOrDefault(Player.ONE.name(), 0L) / simulations,
            1.0f * observedWinners.asMap().getOrDefault(Player.TWO.name(), 0L) / simulations,
            1.0f * observedWinners.asMap().getOrDefault(Player.NONE.name(), 0L) / simulations};

    return Example.newBuilder()
        .setFeatures(
            Features.newBuilder()
                .putFeature("input", TensorFlowUtils.floatList(input))
                .putFeature("output", TensorFlowUtils.floatList(output)))
        .build();
  }

  public void mergeFrom(TreeNode other){
    this.observedWinners.addAndGet(Player.ONE, other.observedWinners.get(Player.ONE));
    this.observedWinners.addAndGet(Player.TWO, other.observedWinners.get(Player.TWO));
    this.observedWinners.addAndGet(Player.NONE, other.observedWinners.get(Player.NONE));

    if(other.getChildren().isEmpty()){
      return;
    }

    if(this.getChildren().isEmpty()){
      for(TreeNode child: other.getChildren()){
        child.parent = this;
        this.children.add(child);
      }
    }else {
      ImmutableMap<Integer, TreeNode> selfChildren = Maps.uniqueIndex(this.getChildren(), TreeNode::getMove);
      ImmutableMap<Integer, TreeNode> otherChildren = Maps.uniqueIndex(other.getChildren(), TreeNode::getMove);

      for(int move= 1;move <= 9; move++){
        if(selfChildren.containsKey(move)){
          selfChildren.get(move).mergeFrom(otherChildren.get(move));
        }
      }
    }
  }
}
