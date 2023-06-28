package kz.ninestones.game.core;

public enum Player {
  ONE,
  TWO,
  NONE;

  static {
    ONE.opponent = TWO;
    TWO.opponent = ONE;
    NONE.opponent = NONE;
  }

  public Player opponent;

  public int boardCell(int move) {
    if (this == ONE) {
      return 9 - move;
    }

    return 8 + move;
  }
}
