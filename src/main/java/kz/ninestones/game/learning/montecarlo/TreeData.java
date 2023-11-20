package kz.ninestones.game.learning.montecarlo;

import com.google.common.collect.ArrayListMultimap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TreeData implements Serializable {
  private final Map<String, StateNode> index;

  private final ArrayListMultimap<String, String> parentToChild;

  public TreeData() {
    index = new HashMap<>();
    parentToChild = ArrayListMultimap.create();
  }

  public TreeData(TreeData other) {
    index = new HashMap<>();
    other.index.forEach((key, value) -> index.put(key, new StateNode(value)));
    parentToChild = ArrayListMultimap.create(other.parentToChild);
  }

  public void merge(TreeData other) {
    other.getIndex().forEach((key, value) -> this.index.merge(key, value, (o, n) -> o.merge(n)));
    this.parentToChild.putAll(other.parentToChild);
  }

  public Map<String, StateNode> getIndex() {
    return index;
  }

  public ArrayListMultimap<String, String> getParentToChild() {
    return parentToChild;
  }
}
