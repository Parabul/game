package kz.ninestones.game.core;

public enum Player {

  ONE(0), TWO(1);

  static {
    ONE.next = TWO;
    TWO.next = ONE;
  }

  public Player next;
  public final int index;

  Player(int index) {
    this.index = index;
  }

  public int boardCell(int cell) {
    if (this == ONE) {
      return 9 - cell;
    }

    return 8 + cell;
  }
}
