package kz.ninestones.game.learning.montecarlo;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.util.EnumMap;
import java.util.Queue;
import kz.ninestones.game.core.Player;

public class FlatNode {

  public static FlatNode DIVIDER = new FlatNode();
  private final int move;

  private final int parentMove;

  private final EnumMap<Player, Integer> observedWinners;
  private final boolean isLeaf;

  private final boolean isDivider;

  public FlatNode(int move, int parentMove, EnumMap<Player, Integer> observedWinners, boolean isLeaf) {
    this.move = move;
    this.parentMove = parentMove;
    this.observedWinners = observedWinners;
    this.isLeaf = isLeaf;
    this.isDivider = false;
  }

  private FlatNode() {
    this.move = -1;
    this.parentMove = -1;
    this.observedWinners = null;
    this.isLeaf = false;
    this.isDivider = true;
  }

//  public static TreeNode reconstruct(List<FlatNode> input){
//    Queue<FlatNode> nodes = new LinkedList<>(input);
//    // Root
//    FlatNode rootNode = nodes.poll();
//    TreeNode root = TreeNode.ROOT.get();
//    root.getObservedWinners().putAll(rootNode.observedWinners.asMap());
//
//    reconstruct(nodes, root);
//  }

  private static void reconstruct(Queue<FlatNode> nodes, TreeNode parent) {
    FlatNode current = nodes.poll();
    while(!current.isDivider){
      TreeNode treeNode = new TreeNode(parent, current.move);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FlatNode flatNode = (FlatNode) o;
    return move == flatNode.move && parentMove == flatNode.parentMove && isLeaf == flatNode.isLeaf && isDivider == flatNode.isDivider;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(move, parentMove, isLeaf, isDivider);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("move", move)
            .add("parentMove", parentMove)
            .add("observedWinners", observedWinners)
            .add("isLeaf", isLeaf)
            .add("isDivider", isDivider)
            .toString();
  }
}
