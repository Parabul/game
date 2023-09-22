package kz.ninestones.game.learning.evaluation;

import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.NormalizedStateEncoder;
import kz.ninestones.game.learning.encode.StateEncoder;
import org.tensorflow.Result;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
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
    this.model = SavedModelBundle.load("/mnt/nfs/models/min_max/stable", "serve");
    this.session = model.session();
    this.stateEncoder = new NormalizedStateEncoder();
    System.out.println("modelPath: /mnt/nfs/models/min_max/stable");
  }

  @Override
  public double evaluate(State state, Player player) {

    FloatNdArray val = NdArrays.wrap(Shape.of(1, 39), DataBuffers.of(stateEncoder.encode(state)));

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
