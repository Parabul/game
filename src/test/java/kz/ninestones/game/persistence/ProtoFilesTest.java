package kz.ninestones.game.persistence;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import kz.ninestones.game.core.State;
import kz.ninestones.game.proto.Game;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.utils.ProtoFiles;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ProtoFilesTest {

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void shouldWriteAndReadFiles() throws IOException {

    final File tempFile = tempFolder.newFile("tempFile.dat");

    assertThat(tempFile.getAbsolutePath()).isNotEmpty();

    assertThat(tempFile.length()).isEqualTo(0L);

    ImmutableList<Game.StateProto> messages =
        IntStream.range(0, 10)
            .mapToObj(i -> GameSimulator.randomState())
            .map(State::toProto)
            .collect(ImmutableList.toImmutableList());

    ProtoFiles.write(tempFile.getAbsolutePath(), messages);

    assertThat(tempFile.length()).isGreaterThan(1L);

    List<Game.StateProto> writtenMessages =
        ProtoFiles.read(tempFile.getAbsolutePath(), Game.StateProto.getDefaultInstance());

    assertThat(writtenMessages).containsExactlyElementsIn(messages);
  }
}
