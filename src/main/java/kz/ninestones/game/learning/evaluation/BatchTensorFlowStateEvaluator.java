package kz.ninestones.game.learning.evaluation;

import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.encode.DefaultStateEncoder;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.example.FloatList;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.DataBuffers;
import org.tensorflow.types.TFloat32;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BatchTensorFlowStateEvaluator implements Serializable {

    private static final String MODEL_PATH ="/var/shared_disk/models/v8/direct/tf";

    public static final String OPERATION = "StatefulPartitionedCall:0";
    public static final String FEED_KEY_MASK = "serving_default_%s:0";
    private static Session SESSION = SavedModelBundle.load(MODEL_PATH, "serve").session();



    public BatchTensorFlowStateEvaluator() {
    }



    public Map<String, Double> evaluate(List<State> states, Player player) {
        Map<String, Double> scoreByStateId = new HashMap<>();
        states.stream().filter(Policy::isGameOver).forEach(state -> scoreByStateId.put(state.getId(), Policy.winnerOf(state).get().equals(player) ? 1.01 : 0));

        List<State> notTerminalStates = states.stream().filter(state -> !(Policy.isGameOver(state))).collect(Collectors.toList());

        List<Float> encoded = notTerminalStates.stream().map(DefaultStateEncoder::direct).map(FloatList::getValueList).flatMap(List::stream).collect(Collectors.toList());
        float[] encodedRaw = new float[encoded.size()];
        for (int i = 0; i < encoded.size(); i++) {
            encodedRaw[i] = encoded.get(i);
        }

        TFloat32 input = TFloat32.tensorOf(NdArrays.wrap(Shape.of(notTerminalStates.size(), 39), DataBuffers.of(encodedRaw)));

        Session.Runner runner = SESSION.runner();

        runner.feed(String.format(FEED_KEY_MASK, DefaultStateEncoder.INPUT), input);

        FloatNdArray output = NdArrays.wrap(Shape.of(notTerminalStates.size(), 3), runner.fetch(OPERATION).run().get(0).asRawTensor().data().asFloats());

        for(int i=0; i < notTerminalStates.size();i++){
            String id = notTerminalStates.get(i).getId();
            Float score = output.getFloat(i, player.ordinal());
            scoreByStateId.put(id, score.doubleValue());
        }

        return scoreByStateId;
    }
}
