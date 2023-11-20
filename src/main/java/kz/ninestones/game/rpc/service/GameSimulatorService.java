package kz.ninestones.game.rpc.service;

import com.google.mu.util.stream.BiStream;
import io.grpc.stub.StreamObserver;
import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import kz.ninestones.game.modeling.strategy.Strategies;
import kz.ninestones.game.proto.Game;
import kz.ninestones.game.proto.GameSimulatorGrpc;
import kz.ninestones.game.simulation.GameSimulator;
import kz.ninestones.game.simulation.SimulationResult;

public class GameSimulatorService extends GameSimulatorGrpc.GameSimulatorImplBase {

  private final GameSimulator gameSimulator =
      new GameSimulator(Strategies.MIN_MAX_SCORE_DIFF, Strategies.MIN_MAX_SCORE_DIFF);

  @Override
  public void playOut(
      Game.GameSimulatorRequest request,
      StreamObserver<Game.GameSimulatorResponse> responseObserver) {

    SimulationResult simulations =
        gameSimulator.playOut(new State(request.getInitialState()), request.getNumSimulations());

    Game.GameSimulatorResponse.Builder response = Game.GameSimulatorResponse.newBuilder();

    BiStream.from(simulations.getObservedWinners())
        .mapKeys(Player::name)
        .forEach(response::putObservedWinners);

    response.setLabel(request.getLabel());

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  @Override
  public void sayHello(
      Game.HelloRequest request, StreamObserver<Game.HelloResponse> responseObserver) {

    responseObserver.onNext(
        Game.HelloResponse.newBuilder().setMessage("Salem, " + request.getName()).build());
    responseObserver.onCompleted();
  }

  @Override
  public void move(
      Game.PolicyRequest request, StreamObserver<Game.PolicyResponse> responseObserver) {
    State state = new State();

    Game.PolicyResponse.Builder response = Game.PolicyResponse.newBuilder();

    for (Integer move : request.getMovesList()) {
      state = Policy.makeMove(state, move);
    }

    response.setCurrentState(state.toProto());

    if (Policy.isGameOver(state)) {
      switch (Policy.winnerOf(state).get()) {
        case ONE:
          response.setGameStatus(Game.GameStatus.GAME_OVER_PLAYER_ONE_WON);
          break;
        case TWO:
          response.setGameStatus(Game.GameStatus.GAME_OVER_PLAYER_TWO_WON);
          break;
        default:
          response.setGameStatus(Game.GameStatus.GAME_OVER_TIE);
      }
    } else {
      response.setGameStatus(Game.GameStatus.ACTIVE);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  @Override
  public void suggest(
      Game.SuggestionRequest request, StreamObserver<Game.SuggestionResponse> responseObserver) {
    State state = new State();

    for (Integer move : request.getMovesList()) {
      state = Policy.makeMove(state, move);
    }

    responseObserver.onNext(
        Game.SuggestionResponse.newBuilder()
            .setMove(Strategies.TENSOR_FLOW.suggestNextMove(state))
            .build());
    responseObserver.onCompleted();
  }
}
