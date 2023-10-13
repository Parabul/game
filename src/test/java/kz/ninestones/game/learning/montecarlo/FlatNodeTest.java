package kz.ninestones.game.learning.montecarlo;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.util.concurrent.AtomicLongMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import kz.ninestones.game.core.Player;
import org.junit.Test;

public class FlatNodeTest {

  @Test
  public void shouldTraverseToFlat() {
    List<FlatNode> nodes = new ArrayList<>();

    TreeNode root = TreeNode.ROOT.get();

    assertThat(root.getParent()).isNull();
    assertThat(root.getMove()).isEqualTo(-1);
    assertThat(root.getSimulations()).isEqualTo(0);

    root.initChildren();

    assertThat(root.getChildren()).hasSize(9);

    root.traverseToFlat(nodes);

    assertThat(nodes).hasSize(11);

    assertThat(nodes).contains(new FlatNode(-1, -1, new EnumMap<>(Player.class), false));
    assertThat(nodes).contains(new FlatNode(7, -1, new EnumMap<>(Player.class), true));
    assertThat(nodes).contains(FlatNode.DIVIDER);
  }

  @Test
  public void shouldTraverseToFlatNested() {
    List<FlatNode> nodes = new ArrayList<>();

    TreeNode root = TreeNode.ROOT.get();

    assertThat(root.getParent()).isNull();
    assertThat(root.getMove()).isEqualTo(-1);
    assertThat(root.getSimulations()).isEqualTo(0);

    root.initChildren();

    assertThat(root.getChildren()).hasSize(9);

    root.getChildren().get(0).initChildren();

    root.traverseToFlat(nodes);

    assertThat(nodes).hasSize(1 + 9 + 9 + 2);

    assertThat(nodes).contains(new FlatNode(-1, -1, new EnumMap<>(Player.class), false));
    assertThat(nodes).contains(new FlatNode(1, -1, new EnumMap<>(Player.class), false));
    assertThat(nodes).contains(new FlatNode(1, 1,new EnumMap<>(Player.class), true));
    assertThat(nodes).contains(FlatNode.DIVIDER);
  }
}
