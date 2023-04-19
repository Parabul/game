package kz.ninestones.game.api;

import kz.ninestones.game.core.Player;
import kz.ninestones.game.core.State;

import java.io.Serializable;

public class GameStateResponse implements Serializable {

    public String nextMovePlayer;
    private int[] cells;
    private int playerOneScore;
    private int playerTwoScore;
    private int playerOneSpecialCell;
    private int playerTwoSpecialCell;

    public GameStateResponse() {
    }

    public GameStateResponse(State state) {
        this.cells = state.cells;
        this.playerOneScore = state.score.get(Player.ONE);
        this.playerTwoScore = state.score.get(Player.TWO);
        this.playerOneSpecialCell = state.specialCells.getOrDefault(Player.ONE, -1);
        this.playerTwoSpecialCell = state.specialCells.getOrDefault(Player.TWO, -1);
        this.nextMovePlayer = state.nextMove.name();
    }

    public int[] getCells() {
        return cells;
    }

    public void setCells(int[] cells) {
        this.cells = cells;
    }

    public int getPlayerOneScore() {
        return playerOneScore;
    }

    public void setPlayerOneScore(int playerOneScore) {
        this.playerOneScore = playerOneScore;
    }

    public int getPlayerTwoScore() {
        return playerTwoScore;
    }

    public void setPlayerTwoScore(int playerTwoScore) {
        this.playerTwoScore = playerTwoScore;
    }

    public int getPlayerOneSpecialCell() {
        return playerOneSpecialCell;
    }

    public void setPlayerOneSpecialCell(int playerOneSpecialCell) {
        this.playerOneSpecialCell = playerOneSpecialCell;
    }

    public int getPlayerTwoSpecialCell() {
        return playerTwoSpecialCell;
    }

    public void setPlayerTwoSpecialCell(int playerTwoSpecialCell) {
        this.playerTwoSpecialCell = playerTwoSpecialCell;
    }

    public String getNextMovePlayer() {
        return nextMovePlayer;
    }

    public void setNextMovePlayer(String nextMovePlayer) {
        this.nextMovePlayer = nextMovePlayer;
    }
}
