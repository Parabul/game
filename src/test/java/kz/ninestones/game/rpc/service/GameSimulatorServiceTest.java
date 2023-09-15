package kz.ninestones.game.rpc.service;

import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableList;
import io.grpc.stub.StreamObserver;
import kz.ninestones.game.core.State;
import kz.ninestones.game.proto.Game;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class GameSimulatorServiceTest {

  @Mock private StreamObserver<Game.HelloResponse> helloResponseObserver;

  @Mock private StreamObserver<Game.PolicyResponse> policyResponseObserver;

  @Mock private StreamObserver<Game.SuggestionResponse> suggestionResponseObserver;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldSayHello() {
    GameSimulatorService simulatorServiceGrpc = new GameSimulatorService();

    simulatorServiceGrpc.sayHello(
        Game.HelloRequest.newBuilder().setName("Alem").build(), helloResponseObserver);

    verify(helloResponseObserver)
        .onNext(Mockito.eq(Game.HelloResponse.newBuilder().setMessage("Salem, Alem").build()));
    verify(helloResponseObserver).onCompleted();
  }

  @Test
  public void shouldProvideDefaultState() {
    GameSimulatorService simulatorServiceGrpc = new GameSimulatorService();

    simulatorServiceGrpc.move(Game.PolicyRequest.newBuilder().build(), policyResponseObserver);

    verify(policyResponseObserver)
        .onNext(
            Mockito.eq(
                Game.PolicyResponse.newBuilder()
                    .setCurrentState((new State()).toProto())
                    .setGameStatus(Game.GameStatus.ACTIVE)
                    .build()));
    verify(policyResponseObserver).onCompleted();
  }

  @Test
  public void shouldProvideStateAfterFewMoves() {
    GameSimulatorService simulatorServiceGrpc = new GameSimulatorService();

    simulatorServiceGrpc.move(
        Game.PolicyRequest.newBuilder().addMoves(9).addMoves(7).build(), policyResponseObserver);

    verify(policyResponseObserver)
        .onNext(
            Mockito.eq(
                Game.PolicyResponse.newBuilder()
                    .setCurrentState(
                        Game.StateProto.newBuilder()
                            .setNextMove(Game.PlayerProto.ONE)
                            .putScore("ONE", 10)
                            .putScore("TWO", 10)
                            .addAllCells(
                                ImmutableList.of(
                                    1, 9, 0, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 1, 1,
                                    10))
                            .build())
                    .setGameStatus(Game.GameStatus.ACTIVE)
                    .build()));
    verify(policyResponseObserver).onCompleted();
  }

  @Test
  public void shouldSuggestNextMove() {
    GameSimulatorService simulatorServiceGrpc = new GameSimulatorService();

    simulatorServiceGrpc.suggest(
        Game.SuggestionRequest.newBuilder().addMoves(9).addMoves(7).build(),
        suggestionResponseObserver);

    verify(suggestionResponseObserver)
        .onNext(Mockito.eq(Game.SuggestionResponse.newBuilder().setMove(1).build()));

    verify(suggestionResponseObserver).onCompleted();
  }
}
