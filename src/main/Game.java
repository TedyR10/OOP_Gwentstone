package main;

import fileio.ActionsInput;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Input;

import java.util.ArrayList;

/**
 * initialize game settings for the current game
 */
public class Game {

    private int playerOneDeckIdx;
    private int playerTwoDeckIdx;
    private int shuffleSeed;
    private CardInput playerOneHero;
    private CardInput playerTwoHero;
    private int startingPlayer;
    private ArrayList<ActionsInput> actions;

    /**
     * constructor
     */
    public Game(GameInput game) {
        this.playerOneDeckIdx = game.getStartGame().getPlayerOneDeckIdx();
        this.playerTwoDeckIdx = game.getStartGame().getPlayerTwoDeckIdx();
        this.shuffleSeed = game.getStartGame().getShuffleSeed();
        this.playerOneHero = game.getStartGame().getPlayerOneHero();
        this.playerTwoHero = game.getStartGame().getPlayerTwoHero();
        this.startingPlayer = game.getStartGame().getStartingPlayer();
        this.actions = game.getActions();
    }

    /**
     * gets actions
     */
    public ArrayList<ActionsInput> getActions() {
        return actions;
    }
    /**
     * sets actions
     */
    public void setActions(ArrayList<ActionsInput> actions) {
        this.actions = actions;
    }
    /**
     * gets PlayerOneDeckIdx
     */
    public int getPlayerOneDeckIdx() {
        return playerOneDeckIdx;
    }
    /**
     * sets PlayerOneDeckIdx
     */
    public void setPlayerOneDeckIdx(final int playerOneDeckIdx) {
        this.playerOneDeckIdx = playerOneDeckIdx;
    }
    /**
     * gets PlayerTwoDeckIdx
     */
    public int getPlayerTwoDeckIdx() {
        return playerTwoDeckIdx;
    }
    /**
     * sets PlayerTwoDeckIdx
     */
    public void setPlayerTwoDeckIdx(final int playerTwoDeckIdx) {
        this.playerTwoDeckIdx = playerTwoDeckIdx;
    }
    /**
     * gets shuffleSeed
     */
    public int getShuffleSeed() {
        return shuffleSeed;
    }
    /**
     * sets shuffleSeed
     */
    public void setShuffleSeed(final int shuffleSeed) {
        this.shuffleSeed = shuffleSeed;
    }
    /**
     * gets PlayerOneHero
     */
    public CardInput getPlayerOneHero() {
        return playerOneHero;
    }
    /**
     * sets PlayerOneHero
     */
    public void setPlayerOneHero(final CardInput playerOneHero) {
        this.playerOneHero = playerOneHero;
    }
    /**
     * gets PlayerTwoHero
     */
    public CardInput getPlayerTwoHero() {
        return playerTwoHero;
    }
    /**
     * sets PlayerTwoHero
     */
    public void setPlayerTwoHero(final CardInput playerTwoHero) {
        this.playerTwoHero = playerTwoHero;
    }
    /**
     * gets StartingPlayer
     */
    public int getStartingPlayer() {
        return startingPlayer;
    }
    /**
     * sets StartingPlayer
     */
    public void setStartingPlayer(final int startingPlayer) {
        this.startingPlayer = startingPlayer;
    }
    /**
     * toString method used for debugging purposes
     */
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
