package kz.ninestones.game.modeling.strategy;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.truth.Truth;
import java.util.ArrayList;
import java.util.List;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.learning.evaluation.ScoreDiffStateEvaluator;
import org.junit.Test;

public class RecursiveMinMaxTest {

  @Test
  public void regressionTest() {
    RecursiveMinMax recursiveMinMax = new RecursiveMinMax(new ScoreDiffStateEvaluator(), 2);
    State state = new State();

    List<Integer> moves = new ArrayList<>(10);

    for(int i =0;i < 10; i++){
      int move = recursiveMinMax.suggestNextMove(state);
      moves.add(move);
      state = Policy.makeMove(state, move);
    }

    Truth.assertThat(moves).containsExactly(2, 2, 9, 3, 9, 2, 1, 2, 2, 9);
  }
}
