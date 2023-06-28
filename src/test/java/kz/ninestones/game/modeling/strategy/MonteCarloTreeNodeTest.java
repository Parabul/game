package kz.ninestones.game.modeling.strategy;

import static com.google.common.truth.Truth.assertThat;

import kz.ninestones.game.core.Player;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.MathUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class MonteCarloTreeNodeTest {
  @Test
  public void rootHasNoParent() {
    MonteCarloTreeNode root = MonteCarloTreeNode.ROOT.get();

    assertThat(root.getParent()).isNull();
    assertThat(root.getMove()).isEqualTo(-1);
    assertThat(root.getSimulations()).isEqualTo(0);

    root.initChildren();

    assertThat(root.getChildren()).hasSize(9);

    NullPointerException exception =
        Assertions.assertThrows(NullPointerException.class, () -> root.getWeight(Player.ONE));
    assertThat(exception).hasMessageThat().contains("Root has no weight");
  }

  @Test
  public void backPropagateChangesWeight() {
    MonteCarloTreeNode root = MonteCarloTreeNode.ROOT.get();

    root.initChildren();

    MonteCarloTreeNode childState = root.getChildren().stream().findAny().get();

    assertThat(childState.getWeight(Player.ONE)).isEqualTo(0);
    assertThat(childState.getWeight(Player.TWO)).isEqualTo(0);
    assertThat(childState.getWeight(Player.NONE)).isEqualTo(0);

    SimulationResult simulationResult = new SimulationResult();
    simulationResult.addWinner(Player.ONE);
    simulationResult.addWinner(Player.ONE);
    simulationResult.addWinner(Player.ONE);
    simulationResult.addWinner(Player.TWO);

    childState.update(simulationResult);

    assertThat(root.getSimulations()).isEqualTo(0);
    assertThat(childState.getSimulations()).isEqualTo(4);
    assertThat(childState.getWeight(Player.ONE)).isWithin(0.0001).of(0.75);
    assertThat(childState.getWeight(Player.TWO)).isWithin(0.0001).of(0.25);
    assertThat(childState.getWeight(Player.NONE)).isWithin(0.0001).of(0.0);

    childState.backPropagate(simulationResult);

    assertThat(childState.getSimulations()).isEqualTo(4);
    assertThat(root.getSimulations()).isEqualTo(4);

    double exploration = MonteCarloTreeNode.EXPLORATION_WEIGHT * Math.sqrt(MathUtils.ln(4) / 4);

    assertThat(childState.getWeight(Player.ONE)).isWithin(0.0001).of(0.75 + exploration);
    assertThat(childState.getWeight(Player.TWO)).isWithin(0.0001).of(0.25 + exploration);
    assertThat(childState.getWeight(Player.NONE)).isWithin(0.0001).of(0.0 + exploration);
  }
}
