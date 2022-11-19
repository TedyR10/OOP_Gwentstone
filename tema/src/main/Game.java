package main;

import fileio.ActionsInput;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Input;

import java.util.ArrayList;

public class Game {

    private int playerOneDeckIdx;
    private int playerTwoDeckIdx;
    private int shuffleSeed;
    private CardInput playerOneHero;
    private CardInput playerTwoHero;
    private int startingPlayer;
    private ArrayList<ActionsInput> actions;

    public Game(GameInput game) {
        this.playerOneDeckIdx = game.getStartGame().getPlayerOneDeckIdx();
        this.playerTwoDeckIdx = game.getStartGame().getPlayerTwoDeckIdx();
        this.shuffleSeed = game.getStartGame().getShuffleSeed();
        this.playerOneHero = game.getStartGame().getPlayerOneHero();
        this.playerTwoHero = game.getStartGame().getPlayerTwoHero();
        this.startingPlayer = game.getStartGame().getStartingPlayer();
        this.actions = game.getActions();
    }

    public ArrayList<ActionsInput> getActions() {
        return actions;
    }

    public void setActions(ArrayList<ActionsInput> actions) {
        this.actions = actions;
    }

    public int getPlayerOneDeckIdx() {
        return playerOneDeckIdx;
    }

    public void setPlayerOneDeckIdx(final int playerOneDeckIdx) {
        this.playerOneDeckIdx = playerOneDeckIdx;
    }

    public int getPlayerTwoDeckIdx() {
        return playerTwoDeckIdx;
    }

    public void setPlayerTwoDeckIdx(final int playerTwoDeckIdx) {
        this.playerTwoDeckIdx = playerTwoDeckIdx;
    }

    public int getShuffleSeed() {
        return shuffleSeed;
    }

    public void setShuffleSeed(final int shuffleSeed) {
        this.shuffleSeed = shuffleSeed;
    }

    public CardInput getPlayerOneHero() {
        return playerOneHero;
    }

    public void setPlayerOneHero(final CardInput playerOneHero) {
        this.playerOneHero = playerOneHero;
    }

    public CardInput getPlayerTwoHero() {
        return playerTwoHero;
    }

    public void setPlayerTwoHero(final CardInput playerTwoHero) {
        this.playerTwoHero = playerTwoHero;
    }

    public int getStartingPlayer() {
        return startingPlayer;
    }

    public void setStartingPlayer(final int startingPlayer) {
        this.startingPlayer = startingPlayer;
    }

    @Override
    public String toString() {
        return "StartGameInput{"
                + "playerOneDeckIdx="
                + playerOneDeckIdx
                + ", playerTwoDeckIdx="
                + playerTwoDeckIdx
                + ", shuffleSeed="
                + shuffleSeed
                +  ", playerOneHero="
                + playerOneHero
                + ", playerTwoHero="
                + playerTwoHero
                + ", startingPlayer="
                + startingPlayer
                + '}';
    }
}
