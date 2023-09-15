package kz.ninestones.game.persistence;

import com.google.protobuf.Message;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProtoFiles {
  public static <T extends Message> void write(String file, Collection<T> messages)
      throws IOException {
    System.out.println("Writing " + messages.size() + " messages to " + file);

    File dest = new File(file);
    dest.createNewFile();

    try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(dest))) {
      outputStream.writeInt(messages.size());

      boolean first = true;
      for (T message : messages) {
        if (first) {
          System.out.println(message);
          first = false;
        }
        byte[] messageBytes = message.toByteArray();
        outputStream.writeInt(messageBytes.length);
        outputStream.write(messageBytes);
      }
    }
  }

  public static <T extends Message> List<T> read(String file, T defaultInstance) {
    try (DataInputStream inputStream = new DataInputStream(new FileInputStream(file))) {

      int n = inputStream.readInt();

      List<T> messages = new ArrayList<>(n);

      for (int i = 0; i < n; i++) {
        byte[] messageBytes = new byte[inputStream.readInt()];
        inputStream.readFully(messageBytes);
        messages.add((T) defaultInstance.toBuilder().mergeFrom(messageBytes).build());
      }

      return messages;
    } catch (IOException ex) {
      throw new RuntimeException("File: " + file, ex);
    }
  }

  public static List<byte[]> read(ReadableByteChannel channel) {
    try (DataInputStream inputStream = new DataInputStream(Channels.newInputStream(channel))) {

      int n = inputStream.readInt();

      List<byte[]> messages = new ArrayList<>(n);

      for (int i = 0; i < n; i++) {
        byte[] messageBytes = new byte[inputStream.readInt()];
        inputStream.readFully(messageBytes);
        messages.add(messageBytes);
      }

      return messages;
    } catch (IOException ex) {
      throw new RuntimeException("Channel read failed ", ex);
    }
  }
}
