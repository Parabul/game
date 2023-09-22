package kz.ninestones.game.learning.montecarlo;

import static com.google.common.truth.Truth.assertThat;

import kz.ninestones.game.core.Player;
import kz.ninestones.game.simulation.SimulationResult;
import kz.ninestones.game.utils.MathUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TreeNodeTest {
  @Test
  public void rootHasNoParent() {
    TreeNode root = TreeNode.ROOT.get();

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
    TreeNode root = TreeNode.ROOT.get();

    root.initChildren();

    TreeNode childState = root.getChildren().stream().findAny().get();

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

    double exploration = TreeNode.EXPLORATION_WEIGHT * Math.sqrt(MathUtils.ln(4) / 4);

    assertThat(childState.getWeight(Player.ONE)).isWithin(0.0001).of(0.75 + exploration);
    assertThat(childState.getWeight(Player.TWO)).isWithin(0.0001).of(0.25 + exploration);
    assertThat(childState.getWeight(Player.NONE)).isWithin(0.0001).of(0.0 + exploration);
  }

  @Test
  public void shouldMergeTwoRoots() {
    TreeNode root = TreeNode.ROOT.get();

    TreeNode otherRoot = TreeNode.ROOT.get();

    root.initChildren();
    otherRoot.initChildren();

    TreeNode child =
        root.getChildren().stream().filter(node -> node.getMove() == 7).findFirst().get();
    TreeNode otherChildOne =
        otherRoot.getChildren().stream().filter(node -> node.getMove() == 7).findFirst().get();
    TreeNode otherChildTwo =
        otherRoot.getChildren().stream().filter(node -> node.getMove() == 8).findFirst().get();

    SimulationResult simulationResultOne = new SimulationResult();
    simulationResultOne.addWinner(Player.ONE);
    simulationResultOne.addWinner(Player.ONE);
    simulationResultOne.addWinner(Player.ONE);
    simulationResultOne.addWinner(Player.TWO);

    child.update(simulationResultOne);
    child.backPropagate(simulationResultOne);

    SimulationResult simulationResultTwo = new SimulationResult();
    simulationResultTwo.addWinner(Player.ONE);
    simulationResultTwo.addWinner(Player.TWO);

    otherChildOne.update(simulationResultTwo);
    otherChildOne.backPropagate(simulationResultTwo);

    SimulationResult simulationResultThree = new SimulationResult();
    simulationResultThree.addWinner(Player.TWO);
    simulationResultThree.addWinner(Player.TWO);

    otherChildTwo.update(simulationResultThree);
    otherChildTwo.backPropagate(simulationResultThree);

    root.mergeFrom(otherRoot);

    TreeNode refreshedChildOne =
        root.getChildren().stream().filter(node -> node.getMove() == 7).findFirst().get();
    TreeNode refreshedChildTwo =
        root.getChildren().stream().filter(node -> node.getMove() == 8).findFirst().get();

    assertThat(root.getSimulations()).isEqualTo(8);
    assertThat(root.getObservedWinners().get(Player.ONE)).isEqualTo(4);
    assertThat(root.getObservedWinners().get(Player.TWO)).isEqualTo(4);
    assertThat(root.getObservedWinners().get(Player.NONE)).isEqualTo(0);


    assertThat(refreshedChildOne.getSimulations()).isEqualTo(6);
    assertThat(refreshedChildOne.getObservedWinners().get(Player.ONE)).isEqualTo(4);
    assertThat(refreshedChildOne.getObservedWinners().get(Player.TWO)).isEqualTo(2);
    assertThat(refreshedChildOne.getObservedWinners().get(Player.NONE)).isEqualTo(0);

    assertThat(refreshedChildTwo.getSimulations()).isEqualTo(2);
    assertThat(refreshedChildTwo.getObservedWinners().get(Player.ONE)).isEqualTo(0);
    assertThat(refreshedChildTwo.getObservedWinners().get(Player.TWO)).isEqualTo(2);
    assertThat(refreshedChildTwo.getObservedWinners().get(Player.NONE)).isEqualTo(0);
  }
}
