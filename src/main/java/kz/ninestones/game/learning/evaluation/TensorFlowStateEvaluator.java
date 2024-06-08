package kz.ninestones.game.learning.evaluation;

import java.util.List;
import java.util.Map;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.DefaultStateEncoder;
import kz.ninestones.game.learning.encode.StateEncoder;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.example.Feature;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.DataBuffers;
import org.tensorflow.types.TFloat32;

public class TensorFlowStateEvaluator implements StateEvaluator {

  public static final String OPERATION = "StatefulPartitionedCall:0";
  public static final String FEED_KEY_MASK = "serving_default_%s:0";
  private final String modelPath;
  private final StateEncoder stateEncoder;

  private final boolean direct;
  private transient SavedModelBundle model;

  public TensorFlowStateEvaluator(String modelPath, boolean direct) {
    this.direct = direct;
    this.modelPath = modelPath;
    this.stateEncoder = new DefaultStateEncoder();
  }

  public TensorFlowStateEvaluator() {
    this.modelPath = getClass().getClassLoader().getResource("models/incumbent/").getPath();
    this.stateEncoder = new DefaultStateEncoder();
    this.direct = false;
  }

  public TensorFlowStateEvaluator(String modelPath, boolean direct, StateEncoder stateEncoder) {
    this.direct = direct;
    this.modelPath = modelPath;
    this.stateEncoder = stateEncoder;
  }

  private static TFloat32 fromFeature(Feature feature) {
    List<? extends Number> values;
    if (feature.hasFloatList()) {
      values = feature.getFloatList().getValueList();
    } else if (feature.hasInt64List()) {
      values = feature.getInt64List().getValueList();
    } else {
      throw new UnsupportedOperationException();
    }

    int n = values.size();
    float[] floatArray = new float[n];

    for (int i = 0; i < n; i++) {
      floatArray[i] = values.get(i).floatValue();
    }

    return TFloat32.tensorOf(NdArrays.wrap(Shape.of(1, n), DataBuffers.of(floatArray)));
  }

  private synchronized SavedModelBundle getModel() {
    if (model == null) {
      model = SavedModelBundle.load(modelPath, "serve");
    }
    return model;
  }

  @Override
  public double evaluate(State state, Player player) {
    if (Policy.isGameOver(state)) {
      return Policy.winnerOf(state).get().equals(player) ? 1.01 : 0;
    }

    Map<String, Feature> features = stateEncoder.featuresOf(state, direct);

    Session.Runner runner = getModel().session().runner();

    for (Map.Entry<String, Feature> featureByName : features.entrySet()) {
      runner.feed(
          String.format(FEED_KEY_MASK, featureByName.getKey()),
          fromFeature(featureByName.getValue()));
    }

    return runner
        .fetch(OPERATION)
        .run()
        .get(0)
        .asRawTensor()
        .data()
        .asFloats()
        .getFloat(player.ordinal());
  }
}
