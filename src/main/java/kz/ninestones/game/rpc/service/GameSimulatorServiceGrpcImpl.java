package kz.ninestones.game.rpc.service;

import com.google.mu.util.stream.BiStream;
import io.grpc.stub.StreamObserver;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;
import kz.ninestones.game.modeling.strategy.Strategies;
import kz.ninestones.game.proto.Game;
import kz.ninestones.game.proto.GameSimulatorServiceGrpc;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.simulation.SimulationResult;

public class GameSimulatorServiceGrpcImpl
    extends GameSimulatorServiceGrpc.GameSimulatorServiceImplBase {

  private final GameSimulator gameSimulator =
      new GameSimulator(Strategies.MIN_MAX_SCORE_DIFF, Strategies.MIN_MAX_SCORE_DIFF);

  /*
   * We observe here that some words have an "@", this are Annotations. Annotations are used to provide supplement
   * information about a program. We can autogenerate this functions, in Intellij we can use the shortcut ctrl + O to
   * do this.
   * */
  @Override
  public void playOut(
      Game.GameSimulatorRequest request,
      StreamObserver<Game.GameSimulatorResponse> responseObserver) {

    SimulationResult simulations =
        gameSimulator.playOut(new State(request.getInitialState()), request.getNumSimulations());

    Game.GameSimulatorResponse.Builder response = Game.GameSimulatorResponse.newBuilder();

    BiStream.from(simulations.getObservedWinners().asMap())
        .mapKeys(Player::name)
        .forEach(response::putObservedWinners);

    response.setLabel(request.getLabel());

    /* We can call multiple times onNext function if we have multiple replies, ex. in next commits */
    responseObserver.onNext(response.build());
    /* We use the response observer's onCompleted method to specify that we've finished dealing with the RPC */
    responseObserver.onCompleted();
  }
}
