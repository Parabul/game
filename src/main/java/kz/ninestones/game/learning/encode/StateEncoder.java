package kz.ninestones.game.learning.encode;

import java.util.Map;
import kz.ninestones.game.core.State;
import org.tensorflow.example.Feature;

public interface StateEncoder {
  String INPUT = "input";

  // If direct is true, the only feature returned is "input" (FloatList) - concatenation of the
  // features.
  Map<String, Feature> featuresOf(State state, boolean direct);
}
