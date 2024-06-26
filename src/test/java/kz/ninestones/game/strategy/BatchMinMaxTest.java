package kz.ninestones.game.strategy;

import com.google.common.truth.Truth;
import java.util.ArrayList;
import java.util.List;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import org.apache.beam.sdk.util.SerializableUtils;
import org.junit.Test;

public class BatchMinMaxTest {

  @Test
  public void regressionTest() {
    BatchMinMaxStrategy batch = new BatchMinMaxStrategy();
    State state = new State();

    List<Integer> moves = new ArrayList<>(10);

    for (int i = 0; i < 20; i++) {
      int batchMove = batch.suggestNextMove(state);
      moves.add(batchMove);
      state = Policy.makeMove(state, batchMove);
    }

    Truth.assertThat(moves).hasSize(20);
  }

  @Test
  public void shouldBeSerializable() {
    SerializableUtils.ensureSerializable(new BatchMinMaxStrategy());
  }
}
