package kz.ninestones.game.simulation;

import kz.ninestones.game.modeling.evaluation.NeuralNetStateEvaluator;
import kz.ninestones.game.modeling.evaluation.ScoreDiffStateEvaluator;
import kz.ninestones.game.modeling.strategy.MatrixMinMaxStrategy;
import kz.ninestones.game.modeling.strategy.RandomMoveStrategy;
import kz.ninestones.game.modeling.strategy.Strategy;

public enum KnownStrategy {

  RANDOM(new RandomMoveStrategy()),

//  FIRST_ALLOWED_MOVE(new FirstAllowedMoveStrategy()),

//  MAX_SCORE_DIFF(new MaxStrategy(new ScoreDiffStateEvaluator())),

//  MIN_MAX_PLAYER_SCORE(new MatrixMinMaxStrategy(new ScoreStateEvaluator())),

  MIN_MAX_SCORE_DIFF(new MatrixMinMaxStrategy(new ScoreDiffStateEvaluator())),

  NEURAL_NET_2(new MatrixMinMaxStrategy(new NeuralNetStateEvaluator(
      "/home/anarbek/projects/ninestones/models/second.model"))),

  NEURAL_NET_1(new MatrixMinMaxStrategy(new NeuralNetStateEvaluator(
      "/home/anarbek/projects/ninestones/models/first.model"))),

  LATEST(new MatrixMinMaxStrategy(new NeuralNetStateEvaluator(
      "/home/anarbek/projects/ninestones/models/3.1.model")));

  private final Strategy strategy;

  KnownStrategy(Strategy strategy) {
    this.strategy = strategy;
  }

  public Strategy getStrategy() {
    return strategy;
  }
}
