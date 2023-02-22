package kz.ninestones.game.learning;


import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * Created by Anwar on 3/15/2016. An example of regression neural network for performing addition
 */
//@SuppressWarnings({"DuplicatedCode", "FieldCanBeLocal"})
public class SumModel {

  //Random number generator seed, for reproduceability
  public static final int seed = 12345;
  //Number of epochs (full passes of the data)
  public static final int nEpochs = 20;
  //Batch size: i.e., each epoch has nSamples/batchSize parameter updates
  public static final int batchSize = 10000;
  //Network learning rate
  public static final double learningRate = 0.01;

  //Number of data points
  private static final int nSamples = 5000;


  public static void main(String[] args) throws IOException {
    System.out.println("-----");

    Stopwatch watch = Stopwatch.createStarted();
    System.out.println("Start: " + watch);

    //Generate the training data

    //Create the network
    int numInput = 163;
    int numOutputs = 1;
    int nHidden = 300;
    MultiLayerNetwork net = new MultiLayerNetwork(
        new NeuralNetConfiguration.Builder().seed(seed).weightInit(WeightInit.XAVIER)
            .updater(new Adam(learningRate)).list().layer(0,
                new DenseLayer.Builder().nIn(numInput).nOut(nHidden).activation(
                        Activation.RELU) //Change this to RELU and you will see the net learns very well very quickly
                    .build()).layer(1, new DenseLayer.Builder().nIn(nHidden).nOut(nHidden).activation(
                    Activation.RELU) //Change this to RELU and you will see the net learns very well very quickly
                .build()).layer(2,
                new OutputLayer.Builder(LossFunctions.LossFunction.MSE).activation(Activation.SIGMOID)
                    .nIn(nHidden).nOut(numOutputs).build()).build());
    net.init();
    net.setListeners(new ScoreIterationListener(1));

    System.out.println("Init: " + watch);

    for (int j = 0; j < 10; j++) {
      DataSetIterator iterator = TrainingSet.getTrainingData(nSamples, batchSize);

      //Train the network on the full data set, and evaluate in periodically
      for (int i = 0; i < nEpochs; i++) {
        System.out.println("training: " + j + " epoch: " + i + " -> " + watch);
        iterator.reset();
        net.fit(iterator);

        final INDArray input = StateEncoder.encode(new State());
        INDArray out = net.output(input, false);
        System.out.println(out);

        final INDArray input2 = StateEncoder.encode(
            new State(ImmutableMap.of(0, 10, 12, 10), new int[]{80, 62}, new int[]{-1, -1},
                Player.ONE));
        INDArray out2 = net.output(input2, false);
        System.out.println(out2);
      }
    }
    // Test the addition of 2 numbers (Try different numbers here)
    final INDArray input = StateEncoder.encode(new State());
    INDArray out = net.output(input, false);
    System.out.println(out);

    watch.stop();

    System.out.println();
    System.out.println("Stopwatch: " + watch);

    net.save(new File("/home/anarbek/projects/ninestones/models/first.model"));
  }

}
