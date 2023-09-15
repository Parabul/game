package kz.ninestones.game.modeling.evaluation;

import org.tensorflow.Result;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.DataBuffers;
import org.tensorflow.proto.framework.MetaGraphDef;
import org.tensorflow.types.TFloat32;

public class UseModel {
  public static void main(String[] args) {
    SavedModelBundle model = SavedModelBundle.load("/home/anarbek/tmp/model/min_max", "serve");
    Session session = model.session();
    //        Tensor inputTensor = Tensor.of(Float.class, Shape.of(1,2)).create(new float[][]{{0.2f,
    // 0.3f}});

    final MetaGraphDef metaGraphDef = model.metaGraphDef();

    //    System.out.println(metaGraphDef.toString());
    //
    //        final SignatureDef signatureDef =
    // metaGraphDef.getSignatureDefMap().get("serving_default");
    //
    //        final TensorInfo inputTensorInfo = signatureDef.getInputsMap()
    //                .values()
    //                .stream()
    //                .filter(Objects::nonNull)
    //                .findFirst()
    //                .get();

    //
    //        System.out.println(inputTensorInfo.toBuilder().buildPartial());
    //
    //        final TensorInfo outputTensorInfo = signatureDef.getOutputsMap()
    //                .values()
    //                .stream()
    //                .filter(Objects::nonNull)
    //                .findFirst()
    //                .get();

    //        System.out.println(outputTensorInfo);
    //
    //    Ops tf = Ops.create();
    //    Constant<TFloat32> c1 = tf.constant(new float[] {0.2f, 0.3f});

//

    FloatNdArray val =
        NdArrays.wrap(
            Shape.of(1, 39),
            DataBuffers.of(
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.055555556f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f));

    TFloat32 input = TFloat32.tensorOf(val);

    Result output =
        session
            .runner()
            .feed("serving_default_input:0", input)
            .fetch("StatefulPartitionedCall:0")
            .run();

    float[] result = new float[] {};

    System.out.println(output.get(0).asRawTensor().data().asFloats().getFloat(0));
    System.out.println(output.get(0).asRawTensor().data().asFloats().getFloat(1));
    System.out.println(output.get(0).asRawTensor().data().asFloats().getFloat(2));

    //    System.out.println(Arrays.toString(result));
  }
}
