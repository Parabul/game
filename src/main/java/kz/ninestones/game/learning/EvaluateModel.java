package kz.ninestones.game.learning;

import java.io.File;
import java.io.IOException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

public class EvaluateModel {

  public static void main(String[] args) throws IOException {
    DataSetIterator iterator = (new TrainingSetGenerator()).generateTrainingData(1000, 100);

    MultiLayerNetwork model = MultiLayerNetwork.load(
        new File("/home/anarbek/projects/ninestones/models/3.2.model"), false);

    Evaluation eval = model.evaluate(iterator);

    System.out.println(eval.stats());
  }
}
/**
first model
========================Evaluation Metrics========================
 # of classes:    2
 Accuracy:        0.8139
 Precision:       0.8413
 Recall:          0.8469
 F1 Score:        0.8441
 Precision, recall & F1: reported for positive class (class 1 - "1") only


 =========================Confusion Matrix=========================
 0     1
 -------------
 58898 18054 | 0 = 0
 17296 95681 | 1 = 1

 Confusion matrix format: Actual (rowClass) predicted as (columnClass) N times
 ==================================================================

 second model
 ========================Evaluation Metrics========================
 # of classes:    2
 Accuracy:        0.8252
 Precision:       0.8422
 Recall:          0.8887
 F1 Score:        0.8648
 Precision, recall & F1: reported for positive class (class 1 - "1") only


 =========================Confusion Matrix=========================
 0      1
 ---------------
 50016  19683 | 0 = 0
 13157 105032 | 1 = 1

 Confusion matrix format: Actual (rowClass) predicted as (columnClass) N times
 ==================================================================
 *
 */

/** 3.1
 ========================Evaluation Metrics========================
 # of classes:    2
 Accuracy:        0.8359
 Precision:       0.8492
 Recall:          0.8933
 F1 Score:        0.8707
 Precision, recall & F1: reported for positive class (class 1 - "1") only


 =========================Confusion Matrix=========================
 0      1
 ---------------
 53213  18418 | 0 = 0
 12390 103689 | 1 = 1

 Confusion matrix format: Actual (rowClass) predicted as (columnClass) N times
 ==================================================================

 Process finished with exit code 0

 */