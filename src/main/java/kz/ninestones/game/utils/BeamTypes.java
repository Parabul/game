package kz.ninestones.game.utils;

import kz.ninestones.game.learning.fastmontecarlo.montecarlo.GameStateNode;
import kz.ninestones.game.proto.Game;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.tensorflow.example.Example;

public class BeamTypes {
  public static TypeDescriptor<byte[]> byteArrays = new TypeDescriptor<byte[]>() {};
  public static TypeDescriptor<Example> examples = new TypeDescriptor<Example>() {};
  public static TypeDescriptor<Game.StateProto> stateProtos =
      new TypeDescriptor<Game.StateProto>() {};
  public static TypeDescriptor<GameStateNode> stateNodes = new TypeDescriptor<GameStateNode>() {};
}
