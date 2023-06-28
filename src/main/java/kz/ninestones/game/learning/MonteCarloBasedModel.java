package kz.ninestones.game.learning;

import com.google.common.base.Stopwatch;
import java.io.File;
import java.io.IOException;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * Created by Anwar on 3/15/2016. An example of regression neural network for performing addition
 */
// @SuppressWarnings({"DuplicatedCode", "FieldCanBeLocal"})
public class MonteCarloBasedModel {

  // Random number generator seed, for reproduceability
  public static final int seed = 12345;
  // Number of epochs (full passes of the data)
  public static final int nEpochs = 20;
  // Batch size: i.e., each epoch has nSamples/batchSize parameter updates
  public static final int batchSize = 1000;
  // Network learning rate
  public static final double learningRate = 0.001;

  // Number of data points
  private static final int nSamples = 500;

  public static void main(String[] args) throws IOException {
    Stopwatch watch = Stopwatch.createStarted();
    System.out.println("Start: " + watch);

    // Create the network
    int numInput = 36;
    int numOutputs = 3;
    int nHidden = 108;

    MultiLayerNetwork net =
        new MultiLayerNetwork(
            new NeuralNetConfiguration.Builder()
                .seed(seed)
                .l1(0.001)
                .weightInit(WeightInit.RELU_UNIFORM)
                .updater(new Adam(learningRate))
                .list()
                .layer(
                    0,
                    new DenseLayer.Builder()
                        .nIn(numInput)
                        .nOut(nHidden)
                        .activation(
                            Activation
                                .RELU) // Change this to RELU and you will see the net learns very
                        // well very quickly
                        .build())
                .layer(
                    1,
                    new DenseLayer.Builder()
                        .nIn(nHidden)
                        .nOut(nHidden)
                        .activation(
                            Activation
                                .RELU) // Change this to RELU and you will see the net learns very
                        // well very quickly
                        .build())
                .layer(
                    2,
                    new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.SIGMOID)
                        .nIn(nHidden)
                        .nOut(numOutputs)
                        .build())
                .build());
    net.init();
    //    net.setListeners(new ScoreIterationListener(1));

    System.out.println("Init: " + watch);

    DataSetIterator iterator =
        (new MonteCarloTreeSearchTrainingSetGenerator()).generateTrainingData(nSamples, batchSize);

    // Train the network on the full data set, and evaluate in periodically
    for (int i = 0; i < nEpochs; i++) {
      System.out.println("training: 1 " + " epoch: " + i + " -> " + watch);
      System.out.println("score: " + net.score());
      iterator.reset();
      net.fit(iterator);
    }

    watch.stop();

    System.out.println();
    System.out.println("Stopwatch: " + watch);

    DataSet testSet = (new MonteCarloTreeSearchTrainingSetGenerator()).generateDataSet(10);

    Evaluation eval = new Evaluation(3);
    INDArray output = net.output(testSet.getFeatures());
    eval.eval(testSet.getLabels(), output);
    System.out.println(eval.stats());

    RegressionEvaluation regressionEvaluation = new RegressionEvaluation(3);
    regressionEvaluation.eval(testSet.getLabels(), output);
    System.out.println(regressionEvaluation.stats());

    net.save(new File("/home/anarbek/projects/ninestones/models/monte_carlo_01.model"));
  }
}
