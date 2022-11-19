package main;

import Minions.Hero;
import fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameSetup {
    private Player activePlayer1 = new Player();
    private Player activePlayer2 = new Player();

    public GameSetup(Game gameInput, Player player1, Player player2) {
        activePlayer1.setCurrentDeckInputs(player1.getDecks().get(gameInput.getPlayerOneDeckIdx()));
        activePlayer2.setCurrentDeckInputs(player2.getDecks().get(gameInput.getPlayerTwoDeckIdx()));
        // solve randomize/ solved?>
        Random rand = new Random(gameInput.getShuffleSeed());
        Collections.shuffle(activePlayer1.getCurrentDeck(), rand);
        Random rand1 = new Random(gameInput.getShuffleSeed());
        Collections.shuffle(activePlayer2.getCurrentDeck(), rand1);

        //hero setup
        activePlayer1.setupHero(gameInput.getPlayerOneHero());
        activePlayer2.setupHero(gameInput.getPlayerTwoHero());

    }

    public Player getActivePlayer1() {
        return activePlayer1;
    }

    public void setActivePlayer1(Player activePlayer1) {
        this.activePlayer1 = activePlayer1;
    }

    public Player getActivePlayer2() {
        return activePlayer2;
    }

    public void setActivePlayer2(Player activePlayer2) {
        this.activePlayer2 = activePlayer2;
    }
}
