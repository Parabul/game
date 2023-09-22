package kz.ninestones.game.utils;

import org.apache.beam.sdk.values.TypeDescriptor;
import org.tensorflow.example.Example;

public class BeamTypes {
    public static TypeDescriptor<byte[]> byteArrays = new TypeDescriptor<byte[]>() {};
    public static TypeDescriptor<Example> examples = new TypeDescriptor<Example>() {};
}
