package kz.ninestones.game.rpc;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import kz.ninestones.game.core.State;
import kz.ninestones.game.proto.Game;
import kz.ninestones.game.proto.GameSimulatorServiceGrpc;
import org.apache.commons.lang3.time.StopWatch;

public class Client {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    /* *
     * Establish a connection to the server using the class ManagedChannelBuilder and the function usePlaintext().
     * usePlainText() should only be used for testing or for APIs where the use of such API or the data
     * exchanged is not sensitive.
     * */

    //    ManagedChannel channel =
    //        ManagedChannelBuilder.forAddress("192.168.0.80", 8999).usePlaintext().build();

    NameResolver.Factory nameResolverFactory =
        new MultiAddressNameResolverFactory(
            new InetSocketAddress("192.168.0.80", 8999),
            new InetSocketAddress("192.168.0.62", 8999) // ,
            //            new InetSocketAddress("localhost", 8999)
            );

    ManagedChannel channel =
        ManagedChannelBuilder.forTarget("service")
            .nameResolverFactory(nameResolverFactory)
            .defaultLoadBalancingPolicy("round_robin")
            .usePlaintext()
            .build();

    /* *
     * A blocking-style stub instance of Greeter service. We can have two types of stubs: blocking and async.
     * Blocking stubs are synchronous. Non-blocking stubs are asynchronous.
     * Take care if you want to call a rpc function on a blocking stub from UI thread
     * (cause an unresponsive/laggy UI).
     * */
    GameSimulatorServiceGrpc.GameSimulatorServiceFutureStub simulatorServiceFutureStub =
        GameSimulatorServiceGrpc.newFutureStub(channel);

    /* *
     * Asynchronous instance of the above declaration.
     * GreeterGrpc.GreeterStub bookStub = GreeterGrpc.newStub(channel);
     * */

    /* *
     * We can now use gRPC function via our instance of GreeterBlockingStub bookStub.
     * Below we call the function sayHello(Helloworld.HelloRequest request) with name field value set to "gRPC".
     * This function return a value of type  Helloworld.HelloReply that is saved in our instance reply.
     * We can get via generated functions every field from our message, in this case we have just one field.
     * */

    StopWatch stopWatch = StopWatch.createStarted();

    List<ListenableFuture<Game.GameSimulatorResponse>> replies = new ArrayList<>();
    for (int i = 0; i < 24; i++) {
      replies.add(
          simulatorServiceFutureStub.playOut(
              Game.GameSimulatorRequest.newBuilder()
                  .setLabel(i)
                  .setNumSimulations(100)
                  .setInitialState(new State().toProto())
                  .build()));
    }

    System.out.println(stopWatch.formatTime());

    Futures.allAsList(replies).get().forEach(reply -> System.out.println(reply));

    System.out.println(stopWatch.formatTime());

    channel.shutdown();
  }

  private static class MultiAddressNameResolverFactory extends NameResolver.Factory {

    final List<EquivalentAddressGroup> addresses;

    MultiAddressNameResolverFactory(SocketAddress... addresses) {
      this.addresses =
          Arrays.stream(addresses).map(EquivalentAddressGroup::new).collect(Collectors.toList());
    }

    public NameResolver newNameResolver(URI notUsedUri, NameResolver.Args args) {
      return new NameResolver() {

        @Override
        public String getServiceAuthority() {
          return "fakeAuthority";
        }

        public void start(Listener2 listener) {
          listener.onResult(
              ResolutionResult.newBuilder()
                  .setAddresses(addresses)
                  .setAttributes(Attributes.EMPTY)
                  .build());
        }

        public void shutdown() {}
      };
    }

    @Override
    public String getDefaultScheme() {
      return "multiaddress";
    }
  }
}
