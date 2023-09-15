package kz.ninestones.game.simulation;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import kz.ninestones.game.modeling.strategy.MonteCarloTreeNode;
import kz.ninestones.game.proto.Game;
import kz.ninestones.game.proto.GameSimulatorGrpc;

public class DistributedMonteCarloPlayOutSimulator implements MonteCarloPlayOutSimulator {

  private final GameSimulatorGrpc.GameSimulatorFutureStub simulatorServiceFutureStub;

  public DistributedMonteCarloPlayOutSimulator() {
    NameResolver.Factory nameResolverFactory =
        new MultiAddressNameResolverFactory(
            new InetSocketAddress("192.168.0.62", 8999),
            new InetSocketAddress("192.168.0.101", 8999));

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
    simulatorServiceFutureStub = GameSimulatorGrpc.newFutureStub(channel);
  }

  @Override
  public void playOut(MonteCarloTreeNode currentNode) {

    List<ListenableFuture<Game.GameSimulatorResponse>> replies = new ArrayList<>();

    final Map<Integer, MonteCarloTreeNode> childNodeByMove = new HashMap<>();

    for (MonteCarloTreeNode childNode : currentNode.getChildren()) {

      childNodeByMove.put(childNode.getMove(), childNode);

      replies.add(
          simulatorServiceFutureStub.playOut(
              Game.GameSimulatorRequest.newBuilder()
                  .setLabel(childNode.getMove())
                  .setNumSimulations(Long.valueOf(getNumsimulations()).intValue())
                  .setInitialState(childNode.getState().toProto())
                  .build()));
    }

    try {
      Futures.allAsList(replies)
          .get()
          .forEach(
              reply -> {
                MonteCarloTreeNode childNode = childNodeByMove.get(reply.getLabel());

                SimulationResult simulationResult =
                    new SimulationResult(reply.getObservedWinnersMap());
                childNode.update(simulationResult);
                childNode.backPropagate(simulationResult);
              });
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
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
