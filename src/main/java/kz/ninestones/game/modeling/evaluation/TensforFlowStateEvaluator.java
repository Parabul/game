package kz.ninestones.game.modeling.evaluation;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import java.util.Collection;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.NormalizedStateEncoder;
import kz.ninestones.game.learning.encode.StateEncoder;
import org.tensorflow.Result;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.example.Feature;
import org.tensorflow.example.FloatList;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.DataBuffers;
import org.tensorflow.types.TFloat32;

public class TensforFlowStateEvaluator implements StateEvaluator {

  private final SavedModelBundle model;
  private final StateEncoder stateEncoder;
  private final Session session;

  public TensforFlowStateEvaluator() {
    this.model = SavedModelBundle.load("/home/anarbek/tmp/model/min_max", "serve");
    this.session = model.session();
    this.stateEncoder = new NormalizedStateEncoder();
    System.out.println("modelPath: /home/anarbek/tmp/model/min_max");
  }

  private static <T extends Number> Feature floatList(Collection<T> nums) {
    FloatList.Builder floatList = FloatList.newBuilder();

    nums.stream().forEach(number -> floatList.addValue(number.floatValue()));

    return Feature.newBuilder().setFloatList(floatList).build();
  }

  @Override
  public double evaluate(State state, Player player) {

    float[] encoded = Floats.toArray(Doubles.asList(stateEncoder.encode(state)));

    FloatNdArray val = NdArrays.wrap(Shape.of(1, 39), DataBuffers.of(encoded));

    TFloat32 input = TFloat32.tensorOf(val);

    Result output =
        session
            .runner()
            .feed("serving_default_input:0", input)
            .fetch("StatefulPartitionedCall:0")
            .run();

    double playerOneScore = output.get(0).asRawTensor().data().asFloats().getFloat(0);
    double playerTwoScore = output.get(0).asRawTensor().data().asFloats().getFloat(1);

    return player.equals(Player.ONE) ? playerOneScore : playerTwoScore;
  }
}
