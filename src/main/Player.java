package main;

import minions.*;
import fileio.CardInput;
import fileio.Input;

import java.util.ArrayList;
import java.util.Objects;

/**
 * initialize players & their respective decks
 */
public class Player {
    private int nrCardsInDeck;
    private int nrDecks;
    private ArrayList<ArrayList<CardInput>> decks;
    private ArrayList<CardInput> currentDeckInputs;
    private ArrayList<Card> currentDeck = new ArrayList<Card>();
    private ArrayList<Card> hand = new ArrayList<Card>();
    private int mana = 1;
    private int turn = 1;
    private Hero hero = new Hero();
    boolean endTurn = false;

    public Player(){

    }
    /**
     * constructor for players
     */
    public Player(Input input, int number) {
        if (number == 1) {
            this.nrCardsInDeck = input.getPlayerOneDecks().getNrCardsInDeck();
            this.nrDecks = input.getPlayerOneDecks().getNrDecks();
            this.decks = input.getPlayerOneDecks().getDecks();
        }
        else {
            this.nrCardsInDeck = input.getPlayerTwoDecks().getNrCardsInDeck();
            this.nrDecks = input.getPlayerTwoDecks().getNrDecks();
            this.decks = input.getPlayerTwoDecks().getDecks();
        }
    }

    /**
     * sets the mana for the next round & draws a card
     */
    public void newTurn() {
        if (this.turn < 10)
            ++this.turn;
        int newMana = this.mana + this.turn;
        this.setMana(newMana);
        if (!(this.currentDeck.isEmpty())) {
            hand.add(this.currentDeck.get(0));
            getCurrentDeck().remove(0);
        }

    }

    /**
     * gets endTurn
     */
    public boolean isEndTurn() {
        return endTurn;
    }

    /**
     * sets endTurn
     */
    public void setEndTurn(boolean endTurn) {
        this.endTurn = endTurn;
    }

    /**
     * draw a card from the deck
     */
    public void drawCard() {
        if (!(this.currentDeck.isEmpty())) {
            hand.add(this.currentDeck.get(0));
            getCurrentDeck().remove(0);
        }
    }

