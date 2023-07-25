package kz.ninestones.game.modeling.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.AtomicLongMap;
import com.google.mu.util.stream.BiStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.proto.Game;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.MathUtils;

public class MonteCarloTreeNode {

  public static final Supplier<MonteCarloTreeNode> ROOT = MonteCarloTreeNode::new;

  public static final double EXPLORATION_WEIGHT = Math.sqrt(2);

  private final MonteCarloTreeNode parent;
  private final List<MonteCarloTreeNode> children = new ArrayList<>(9);
  private final int move;

  // this.state := parent.state + this.move
  private final State state;
  private final AtomicLongMap<Player> observedWinners = AtomicLongMap.create();
  // Player that decides to move to current state/node.

  MonteCarloTreeNode(MonteCarloTreeNode parent, int move) {
    this.parent = parent;
    this.move = move;
    this.state = Policy.makeMove(parent.state, move);
  }

  // Root
  private MonteCarloTreeNode() {
    this.parent = null;
    this.move = -1;
    this.state = new State();
  }

  void initChildren() {
    IntStream.rangeClosed(1, 9)
        .boxed()
        .filter(move -> Policy.isAllowedMove(this.state, move))
        .map(move -> new MonteCarloTreeNode(this, move))
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
    MonteCarloTreeNode current = this;
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

  public List<MonteCarloTreeNode> getChildren() {
    return children;
  }

  public long getSimulations() {
    return observedWinners.sum();
  }

  public MonteCarloTreeNode getParent() {
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
        .setState(getState().toProto())
        .putAllObservedWinners(
            BiStream.from(getObservedWinners().asMap()).mapKeys(Player::name).toMap())
        .build();
  }
}
