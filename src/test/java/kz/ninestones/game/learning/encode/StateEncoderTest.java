package kz.ninestones.game.learning.encode;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

public class StateEncoderTest {
    @Test
    public void oneHotShouldReturnArray() {
        assertThat(StateEncoder.oneHot(0)).isEqualTo(new double[] {1, 0, 0, 0, 0, 0, 0, 0, 0});
        assertThat(StateEncoder.oneHot(9)).isEqualTo(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0});
        assertThat(StateEncoder.oneHot(8)).isEqualTo(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 1});
    }
}