    /**
     * gets endTurn
     */
    public int getMana() {
        return mana;
    }
    /**
     * sets endTurn
     */
    public void setMana(int mana) {
        this.mana = mana;
    }
    /**
     * gets hand
     */
    public ArrayList<Card> getHand() {
        return hand;
    }
    /**
     * sets turn
     */
    public void setTurn(int turn) {
        this.turn = turn;
    }
    /**
     * gets turn
     */
    public int getTurn() {
        return turn;
    }
    /**
     * increases turn
     */
    public void increaseTurn() {
        this.turn++;
    }
    /**
     * gets hero
     */
    public Hero getHero() {
        return hero;
    }
    /**
     * sets up hero
     */
    public void setupHero(CardInput cardHero) {
        this.hero.setMana(cardHero.getMana());
        this.hero.setDescription(cardHero.getDescription());
        this.hero.setName(cardHero.getName());
        this.hero.setColors(cardHero.getColors());
        this.hero.setSpecialAbility(cardHero.getName());
    }
    /**
     * gets as input the chosen deck & initializes it for our player with the required classes for each card
     */
    public void setCurrentDeckInputs(ArrayList<CardInput> currentDeckInputs) {
        this.currentDeckInputs = currentDeckInputs;
        for (int i = 0; i < currentDeckInputs.size(); i++) {
            CardInput cardInput = currentDeckInputs.get(i);
            if ((Objects.equals(cardInput.getName(), "Firestorm")) || (Objects.equals(cardInput.getName(), "Winterfell")) ||
                    (Objects.equals(cardInput.getName(), "Heart Hound"))) {
                Environment environmentCard = new Environment(cardInput.getMana(), cardInput.getDescription(),
                        cardInput.getColors(), cardInput.getName());
                this.currentDeck.add(environmentCard);
            }
            else {
                if ((Objects.equals(cardInput.getName(), "Sentinel"))) {
                    Sentinel sentinel = new Sentinel(cardInput.getMana(), cardInput.getAttackDamage(),
                            cardInput.getHealth(), cardInput.getDescription(), cardInput.getColors(),
                            cardInput.getName(), false, false);
                    this.currentDeck.add(sentinel);
                }
                else if ((Objects.equals(cardInput.getName(), "Berserker"))) {
                    Berserker berserker = new Berserker(cardInput.getMana(), cardInput.getAttackDamage(),
                            cardInput.getHealth(), cardInput.getDescription(), cardInput.getColors(),
                            cardInput.getName(), false, false);
                    this.currentDeck.add(berserker);
                }
                else if ((Objects.equals(cardInput.getName(), "Goliath"))) {
                    Goliath goliath = new Goliath(cardInput.getMana(), cardInput.getAttackDamage(),
                            cardInput.getHealth(), cardInput.getDescription(), cardInput.getColors(),
                            cardInput.getName(), false, false);
                    this.currentDeck.add(goliath);
                }
                else if ((Objects.equals(cardInput.getName(), "Warden"))) {
                    Warden warden = new Warden(cardInput.getMana(), cardInput.getAttackDamage(),
                            cardInput.getHealth(), cardInput.getDescription(), cardInput.getColors(),
                            cardInput.getName(), false, false);
                    this.currentDeck.add(warden);
                }
                else if ((Objects.equals(cardInput.getName(), "The Ripper"))) {
                    TheRipper theRipper = new TheRipper(cardInput.getMana(), cardInput.getAttackDamage(),
                            cardInput.getHealth(), cardInput.getDescription(), cardInput.getColors(),
                            cardInput.getName(), false, false);
                    this.currentDeck.add(theRipper);
                }
                else if ((Objects.equals(cardInput.getName(), "Miraj"))) {
                    Miraj miraj = new Miraj(cardInput.getMana(), cardInput.getAttackDamage(),
                            cardInput.getHealth(), cardInput.getDescription(), cardInput.getColors(),
                            cardInput.getName(), false, false);
                    this.currentDeck.add(miraj);
                }
                else if ((Objects.equals(cardInput.getName(), "The Cursed One"))) {
                    TheCursedOne theCursedOne = new TheCursedOne(cardInput.getMana(), cardInput.getAttackDamage(),
                            cardInput.getHealth(), cardInput.getDescription(), cardInput.getColors(),
                            cardInput.getName(), false, false);
                    this.currentDeck.add(theCursedOne);
                }
                else if ((Objects.equals(cardInput.getName(), "Disciple"))) {
                    Disciple disciple = new Disciple(cardInput.getMana(), cardInput.getAttackDamage(),
                            cardInput.getHealth(), cardInput.getDescription(), cardInput.getColors(),
                            cardInput.getName(), false, false);
                    this.currentDeck.add(disciple);
                }
            }
        }
    }
    /**
     * gets CurrentDeck
     */
    public ArrayList<Card> getCurrentDeck() {
        return currentDeck;
    }
    /**
     * sets CurrentDeck
     */
    public void setCurrentDeck(ArrayList<Card> currentDeck) {
        this.currentDeck = currentDeck;
    }
    /**
     * gets NrCardsInDeck
     */
    public int getNrCardsInDeck() {
        return nrCardsInDeck;
    }
    /**
     * sets NrCardsInDeck
     */
    public void setNrCardsInDeck(final int nrCardsInDeck) {
        this.nrCardsInDeck = nrCardsInDeck;
    }
    /**
     * gets NrDecks
     */
    public int getNrDecks() {
        return nrDecks;
    }
    /**
     * sets NrDecks
     */
    public void setNrDecks(final int nrDecks) {
        this.nrDecks = nrDecks;
    }
    /**
     * gets Decks
     */
    public ArrayList<ArrayList<CardInput>> getDecks() {
        return decks;
    }
    /**
     * sets Decks
     */
    public void setDecks(final ArrayList<ArrayList<CardInput>> decks) {
        this.decks = decks;
    }
    /**
     * toString method used for debugging purposes
     */
    @Override
    public String toString() {
        return "InfoInput{"
                + "nr_cards_in_deck="
                + nrCardsInDeck
                +  ", nr_decks="
                + nrDecks
                + ", decks="
                + decks
                + '}';
    }
}

