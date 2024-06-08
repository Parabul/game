package kz.ninestones.game.learning.evaluation;

import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;

import java.util.List;
import java.util.Map;

public interface BatchStateEvaluator {
  Map<String, Double> evaluate(List<State> states, Player player);
}
