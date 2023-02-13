package kz.ninestones.game.core;

public enum Player {

  ONE(0), TWO(1);

  static {
    ONE.opponent = TWO;
    TWO.opponent = ONE;
  }

  public final int index;
  public Player opponent;

  Player(int index) {
    this.index = index;
  }

  public int boardCell(int move) {
    if (this == ONE) {
      return 9 - move;
    }

    return 8 + move;
  }
}
