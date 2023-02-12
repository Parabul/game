package kz.ninestones.game.core;

public class Runner {


  public static void main(String[] args){
    System.out.println("Hello");

    State state = new State();

    System.out.println(state);

    State newState = Policy.makeMove(Player.ONE, state, 7);

    System.out.println(newState);

    State newNewState = Policy.makeMove(Player.TWO, newState, 6);

    System.out.println(newNewState);
  }
}
