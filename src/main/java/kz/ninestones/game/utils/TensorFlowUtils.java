package kz.ninestones.game.utils;

import org.tensorflow.example.Feature;
import org.tensorflow.example.FloatList;
import org.tensorflow.example.Int64List;

public class TensorFlowUtils {
  public static Feature floatList(float[] nums) {
    FloatList.Builder floatList = FloatList.newBuilder();

    for (float num : nums) {
      floatList.addValue(num);
    }

    return Feature.newBuilder().setFloatList(floatList).build();
  }

  public static Feature intList(int... elements) {
    Int64List.Builder list = Int64List.newBuilder();

    for (int element : elements) {
      list.addValue(element);
    }

    return Feature.newBuilder().setInt64List(list).build();
  }

  public static Feature doubleList(double... elements) {
    FloatList.Builder list = FloatList.newBuilder();

    for (double element : elements) {
      list.addValue((float) element);
    }

    return Feature.newBuilder().setFloatList(list).build();
  }
}
