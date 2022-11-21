//Copyright Theodor-Ioan Rolea 2022
package main;

import minions.*;
import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.Input;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);

        ArrayNode output = objectMapper.createArrayNode();

        //TODO add here the entry point to your implementation


        //initialize players & their respective decks
        Player startingPlayer1 = new Player(inputData, 1);
        Player startingPlayer2 = new Player(inputData, 2);
        //game stats
        int player1Wins = 0;
        int player2Wins = 0;
        int totalGamesPlayed = 0;


        //cycle through games
        for (int i = 0; i < inputData.getGames().size(); i++) {
            //set up the current game
            Game gameInput = new Game(inputData.getGames().get(i));
            //initialize the players & their decks
            GameSetup gameSetup = new GameSetup(gameInput, startingPlayer1, startingPlayer2);
            //defining the actions & the active players for the current game
            ArrayList<ActionsInput> actions = gameInput.getActions();
            Player activePlayer1 = gameSetup.getActivePlayer1();
            Player activePlayer2 = gameSetup.getActivePlayer2();
            //defining the table
            ArrayList<Card> player2Row0 = new ArrayList<Card>();
            ArrayList<Card> player2Row1 = new ArrayList<Card>();
            ArrayList<Card> player1Row2 = new ArrayList<Card>();
            ArrayList<Card> player1Row3 = new ArrayList<Card>();

            //used to determine if a hero is dead or not & which hero died
            boolean deadHero1 = false;
            boolean deadHero2 = false;
            int dead = 0;

            //playerTurn will tell us whose turn it is at any time
            int playerTurn = gameInput.getStartingPlayer();
            //the game starts here
            totalGamesPlayed++;
            activePlayer1.drawCard();
            activePlayer2.drawCard();
            ObjectNode node1 = objectMapper.createObjectNode();

            //cycle through the given actions
            for (ActionsInput action : actions) {

                ObjectNode node = objectMapper.createObjectNode();

                //we check if a hero is dead or not
                if (deadHero1 && dead == 0) {
                    node1.put("gameEnded", "Player two killed the enemy hero.");
                    output.add(node1);
                    dead = 1;
                    player2Wins++;
                }
                if (deadHero2 && dead == 0) {
                    node1.put("gameEnded", "Player one killed the enemy hero.");
                    output.add(node1);
                    dead = 1;
                    player1Wins++;
                }

                //gets the deck of the desired player
                if (Objects.equals(action.getCommand(), "getPlayerDeck")) {
                    node.put("command", action.getCommand());
                    int playerIdx = action.getPlayerIdx();
                    node.put("playerIdx", playerIdx);

                    if (playerIdx == 1) {
                        ArrayNode deck = objectMapper.createArrayNode();
                        //work-around to print only the required things by the checker
                        for (Card card : activePlayer1.getCurrentDeck()) {
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            if (card instanceof Minion) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("attackDamage", card.getAttackDamage());
                                cardNode.put("health", card.getHealth());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            if (card instanceof Environment) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            deck.add(cardNode);
                        }
                        node.set("output", deck);
                    }
                    if (playerIdx == 2) {
                        ArrayNode deck = objectMapper.createArrayNode();
                        for (Card card : activePlayer2.getCurrentDeck()) {
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            if (card instanceof Minion) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("attackDamage", card.getAttackDamage());
                                cardNode.put("health", card.getHealth());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            if (card instanceof Environment) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            deck.add(cardNode);
                        }
                        node.set("output", deck);
                    }
                    output.add(node);
                }
                //gets the hero of the desired player
                else if (Objects.equals(action.getCommand(), "getPlayerHero")) {
                    int playerIdx = action.getPlayerIdx();
                    node.put("command", action.getCommand());
                    node.put("playerIdx", playerIdx);

                    if (playerIdx == 1) {
                        //work-around to print only the required things by the checker
                        ObjectNode heroNode = objectMapper.createObjectNode();
                        heroNode.put("mana", activePlayer1.getHero().getMana());
                        heroNode.put("description", activePlayer1.getHero().getDescription());
                        heroNode.putPOJO("colors", activePlayer1.getHero().getColors());
                        heroNode.put("name", activePlayer1.getHero().getName());
                        heroNode.put("health", activePlayer1.getHero().getHealth());
                        node.set("output", heroNode);
                    }
                    else if (playerIdx == 2) {
                        ObjectNode heroNode = objectMapper.createObjectNode();
                        heroNode.put("mana", activePlayer2.getHero().getMana());
                        heroNode.put("description", activePlayer2.getHero().getDescription());
                        heroNode.putPOJO("colors", activePlayer2.getHero().getColors());
                        heroNode.put("name", activePlayer2.getHero().getName());
                        heroNode.put("health", activePlayer2.getHero().getHealth());
                        node.set("output", heroNode);
                    }
                    output.add(node);
                }
                //gets the current player's turn
                else if (Objects.equals(action.getCommand(), "getPlayerTurn")) {
                    node.put("command", action.getCommand());
                    if (playerTurn % 2 == 0)
                        node.put("output", 2);
                    else node.put("output", 1);
                    output.add(node);
                }
                //gets the cards in hand of the desired player
                else if (Objects.equals(action.getCommand(), "getCardsInHand")) {
                    node.put("command", action.getCommand());
                    node.put("playerIdx", action.getPlayerIdx());
                    ArrayNode hand = objectMapper.createArrayNode();
                    //work-around to print only the required things by the checker
                    if (action.getPlayerIdx() == 1) {
                        for (Card card : activePlayer1.getHand()) {
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            if (card instanceof Minion) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("attackDamage", card.getAttackDamage());
                                cardNode.put("health", card.getHealth());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            else if (card instanceof Environment) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            hand.add(cardNode);
                        }
                    }
                    else {
                        for (Card card : activePlayer2.getHand()) {
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            if (card instanceof Minion) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("attackDamage", card.getAttackDamage());
                                cardNode.put("health", card.getHealth());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            else if (card instanceof Environment) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            hand.add(cardNode);
                        }
                    }
                    node.set("output", hand);
                    output.add(node);
                }
                //gets the mana of the current player
                else if (Objects.equals(action.getCommand(), "getPlayerMana")) {
                    node.put("command", action.getCommand());
                    node.put("playerIdx", action.getPlayerIdx());
                    if (action.getPlayerIdx() == 2) {
                        node.put("output", activePlayer2.getMana());
                    }
                    else {
                        node.put("output", activePlayer1.getMana());
                    }
                    output.add(node);
                }
                //end the current player's turn
                else if (Objects.equals(action.getCommand(), "endPlayerTurn")) {
                    //we check whether a hero is dead or not
                    //if nobody died, move on to the next turn
                    if (!(deadHero2 || deadHero1)) {
                        if (playerTurn % 2 == 0) {
                            //set the endTurn true & set freeze & hasAttacked to false
                            activePlayer2.setEndTurn(true);
                            //start the new turn only if both players finished theirs
                            if (activePlayer1.isEndTurn() && activePlayer2.isEndTurn()) {
                                activePlayer1.newTurn();
                                activePlayer2.newTurn();
                                activePlayer1.setEndTurn(false);
                                activePlayer2.setEndTurn(false);
                                activePlayer1.getHero().setHasAttacked(false);
                                activePlayer2.getHero().setHasAttacked(false);
                            }
                            for (Card card : player2Row0) {
                                card.setFrozen(false);
                                card.setHasAttacked(false);
                            }
                            for (Card card : player2Row1) {
                                card.setFrozen(false);
                                card.setHasAttacked(false);
                            }
                        } else {
                            activePlayer1.setEndTurn(true);
                            if (activePlayer1.isEndTurn() && activePlayer2.isEndTurn()) {
                                activePlayer1.newTurn();
                                activePlayer2.newTurn();
                                activePlayer1.setEndTurn(false);
                                activePlayer2.setEndTurn(false);
                                activePlayer1.getHero().setHasAttacked(false);
                                activePlayer2.getHero().setHasAttacked(false);
                            }
                            for (Card card : player1Row2) {
                                card.setFrozen(false);
                                card.setHasAttacked(false);
                            }
                            for (Card card : player1Row3) {
                                card.setFrozen(false);
                                card.setHasAttacked(false);
                            }
                        }
                        playerTurn++;
                    }
                }
                //place a card on the table
                else if (Objects.equals(action.getCommand(), "placeCard")) {
                    int handIdx = action.getHandIdx();
                    if (playerTurn % 2 == 0) {
                        //we check whether the given index is good or not
                        if (handIdx < activePlayer2.getHand().size()) {
                            //check if the card is of type Environment; it cannot be placed on the table
                            if (activePlayer2.getHand().get(handIdx) instanceof Environment) {
                                node.put("command", action.getCommand());
                                node.put("handIdx", action.getHandIdx());
                                node.put("error", "Cannot place environment card on table.");
                                output.add(node);
                            }
                            //check if the card costs more mana that we currently have
                            else if (activePlayer2.getHand().get(handIdx).getMana() > activePlayer2.getMana()) {
                                node.put("command", action.getCommand());
                                node.put("handIdx", action.getHandIdx());
                                node.put("error", "Not enough mana to place card on table.");
                                output.add(node);
                            } else {
                                //if the card is of type TheRipper, Miraj, Goliath or Warden they have to be put on the front row
                                if (activePlayer2.getHand().get(handIdx) instanceof TheRipper ||
                                        activePlayer2.getHand().get(handIdx) instanceof Miraj ||
                                        activePlayer2.getHand().get(handIdx) instanceof Goliath ||
                                        activePlayer2.getHand().get(handIdx) instanceof Warden) {
                                    //check if the row is full or not
                                    if (player2Row1.size() < 5) {
                                        //add the card to the row, decrease the player's mana & eliminate the card from hand
                                        player2Row1.add(activePlayer2.getHand().get(handIdx));
                                        int previousMana = activePlayer2.getMana();
                                        int manaNext = previousMana - activePlayer2.getHand().get(handIdx).getMana();
                                        activePlayer2.setMana(manaNext);
                                        activePlayer2.getHand().remove(handIdx);
                                    }
                                    else {
                                        //error if the row is already full
                                        node.put("command", action.getCommand());
                                        node.put("handIdx", action.getHandIdx());
                                        node.put("error", "Cannot place card on table since row is full.");
                                        output.add(node);
                                    }
                                }
                                //if the card is of type Sentinel, Berserker, TheCursedOne or Disciple they have to be put on the back row
                                else if (activePlayer2.getHand().get(handIdx) instanceof Sentinel ||
                                        activePlayer2.getHand().get(handIdx) instanceof Berserker ||
                                        activePlayer2.getHand().get(handIdx) instanceof TheCursedOne ||
                                        activePlayer2.getHand().get(handIdx) instanceof Disciple) {
                                    //check if the row is full or not
                                    if (player2Row0.size() < 5) {
                                        //add the card to the row, decrease the player's mana & eliminate the card from hand
                                        player2Row0.add(activePlayer2.getHand().get(handIdx));
                                        int previousMana = activePlayer2.getMana();
                                        int manaNext = previousMana - activePlayer2.getHand().get(handIdx).getMana();
                                        activePlayer2.setMana(manaNext);
                                        activePlayer2.getHand().remove(handIdx);
                                    }
                                    else {
                                        //error if the row is already full
                                        node.put("command", action.getCommand());
                                        node.put("handIdx", action.getHandIdx());
                                        node.put("error", "Cannot place card on table since row is full.");
                                        output.add(node);
                                    }
                                }
                            }
                        }
                    }
                    //same logic for the other player
                    else {
                        if (handIdx < activePlayer1.getHand().size()) {
                            if (activePlayer1.getHand().get(handIdx) instanceof Environment) {
                                node.put("command", action.getCommand());
                                node.put("handIdx", action.getHandIdx());
                                node.put("error", "Cannot place environment card on table.");
                                output.add(node);
                            } else if (activePlayer1.getHand().get(handIdx).getMana() > activePlayer1.getMana()) {
                                node.put("command", action.getCommand());
                                node.put("handIdx", action.getHandIdx());
                                node.put("error", "Not enough mana to place card on table.");
                                output.add(node);
                            } else {
                                if (activePlayer1.getHand().get(handIdx) instanceof TheRipper ||
                                        activePlayer1.getHand().get(handIdx) instanceof Miraj ||
                                        activePlayer1.getHand().get(handIdx) instanceof Goliath ||
                                        activePlayer1.getHand().get(handIdx) instanceof Warden) {
                                    if (player1Row2.size() < 5) {
                                        player1Row2.add(activePlayer1.getHand().get(handIdx));
                                        int previousMana = activePlayer1.getMana();
                                        int manaNext = previousMana - activePlayer1.getHand().get(handIdx).getMana();
                                        activePlayer1.setMana(manaNext);
                                        activePlayer1.getHand().remove(handIdx);
                                    }
                                    else {
                                        node.put("command", action.getCommand());
                                        node.put("handIdx", action.getHandIdx());
                                        node.put("error", "Cannot place card on table since row is full.");
                                        output.add(node);
                                    }
                                }
                                else if (activePlayer1.getHand().get(handIdx) instanceof Sentinel ||
                                        activePlayer1.getHand().get(handIdx) instanceof Berserker ||
                                        activePlayer1.getHand().get(handIdx) instanceof TheCursedOne ||
                                        activePlayer1.getHand().get(handIdx) instanceof Disciple) {
                                    if (player1Row3.size() < 5) {
                                        player1Row3.add(activePlayer1.getHand().get(handIdx));
                                        int previousMana = activePlayer1.getMana();
                                        int manaNext = previousMana - activePlayer1.getHand().get(handIdx).getMana();
                                        activePlayer1.setMana(manaNext);

                                        activePlayer1.getHand().remove(handIdx);
                                    }
                                    else {
                                        node.put("command", action.getCommand());
                                        node.put("handIdx", action.getHandIdx());
                                        node.put("error", "Cannot place card on table since row is full.");
                                        output.add(node);
                                    }
                                }
                            }
                        }
                    }
                }
                //gets the cards on the table
                else if (Objects.equals(action.getCommand(), "getCardsOnTable")) {
                    node.put("command", action.getCommand());
                    ArrayNode table = objectMapper.createArrayNode();
                    ArrayNode row0 = objectMapper.createArrayNode();
                    ArrayNode row1 = objectMapper.createArrayNode();
                    ArrayNode row2 = objectMapper.createArrayNode();
                    ArrayNode row3 = objectMapper.createArrayNode();
                    //starts printing the cards from the top row to the bottom row
                    for (Card card : player2Row0) {
                        ObjectNode cardNode = objectMapper.createObjectNode();
                        if (card instanceof Minion) {
                            cardNode.put("mana", card.getMana());
                            cardNode.put("attackDamage", card.getAttackDamage());
                            cardNode.put("health", card.getHealth());
                            cardNode.put("description", card.getDescription());
                            cardNode.putPOJO("colors", card.getColors());
                            cardNode.put("name", card.getName());
                        }
                        else if (card instanceof Environment) {
                            cardNode.put("mana", card.getMana());
                            cardNode.put("description", card.getDescription());
                            cardNode.putPOJO("colors", card.getColors());
                            cardNode.put("name", card.getName());
                        }
                        row0.add(cardNode);
                    }
                    table.add(row0);
                    for (Card card : player2Row1) {
                        ObjectNode cardNode = objectMapper.createObjectNode();
                        if (card instanceof Minion) {
                            cardNode.put("mana", card.getMana());
                            cardNode.put("attackDamage", card.getAttackDamage());
                            cardNode.put("health", card.getHealth());
                            cardNode.put("description", card.getDescription());
                            cardNode.putPOJO("colors", card.getColors());
                            cardNode.put("name", card.getName());
                        }
                        else if (card instanceof Environment) {
                            cardNode.put("mana", card.getMana());
                            cardNode.put("description", card.getDescription());
                            cardNode.putPOJO("colors", card.getColors());
                            cardNode.put("name", card.getName());
                        }
                        row1.add(cardNode);
                    }
                    table.add(row1);
                    for (Card card : player1Row2) {
                        ObjectNode cardNode = objectMapper.createObjectNode();
                        if (card instanceof Minion) {
                            cardNode.put("mana", card.getMana());
                            cardNode.put("attackDamage", card.getAttackDamage());
                            cardNode.put("health", card.getHealth());
                            cardNode.put("description", card.getDescription());
                            cardNode.putPOJO("colors", card.getColors());
                            cardNode.put("name", card.getName());
                        }
                        else if (card instanceof Environment) {
                            cardNode.put("mana", card.getMana());
                            cardNode.put("description", card.getDescription());
                            cardNode.putPOJO("colors", card.getColors());
                            cardNode.put("name", card.getName());
                        }
                        row2.add(cardNode);
                    }
                    table.add(row2);
                    for (Card card : player1Row3) {
                        ObjectNode cardNode = objectMapper.createObjectNode();
                        if (card instanceof Minion) {
                            cardNode.put("mana", card.getMana());
                            cardNode.put("attackDamage", card.getAttackDamage());
                            cardNode.put("health", card.getHealth());
                            cardNode.put("description", card.getDescription());
                            cardNode.putPOJO("colors", card.getColors());
                            cardNode.put("name", card.getName());
                        }
                        else if (card instanceof Environment) {
                            cardNode.put("mana", card.getMana());
                            cardNode.put("description", card.getDescription());
                            cardNode.putPOJO("colors", card.getColors());
                            cardNode.put("name", card.getName());
                        }
                        row3.add(cardNode);
                    }
                    table.add(row3);
                    node.set("output", table);
                    output.add(node);
                }
                //gets the environment cards in the desired player's hand
                else if (Objects.equals(action.getCommand(), "getEnvironmentCardsInHand")) {
                    node.put("command", action.getCommand());
                    node.put("playerIdx", action.getPlayerIdx());
                    ArrayNode envrCards = objectMapper.createArrayNode();
                    if (action.getPlayerIdx() == 1) {
                        for (Card card : activePlayer1.getHand()) {
                            if (card instanceof Environment) {
                                ObjectNode cardNode = objectMapper.createObjectNode();
                                cardNode.put("mana", card.getMana());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                                envrCards.add(cardNode);
                            }
                        }
                        node.set("output", envrCards);
                        output.add(node);
                    }
                    else {
                        for (Card card : activePlayer2.getHand()) {
                            if (card instanceof Environment) {
                                ObjectNode cardNode = objectMapper.createObjectNode();
                                cardNode.put("mana", card.getMana());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                                envrCards.add(cardNode);
                            }
                        }
                        node.set("output", envrCards);
                        output.add(node);
                    }
                }
                //gets the card at the required position
                else if (Objects.equals(action.getCommand(), "getCardAtPosition")) {
                    node.put("command", action.getCommand());
                    node.put("x", action.getX());
                    node.put("y", action.getY());
                    if (action.getX() == 0) {
                        //checks if the index is correct
                        if (action.getY() < player2Row0.size()) {
                            Card card = player2Row0.get(action.getY());
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            if (card instanceof Minion) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("attackDamage", card.getAttackDamage());
                                cardNode.put("health", card.getHealth());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            else if (card instanceof Environment) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            node.set("output", cardNode);
                            output.add(node);
                        }
                        else {
                            //error if it doesn't exist
                            node.put("output", "No card available at that position.");
                            output.add(node);
                        }
                    }
                    else if (action.getX() == 1) {
                        //checks if the index is correct
                        if (action.getY() < player2Row1.size()) {
                            Card card = player2Row1.get(action.getY());
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            if (card instanceof Minion) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("attackDamage", card.getAttackDamage());
                                cardNode.put("health", card.getHealth());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            } else if (card instanceof Environment) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            node.set("output", cardNode);
                            output.add(node);
                        }
                        else {
                            //error if it doesn't exist
                            node.put("output", "No card available at that position.");
                            output.add(node);
                        }
                    }
                    else if (action.getX() == 2) {
                        //checks if the index is correct
                        if (action.getY() < player1Row2.size()) {
                            Card card = player1Row2.get(action.getY());
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            if (card instanceof Minion) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("attackDamage", card.getAttackDamage());
                                cardNode.put("health", card.getHealth());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            } else if (card instanceof Environment) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            node.set("output", cardNode);
                            output.add(node);
                        }
                        else {
                            //error if it doesn't exist
                            node.put("output", "No card available at that position.");
                            output.add(node);
                        }
                    }
                    else if (action.getX() == 3) {
                        //checks if the index is correct
                        if (action.getY() < player1Row3.size())  {
                            Card card = player1Row3.get(action.getY());
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            if (card instanceof Minion) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("attackDamage", card.getAttackDamage());
                                cardNode.put("health", card.getHealth());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            } else if (card instanceof Environment) {
                                cardNode.put("mana", card.getMana());
                                cardNode.put("description", card.getDescription());
                                cardNode.putPOJO("colors", card.getColors());
                                cardNode.put("name", card.getName());
                            }
                            node.set("output", cardNode);
                            output.add(node);
                        }
                        else {
                            //error if it doesn't exist
                            node.put("output", "No card available at that position.");
                            output.add(node);
                        }
                    }
                }
                //use an environmental card
                else if (Objects.equals(action.getCommand(), "useEnvironmentCard")) {
                    if (playerTurn % 2 != 0) {
                        //checks if the type of card is right
                        if (!(activePlayer1.getHand().get(action.getHandIdx()) instanceof Environment)) {
                            //error if it is not of the right type
                            node.put("command", action.getCommand());
                            node.put("handIdx", action.getHandIdx());
                            node.put("affectedRow", action.getAffectedRow());
                            node.put("error", "Chosen card is not of type environment.");
                            output.add(node);
                        }
                        else if (activePlayer1.getHand().get(action.getHandIdx()).getMana() > activePlayer1.getMana()) {
                            node.put("command", action.getCommand());
                            node.put("handIdx", action.getHandIdx());
                            node.put("affectedRow", action.getAffectedRow());
                            node.put("error", "Not enough mana to use environment card.");
                            output.add(node);
                        }
                        else if (action.getAffectedRow() == 2 || action.getAffectedRow() == 3) {
                            node.put("command", action.getCommand());
                            node.put("handIdx", action.getHandIdx());
                            node.put("affectedRow", action.getAffectedRow());
                            node.put("error", "Chosen row does not belong to the enemy.");
                            output.add(node);
                        }
                        else {
                            //check which card it is & uses its effect on the desired row
                            if (Objects.equals(activePlayer1.getHand().get(action.getHandIdx()).getName(),
                                    "Firestorm")) {
                                if (action.getAffectedRow() == 0) {
                                    int size = player2Row0.size();
                                    for (int j = 0; j < size; j++) {
                                        //if a card's health is 1, delete it, as its health would reach 0
                                        if (player2Row0.get(j).getHealth() == 1) {
                                            player2Row0.remove(player2Row0.get(j));
                                            j--;
                                            size--;
                                        }
                                        //otherwise, decrease the card's health by 1;
                                        else {
                                            player2Row0.get(j).setHealth(player2Row0.get(j).getHealth() - 1);
                                        }
                                    }
                                    int previousMana = activePlayer1.getMana();
                                    int manaNext = previousMana -
                                            activePlayer1.getHand().get(action.getHandIdx()).getMana();
                                    activePlayer1.setMana(manaNext);
                                    activePlayer1.getHand().remove(action.getHandIdx());
                                }
                                else if (action.getAffectedRow() == 1) {
                                    int size = player2Row1.size();
                                    for (int j = 0; j < size; j++) {
                                        //if a card's health is 1, delete it, as its health would reach 0
                                        if (player2Row1.get(j).getHealth() == 1) {
                                            player2Row1.remove(player2Row1.get(j));
                                            j--;
                                            size--;
                                        }
                                        //otherwise, decrease the card's health by 1;
                                        else {
                                            player2Row1.get(j).setHealth(player2Row1.get(j).getHealth() - 1);
                                        }
                                    }
                                    int previousMana = activePlayer1.getMana();
                                    int manaNext = previousMana -
                                            activePlayer1.getHand().get(action.getHandIdx()).getMana();
                                    activePlayer1.setMana(manaNext);
                                    activePlayer1.getHand().remove(action.getHandIdx());
                                }
                            }
                            else if (Objects.equals(activePlayer1.getHand().get(action.getHandIdx()).getName(),
                                    "Winterfell")) {
                                if (action.getAffectedRow() == 0) {
                                    //set the row frozen
                                    for (Card card : player2Row0) {
                                        card.setFrozen(true);
                                    }
                                    int previousMana = activePlayer1.getMana();
                                    int manaNext = previousMana -
                                            activePlayer1.getHand().get(action.getHandIdx()).getMana();
                                    activePlayer1.setMana(manaNext);
                                    activePlayer1.getHand().remove(action.getHandIdx());
                                }
                                else if (action.getAffectedRow() == 1) {
                                    //set the row frozen
                                    for (Card card : player2Row1) {
                                        card.setFrozen(true);
                                    }
                                    int previousMana = activePlayer1.getMana();
                                    int manaNext = previousMana -
                                            activePlayer1.getHand().get(action.getHandIdx()).getMana();
                                    activePlayer1.setMana(manaNext);
                                    activePlayer1.getHand().remove(action.getHandIdx());
                                }
                            }
                            else if (Objects.equals(activePlayer1.getHand().get(action.getHandIdx()).getName(),
                                    "Heart Hound")) {
                                if (action.getAffectedRow() == 0) {
                                    //checks whether the current player's mirrored row is full or not
                                    if (player1Row3.size() < 5) {
                                        int maxHealth = 0;
                                        //searches for the highest health minion
                                        for (Card card : player2Row0) {
                                            if (card.getHealth() > maxHealth) {
                                                maxHealth = card.getHealth();
                                            }
                                        }
                                        //delete it from opponent's row & add it to the current player's row
                                        for (Card card : player2Row0) {
                                            if (card.getHealth() == maxHealth) {
                                                player1Row3.add(card);
                                                player2Row0.remove(card);
                                                break;
                                            }
                                        }
                                        int previousMana = activePlayer1.getMana();
                                        int manaNext = previousMana -
                                                activePlayer1.getHand().get(action.getHandIdx()).getMana();
                                        activePlayer1.setMana(manaNext);
                                        activePlayer1.getHand().remove(action.getHandIdx());
                                    }
                                    //error if the current player's row is already full
                                    else {
                                        node.put("command", action.getCommand());
                                        node.put("handIdx", action.getHandIdx());
                                        node.put("affectedRow", action.getAffectedRow());
                                        node.put("error", "Cannot steal enemy card since the player's row is full.");
                                        output.add(node);
                                    }
                                }
                                else if (action.getAffectedRow() == 1) {
                                    //checks whether the current player's mirrored row is full or not
                                    if (player1Row2.size() < 5) {
                                        int maxHealth = 0;
                                        //searches for the highest health minion
                                        for (Card card : player2Row1) {
                                            if (card.getHealth() > maxHealth) {
                                                maxHealth = card.getHealth();
                                            }
                                        }
                                        //delete it from opponent's row & add it to the current player's row
                                        for (Card card : player2Row1) {
                                            if (card.getHealth() == maxHealth) {
                                                player1Row2.add(card);
                                                player2Row1.remove(card);
                                                break;
                                            }
                                        }
                                        int previousMana = activePlayer1.getMana();
                                        int manaNext = previousMana -
                                                activePlayer1.getHand().get(action.getHandIdx()).getMana();
                                        activePlayer1.setMana(manaNext);
                                        activePlayer1.getHand().remove(action.getHandIdx());
                                    }
                                    //error if the current player's row is already full
                                    else {
                                        node.put("command", action.getCommand());
                                        node.put("handIdx", action.getHandIdx());
                                        node.put("affectedRow", action.getAffectedRow());
                                        node.put("error", "Cannot steal enemy card since the player's row is full.");
                                        output.add(node);                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (!(activePlayer2.getHand().get(action.getHandIdx()) instanceof Environment)) {
                            node.put("command", action.getCommand());
                            node.put("handIdx", action.getHandIdx());
                            node.put("affectedRow", action.getAffectedRow());
                            node.put("error", "Chosen card is not of type environment.");
                            output.add(node);
                        }
                        else if (activePlayer2.getHand().get(action.getHandIdx()).getMana() > activePlayer2.getMana()) {
                            node.put("command", action.getCommand());
                            node.put("handIdx", action.getHandIdx());
                            node.put("affectedRow", action.getAffectedRow());
                            node.put("error", "Not enough mana to use environment card.");
                            output.add(node);                        }
                        else if (action.getAffectedRow() == 0 || action.getAffectedRow() == 1) {
                            node.put("command", action.getCommand());
                            node.put("handIdx", action.getHandIdx());
                            node.put("affectedRow", action.getAffectedRow());
                            node.put("error", "Chosen row does not belong to the enemy.");
                            output.add(node);                        }
                        else {
                            if (Objects.equals(activePlayer2.getHand().get(action.getHandIdx()).getName(),
                                    "Firestorm")) {
                                if (action.getAffectedRow() == 2) {
                                    int size = player1Row2.size();
                                    for (int j = 0; j < size; j++) {
                                        if (player1Row2.get(j).getHealth() == 1) {
                                            player1Row2.remove(player1Row2.get(j));
                                            j--;
                                            size--;
                                        }
                                        else {
                                            player1Row2.get(j).setHealth(player1Row2.get(j).getHealth() - 1);
                                        }
                                    }
                                    int previousMana = activePlayer2.getMana();
                                    int manaNext = previousMana -
                                            activePlayer2.getHand().get(action.getHandIdx()).getMana();
                                    activePlayer2.setMana(manaNext);
                                    activePlayer2.getHand().remove(action.getHandIdx());
                                }
                                else if (action.getAffectedRow() == 3) {
                                    int size = player1Row3.size();
                                    for (int j = 0; j < size; j++) {
                                        if (player1Row3.get(j).getHealth() == 1) {
                                            player1Row3.remove(player1Row3.get(j));
                                            j--;
                                            size--;
                                        }
                                        else {
                                            player1Row3.get(j).setHealth(player1Row3.get(j).getHealth() - 1);
                                        }
                                    }
                                    int previousMana = activePlayer2.getMana();
                                    int manaNext = previousMana -
                                            activePlayer2.getHand().get(action.getHandIdx()).getMana();
                                    activePlayer2.setMana(manaNext);
                                    activePlayer2.getHand().remove(action.getHandIdx());
                                }
                            }
                            else if (Objects.equals(activePlayer2.getHand().get(action.getHandIdx()).getName(),
                                    "Winterfell")) {
                                if (action.getAffectedRow() == 2) {
                                    //may need to put it into minions?
                                    for (Card card : player1Row2) {
                                        card.setFrozen(true);
                                    }
                                    int previousMana = activePlayer2.getMana();
                                    int manaNext = previousMana -
                                            activePlayer2.getHand().get(action.getHandIdx()).getMana();
                                    activePlayer2.setMana(manaNext);
                                    activePlayer2.getHand().remove(action.getHandIdx());
                                }
                                else if (action.getAffectedRow() == 3) {
                                    for (Card card : player1Row3) {
                                        card.setFrozen(true);
                                    }
                                    int previousMana = activePlayer2.getMana();
                                    int manaNext = previousMana -
                                            activePlayer2.getHand().get(action.getHandIdx()).getMana();
                                    activePlayer2.setMana(manaNext);
                                    activePlayer2.getHand().remove(action.getHandIdx());
                                }
                            }
                            else if (Objects.equals(activePlayer2.getHand().get(action.getHandIdx()).getName(),
                                    "Heart Hound")) {
                                if (action.getAffectedRow() == 2) {
                                    if (player2Row1.size() < 5) {
                                        int maxHealth = 0;
                                        for (Card card : player1Row2) {
                                            if (card.getHealth() > maxHealth) {
                                                maxHealth = card.getHealth();
                                            }
                                        }
                                        for (Card card : player1Row2) {
                                            if (card.getHealth() == maxHealth) {
                                                player2Row1.add(card);
                                                player1Row2.remove(card);
                                                break;
                                            }
                                        }
                                        int previousMana = activePlayer2.getMana();
                                        int manaNext = previousMana -
                                                activePlayer2.getHand().get(action.getHandIdx()).getMana();
                                        activePlayer2.setMana(manaNext);
                                        activePlayer2.getHand().remove(action.getHandIdx());
                                    }
                                    else {
                                        node.put("command", action.getCommand());
                                        node.put("handIdx", action.getHandIdx());
                                        node.put("affectedRow", action.getAffectedRow());
                                        node.put("error", "Cannot steal enemy card since the player's row is full.");
                                        output.add(node);                                      }
                                }
                                else if (action.getAffectedRow() == 3) {
                                    if (player2Row0.size() < 5) {
                                        int maxHealth = 0;
                                        for (Card card : player1Row3) {
                                            if (card.getHealth() > maxHealth) {
                                                maxHealth = card.getHealth();
                                            }
                                        }
                                        for (Card card : player1Row3) {
                                            if (card.getHealth() == maxHealth) {
                                                player2Row0.add(card);
                                                player1Row3.remove(card);
                                                break;
                                            }
                                        }
                                        int previousMana = activePlayer2.getMana();
                                        int manaNext = previousMana -
                                                activePlayer2.getHand().get(action.getHandIdx()).getMana();
                                        activePlayer2.setMana(manaNext);
                                        activePlayer2.getHand().remove(action.getHandIdx());
                                    }
                                    else {
                                        node.put("command", action.getCommand());
                                        node.put("handIdx", action.getHandIdx());
                                        node.put("affectedRow", action.getAffectedRow());
                                        node.put("error", "Cannot steal enemy card since the player's row is full.");
                                        output.add(node);                                      }
                                }
                            }
                        }
                    }
                }
                //gets the frozen cards on table
                else if (Objects.equals(action.getCommand(), "getFrozenCardsOnTable")) {
                    node.put("command", action.getCommand());
                    ArrayNode table = objectMapper.createArrayNode();
                    //searches every row to find the frozen cards & adds them to an arrayNode
                    for (Card card : player2Row0)
                        if (card.isFrozen()) {
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            cardNode.put("mana", card.getMana());
                            cardNode.put("attackDamage", card.getAttackDamage());
                            cardNode.put("health", card.getHealth());
                            cardNode.put("description", card.getDescription());
                            cardNode.putPOJO("colors", card.getColors());
                            cardNode.put("name", card.getName());
                            table.add(cardNode);
                        }
                    for (Card card : player2Row1)
                        if (card.isFrozen()) {
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            cardNode.put("mana", card.getMana());
                            cardNode.put("attackDamage", card.getAttackDamage());
                            cardNode.put("health", card.getHealth());
                            cardNode.put("description", card.getDescription());
                            cardNode.putPOJO("colors", card.getColors());
                            cardNode.put("name", card.getName());
                            table.add(cardNode);
                        }
                    for (Card card : player1Row2)
                        if (card.isFrozen()) {
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            cardNode.put("mana", card.getMana());
                            cardNode.put("attackDamage", card.getAttackDamage());
                            cardNode.put("health", card.getHealth());
                            cardNode.put("description", card.getDescription());
                            cardNode.putPOJO("colors", card.getColors());
                            cardNode.put("name", card.getName());
                            table.add(cardNode);
                        }
                    for (Card card : player1Row3)
                        if (card.isFrozen()) {
                            ObjectNode cardNode = objectMapper.createObjectNode();
                            cardNode.put("mana", card.getMana());
                            cardNode.put("attackDamage", card.getAttackDamage());
                            cardNode.put("health", card.getHealth());
                            cardNode.put("description", card.getDescription());
                            cardNode.putPOJO("colors", card.getColors());
                            cardNode.put("name", card.getName());
                            table.add(cardNode);
                        }
                    node.set("output", table);
                    output.add(node);
                }
                //use a card to attack
                else if (Objects.equals(action.getCommand(), "cardUsesAttack")) {
                    if (playerTurn % 2 != 0) {
                        //checks if the attacked card is on the current player's rows
                        if (action.getCardAttacked().getX() == 2 || action.getCardAttacked().getX() == 3) {
                            node.put("command", action.getCommand());
                            ObjectNode cardAttacker = objectMapper.createObjectNode();
                            cardAttacker.put("x", action.getCardAttacker().getX());
                            cardAttacker.put("y", action.getCardAttacker().getY());
                            node.set("cardAttacker", cardAttacker);
                            ObjectNode cardAttacked = objectMapper.createObjectNode();
                            cardAttacked.put("x", action.getCardAttacked().getX());
                            cardAttacked.put("y", action.getCardAttacked().getY());
                            node.set("cardAttacked", cardAttacked);
                            node.put("error", "Attacked card does not belong to the enemy.");
                            output.add(node);
                        }
                        else if (action.getCardAttacker().getX() == 2) {
                            //checks if the card is frozen
                            if (player1Row2.get(action.getCardAttacker().getY()).isFrozen()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card is frozen.");
                                output.add(node);
                            }
                            //checks if the card has already attacked
                            else if (player1Row2.get(action.getCardAttacker().getY()).isHasAttacked()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card has already attacked this turn.");
                                output.add(node);
                            }
                            else {
                                int tank = 0;
                                //checks if the opponent has any tanks
                                for (Card card : player2Row1) {
                                    if (Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(),
                                            "Warden")) {
                                        tank = 1;
                                        break;
                                    }
                                }

                                if (action.getCardAttacked().getX() == 1) {
                                    if (tank == 1) {
                                        //if there's a tank & it isn't the one attacked, display an error
                                        if (!(Objects.equals(player2Row1.get(action.getCardAttacked().getY()).getName(),
                                                "Goliath") ||
                                                Objects.equals(
                                                        player2Row1.get(action.getCardAttacked().getY()).getName(),
                                                "Warden"))) {
                                            node.put("command", action.getCommand());
                                            ObjectNode cardAttacker = objectMapper.createObjectNode();
                                            cardAttacker.put("x", action.getCardAttacker().getX());
                                            cardAttacker.put("y", action.getCardAttacker().getY());
                                            node.set("cardAttacker", cardAttacker);
                                            ObjectNode cardAttacked = objectMapper.createObjectNode();
                                            cardAttacked.put("x", action.getCardAttacked().getX());
                                            cardAttacked.put("y", action.getCardAttacked().getY());
                                            node.set("cardAttacked", cardAttacked);
                                            node.put("error", "Attacked card is not of type 'Tank'.");
                                            output.add(node);
                                        }
                                        else {
                                            //if the attacked card's health is equal or lower to the attacker's attackDamage, remove it from the opponent's row
                                            if (player2Row1.get(action.getCardAttacked().getY()).getHealth() <=
                                                    player1Row2.get(action.getCardAttacker().getY()).getAttackDamage()) {
                                                player2Row1.remove(action.getCardAttacked().getY());
                                                player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                            }
                                            //else, lower the attacked card's health by the attacker's attackDamage
                                            else {
                                                player2Row1.get(action.getCardAttacked().getY()).setHealth(
                                                        player2Row1.get(action.getCardAttacked().getY()).getHealth() -
                                                                player1Row2.get(action.getCardAttacker().getY()).
                                                                        getAttackDamage());
                                                player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                            }
                                        }
                                    }
                                    else {
                                        if (player2Row1.get(action.getCardAttacked().getY()).getHealth() <=
                                                player1Row2.get(action.getCardAttacker().getY()).getAttackDamage()) {
                                            player2Row1.remove(action.getCardAttacked().getY());
                                            player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                        else {
                                            player2Row1.get(action.getCardAttacked().getY()).setHealth(
                                                    player2Row1.get(action.getCardAttacked().getY()).getHealth() -
                                                            player1Row2.get(action.getCardAttacker().getY()).
                                                                    getAttackDamage());
                                            player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                    }
                                }
                                else if (action.getCardAttacked().getX() == 0) {
                                    if (tank == 1) {
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        ObjectNode cardAttacked = objectMapper.createObjectNode();
                                        cardAttacked.put("x", action.getCardAttacked().getX());
                                        cardAttacked.put("y", action.getCardAttacked().getY());
                                        node.set("cardAttacked", cardAttacked);
                                        node.put("error", "Attacked card is not of type 'Tank'.");
                                        output.add(node);                                    }
                                    else {
                                        if (player2Row0.get(action.getCardAttacked().getY()).getHealth() <=
                                                player1Row2.get(action.getCardAttacker().getY()).getAttackDamage()) {
                                            player2Row0.remove(action.getCardAttacked().getY());
                                            player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                        else {
                                            player2Row0.get(action.getCardAttacked().getY()).setHealth(
                                                    player2Row0.get(action.getCardAttacked().getY()).getHealth() -
                                                            player1Row2.get(action.getCardAttacker().getY()).
                                                                    getAttackDamage());
                                            player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getCardAttacker().getX() == 3) {
                            if (player1Row3.get(action.getCardAttacker().getY()).isFrozen()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card is frozen.");
                                output.add(node);
                            }
                            else if (player1Row3.get(action.getCardAttacker().getY()).isHasAttacked()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card has already attacked this turn.");
                                output.add(node);
                            }
                            else {
                                int tank = 0;
                                for (Card card : player2Row1) {
                                    if (Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(),
                                            "Warden")) {
                                        tank = 1;
                                        break;
                                    }
                                }

                                if (action.getCardAttacked().getX() == 1) {
                                    if (tank == 1) {
                                        if (!(Objects.equals(player2Row1.get(action.getCardAttacked().getY()).getName(),
                                                "Goliath") ||
                                                Objects.equals(
                                                        player2Row1.get(action.getCardAttacked().getY()).getName(),
                                                        "Warden"))) {
                                            node.put("command", action.getCommand());
                                            ObjectNode cardAttacker = objectMapper.createObjectNode();
                                            cardAttacker.put("x", action.getCardAttacker().getX());
                                            cardAttacker.put("y", action.getCardAttacker().getY());
                                            node.set("cardAttacker", cardAttacker);
                                            ObjectNode cardAttacked = objectMapper.createObjectNode();
                                            cardAttacked.put("x", action.getCardAttacked().getX());
                                            cardAttacked.put("y", action.getCardAttacked().getY());
                                            node.set("cardAttacked", cardAttacked);
                                            node.put("error", "Attacked card is not of type 'Tank'.");
                                            output.add(node);
                                        } else {
                                            if (player2Row1.get(action.getCardAttacked().getY()).getHealth() <=
                                                    player1Row3.get(action.getCardAttacker().getY()).getAttackDamage()) {
                                                player2Row1.remove(action.getCardAttacked().getY());
                                                player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                            } else {
                                                player2Row1.get(action.getCardAttacked().getY()).setHealth(
                                                        player2Row1.get(action.getCardAttacked().getY()).getHealth() -
                                                                player1Row3.get(action.getCardAttacker().getY()).
                                                                        getAttackDamage());
                                                player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                            }
                                        }
                                    } else {
                                        if (player2Row1.get(action.getCardAttacked().getY()).getHealth() <=
                                                player1Row3.get(action.getCardAttacker().getY()).getAttackDamage()) {
                                            player2Row1.remove(action.getCardAttacked().getY());
                                            player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        } else {
                                            player2Row1.get(action.getCardAttacked().getY()).setHealth(
                                                    player2Row1.get(action.getCardAttacked().getY()).getHealth() -
                                                            player1Row3.get(action.getCardAttacker().getY()).
                                                                    getAttackDamage());
                                            player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                    }
                                } else if (action.getCardAttacked().getX() == 0) {
                                    if (tank == 1) {
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        ObjectNode cardAttacked = objectMapper.createObjectNode();
                                        cardAttacked.put("x", action.getCardAttacked().getX());
                                        cardAttacked.put("y", action.getCardAttacked().getY());
                                        node.set("cardAttacked", cardAttacked);
                                        node.put("error", "Attacked card is not of type 'Tank'.");
                                        output.add(node);
                                    } else {
                                        if (player2Row0.get(action.getCardAttacked().getY()).getHealth() <=
                                                player1Row3.get(action.getCardAttacker().getY()).getAttackDamage()) {
                                            player2Row0.remove(action.getCardAttacked().getY());
                                            player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        } else {
                                            player2Row0.get(action.getCardAttacked().getY()).setHealth(
                                                    player2Row0.get(action.getCardAttacked().getY()).getHealth() -
                                                            player1Row3.get(action.getCardAttacker().getY()).
                                                                    getAttackDamage());
                                            player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (action.getCardAttacked().getX() == 0 || action.getCardAttacked().getX() == 1) {
                            node.put("command", action.getCommand());
                            ObjectNode cardAttacker = objectMapper.createObjectNode();
                            cardAttacker.put("x", action.getCardAttacker().getX());
                            cardAttacker.put("y", action.getCardAttacker().getY());
                            node.set("cardAttacker", cardAttacker);
                            ObjectNode cardAttacked = objectMapper.createObjectNode();
                            cardAttacked.put("x", action.getCardAttacked().getX());
                            cardAttacked.put("y", action.getCardAttacked().getY());
                            node.set("cardAttacked", cardAttacked);
                            node.put("error", "Attacked card does not belong to the enemy.");
                            output.add(node);
                        }
                        else if (action.getCardAttacker().getX() == 1) {
                            if (player2Row1.get(action.getCardAttacker().getY()).isFrozen()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacked().getX());
                                cardAttacker.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card is frozen.");
                                output.add(node);
                            }
                            else if (player2Row1.get(action.getCardAttacker().getY()).isHasAttacked()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacked().getX());
                                cardAttacker.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card has already attacked this turn.");
                                output.add(node);
                            }
                            else {
                                int tank = 0;
                                for (Card card : player1Row2) {
                                    if (Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(),
                                            "Warden")) {
                                        tank = 1;
                                        break;
                                    }
                                }

                                if (action.getCardAttacked().getX() == 2) {
                                    if (tank == 1) {
                                        if (!(Objects.equals(player1Row2.get(action.getCardAttacked().getY()).getName(),
                                                "Goliath") ||
                                                Objects.equals(
                                                        player1Row2.get(action.getCardAttacked().getY()).getName(),
                                                        "Warden"))) {
                                            node.put("command", action.getCommand());
                                            ObjectNode cardAttacker = objectMapper.createObjectNode();
                                            cardAttacker.put("x", action.getCardAttacker().getX());
                                            cardAttacker.put("y", action.getCardAttacker().getY());
                                            node.set("cardAttacker", cardAttacker);
                                            ObjectNode cardAttacked = objectMapper.createObjectNode();
                                            cardAttacker.put("x", action.getCardAttacked().getX());
                                            cardAttacker.put("y", action.getCardAttacked().getY());
                                            node.set("cardAttacked", cardAttacked);
                                            node.put("error", "Attacked card is not of type 'Tank’.");
                                            output.add(node);
                                        }
                                        else {
                                            if (player1Row2.get(action.getCardAttacked().getY()).getHealth() <=
                                                    player2Row1.get(action.getCardAttacker().getY()).getAttackDamage()) {
                                                player1Row2.remove(action.getCardAttacked().getY());
                                                player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                            }
                                            else {
                                                player1Row2.get(action.getCardAttacked().getY()).setHealth(
                                                        player1Row2.get(action.getCardAttacked().getY()).getHealth() -
                                                                player2Row1.get(action.getCardAttacker().getY()).
                                                                        getAttackDamage());
                                                player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                            }
                                        }
                                    }
                                    else {
                                        if (player1Row2.get(action.getCardAttacked().getY()).getHealth() <=
                                                player2Row1.get(action.getCardAttacker().getY()).getAttackDamage()) {
                                            player1Row2.remove(action.getCardAttacked().getY());
                                            player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                        else {
                                            player1Row2.get(action.getCardAttacked().getY()).setHealth(
                                                    player1Row2.get(action.getCardAttacked().getY()).getHealth() -
                                                            player2Row1.get(action.getCardAttacker().getY()).
                                                                    getAttackDamage());
                                            player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                    }
                                }
                                else if (action.getCardAttacked().getX() == 3) {
                                    if (tank == 1) {
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        ObjectNode cardAttacked = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacked().getX());
                                        cardAttacker.put("y", action.getCardAttacked().getY());
                                        node.set("cardAttacked", cardAttacked);
                                        node.put("error", "Attacked card is not of type 'Tank’.");
                                        output.add(node);                                    }
                                    else {
                                        if (player1Row3.get(action.getCardAttacked().getY()).getHealth() <=
                                                player2Row1.get(action.getCardAttacker().getY()).getAttackDamage()) {
                                            player1Row3.remove(action.getCardAttacked().getY());
                                            player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                        else {
                                            player1Row3.get(action.getCardAttacked().getY()).setHealth(
                                                    player1Row3.get(action.getCardAttacked().getY()).getHealth() -
                                                            player2Row1.get(action.getCardAttacker().getY()).
                                                                    getAttackDamage());
                                            player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getCardAttacker().getX() == 0) {
                            if (player2Row0.get(action.getCardAttacker().getY()).isFrozen()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card is frozen.");
                                output.add(node);
                            }
                            else if (player2Row0.get(action.getCardAttacker().getY()).isHasAttacked()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card has already attacked this turn.");
                                output.add(node);
                            }
                            else {
                                int tank = 0;
                                for (Card card : player1Row2) {
                                    if (Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(),
                                            "Warden")) {
                                        tank = 1;
                                        break;
                                    }
                                }

                                if (action.getCardAttacked().getX() == 2) {
                                    if (tank == 1) {
                                        if (!(Objects.equals(player1Row2.get(action.getCardAttacked().getY()).getName(),
                                                "Goliath") ||
                                                Objects.equals(
                                                        player1Row2.get(action.getCardAttacked().getY()).getName(),
                                                        "Warden"))) {
                                            node.put("command", action.getCommand());
                                            ObjectNode cardAttacker = objectMapper.createObjectNode();
                                            cardAttacker.put("x", action.getCardAttacker().getX());
                                            cardAttacker.put("y", action.getCardAttacker().getY());
                                            node.set("cardAttacker", cardAttacker);
                                            ObjectNode cardAttacked = objectMapper.createObjectNode();
                                            cardAttacked.put("x", action.getCardAttacked().getX());
                                            cardAttacked.put("y", action.getCardAttacked().getY());
                                            node.set("cardAttacked", cardAttacked);
                                            node.put("error", "Attacked card is not of type 'Tank'.");
                                            output.add(node);
                                        } else {
                                            if (player1Row2.get(action.getCardAttacked().getY()).getHealth() <=
                                                    player2Row0.get(action.getCardAttacker().getY()).getAttackDamage()) {
                                                player1Row2.remove(action.getCardAttacked().getY());
                                                player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                            } else {
                                                player1Row2.get(action.getCardAttacked().getY()).setHealth(
                                                        player1Row2.get(action.getCardAttacked().getY()).getHealth() -
                                                                player2Row0.get(action.getCardAttacker().getY()).
                                                                        getAttackDamage());
                                                player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                            }
                                        }
                                    } else {
                                        if (player1Row2.get(action.getCardAttacked().getY()).getHealth() <=
                                                player2Row0.get(action.getCardAttacker().getY()).getAttackDamage()) {
                                            player1Row2.remove(action.getCardAttacked().getY());
                                            player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        } else {
                                            player1Row2.get(action.getCardAttacked().getY()).setHealth(
                                                    player1Row2.get(action.getCardAttacked().getY()).getHealth() -
                                                            player2Row0.get(action.getCardAttacker().getY()).
                                                                    getAttackDamage());
                                            player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                    }
                                } else if (action.getCardAttacked().getX() == 3) {
                                    if (tank == 1) {
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        ObjectNode cardAttacked = objectMapper.createObjectNode();
                                        cardAttacked.put("x", action.getCardAttacked().getX());
                                        cardAttacked.put("y", action.getCardAttacked().getY());
                                        node.set("cardAttacked", cardAttacked);
                                        node.put("error", "Attacked card is not of type 'Tank'.");
                                        output.add(node);
                                    } else {
                                        if (player1Row3.get(action.getCardAttacked().getY()).getHealth() <=
                                                player2Row0.get(action.getCardAttacker().getY()).getAttackDamage()) {
                                            player1Row3.remove(action.getCardAttacked().getY());
                                            player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        } else {
                                            player1Row3.get(action.getCardAttacked().getY()).setHealth(
                                                    player1Row3.get(action.getCardAttacked().getY()).getHealth() -
                                                            player2Row0.get(action.getCardAttacker().getY()).
                                                                    getAttackDamage());
                                            player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //use a card's ability
                else if (Objects.equals(action.getCommand(), "cardUsesAbility")) {
                    if (playerTurn % 2 != 0) {
                        if (action.getCardAttacker().getX() == 2) {
                            if (player1Row2.get(action.getCardAttacker().getY()).isFrozen()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card is frozen.");
                                output.add(node);
                            }
                            else if (player1Row2.get(action.getCardAttacker().getY()).isHasAttacked()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card has already attacked this turn.");
                                output.add(node);
                            }
                            else {
                                if (player1Row2.get(action.getCardAttacker().getY()) instanceof Disciple) {
                                    if (action.getCardAttacked().getX() == 0 || action.getCardAttacked().getX() == 1) {
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        ObjectNode cardAttacked = objectMapper.createObjectNode();
                                        cardAttacked.put("x", action.getCardAttacked().getX());
                                        cardAttacked.put("y", action.getCardAttacked().getY());
                                        node.set("cardAttacked", cardAttacked);
                                        node.put("error", "Attacked card does not belong to the current player.");
                                        output.add(node);
                                    }
                                    else {
                                        Disciple disciple = (Disciple) player1Row2.get(action.getCardAttacker().getY());
                                        if (action.getCardAttacked().getX() == 2) {
                                            disciple.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                            player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                        else {
                                            disciple.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                            player1Row3.get(action.getCardAttacked().getY()).setHasAttacked(true);
                                        }
                                    }
                                }
                                else {
                                    if (action.getCardAttacked().getX() == 2 || action.getCardAttacked().getX() == 3) {
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        ObjectNode cardAttacked = objectMapper.createObjectNode();
                                        cardAttacked.put("x", action.getCardAttacked().getX());
                                        cardAttacked.put("y", action.getCardAttacked().getY());
                                        node.set("cardAttacked", cardAttacked);
                                        node.put("error", "Attacked card does not belong to the enemy.");
                                        output.add(node);
                                    }
                                    else {
                                        int tank = 0;
                                        for (Card card : player2Row1) {
                                            if (Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(),
                                                    "Warden")) {
                                                tank = 1;
                                                break;
                                            }
                                        }

                                        if (action.getCardAttacked().getX() == 1) {
                                            if (tank == 1) {
                                                if (!(Objects.equals(player2Row1.get(action.getCardAttacked().getY()).getName(),
                                                        "Goliath") ||
                                                        Objects.equals(
                                                                player2Row1.get(action.getCardAttacked().getY()).getName(),
                                                                "Warden"))) {
                                                    node.put("command", action.getCommand());
                                                    ObjectNode cardAttacker = objectMapper.createObjectNode();
                                                    cardAttacker.put("x", action.getCardAttacker().getX());
                                                    cardAttacker.put("y", action.getCardAttacker().getY());
                                                    node.set("cardAttacker", cardAttacker);
                                                    ObjectNode cardAttacked = objectMapper.createObjectNode();
                                                    cardAttacked.put("x", action.getCardAttacked().getX());
                                                    cardAttacked.put("y", action.getCardAttacked().getY());
                                                    node.set("cardAttacked", cardAttacked);
                                                    node.put("error", "Attacked card is not of type 'Tank'.");
                                                    output.add(node);
                                                } else {
                                                    if (player1Row2.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                        TheRipper theRipper = (TheRipper) player1Row2.get(action.getCardAttacker().getY());
                                                        theRipper.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                        player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                    }
                                                    else if (player1Row2.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                        Miraj miraj = (Miraj) player1Row2.get(action.getCardAttacker().getY());
                                                        miraj.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                        player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                    }
                                                    else if (player1Row2.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                        TheCursedOne theCursedOne = (TheCursedOne) player1Row2.get(action.getCardAttacker().getY());
                                                        theCursedOne.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                        if (player2Row1.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                            player2Row1.remove(action.getCardAttacked().getY());
                                                        player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                    }
                                                }
                                            } else {
                                                if (player1Row2.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player1Row2.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                    player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player1Row2.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player1Row2.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                    player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player1Row2.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player1Row2.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                    if (player2Row1.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player2Row1.remove(action.getCardAttacked().getY());
                                                    player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                            }
                                        } else if (action.getCardAttacked().getX() == 0) {
                                            if (tank == 1) {
                                                node.put("command", action.getCommand());
                                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                                cardAttacker.put("x", action.getCardAttacker().getX());
                                                cardAttacker.put("y", action.getCardAttacker().getY());
                                                node.set("cardAttacker", cardAttacker);
                                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                                cardAttacked.put("x", action.getCardAttacked().getX());
                                                cardAttacked.put("y", action.getCardAttacked().getY());
                                                node.set("cardAttacked", cardAttacked);
                                                node.put("error", "Attacked card is not of type 'Tank'.");
                                                output.add(node);
                                            } else {
                                                if (player1Row2.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player1Row2.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                                    player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player1Row2.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player1Row2.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                                    player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player1Row2.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player1Row2.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                                    if (player2Row0.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player2Row0.remove(action.getCardAttacked().getY());
                                                    player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getCardAttacker().getX() == 3) {
                            if (player1Row3.get(action.getCardAttacker().getY()).isFrozen()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card is frozen.");
                                output.add(node);
                            }
                            else if (player1Row3.get(action.getCardAttacker().getY()).isHasAttacked()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card has already attacked this turn.");
                                output.add(node);
                            }
                            else {
                                if (player1Row2.get(action.getCardAttacker().getY()) instanceof Disciple) {
                                    if (action.getCardAttacked().getX() == 0 || action.getCardAttacked().getX() == 1) {
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        ObjectNode cardAttacked = objectMapper.createObjectNode();
                                        cardAttacked.put("x", action.getCardAttacked().getX());
                                        cardAttacked.put("y", action.getCardAttacked().getY());
                                        node.set("cardAttacked", cardAttacked);
                                        node.put("error", "Attacked card does not belong to the current player.");
                                        output.add(node);
                                    }
                                    else {
                                        Disciple disciple = (Disciple) player1Row2.get(action.getCardAttacker().getY());
                                        if (action.getCardAttacked().getX() == 2) {
                                            disciple.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                            player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                        else {
                                            disciple.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                            player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                    }
                                }
                                else {
                                    if (action.getCardAttacked().getX() == 2 || action.getCardAttacked().getX() == 3) {
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        ObjectNode cardAttacked = objectMapper.createObjectNode();
                                        cardAttacked.put("x", action.getCardAttacked().getX());
                                        cardAttacked.put("y", action.getCardAttacked().getY());
                                        node.set("cardAttacked", cardAttacked);
                                        node.put("error", "Attacked card does not belong to the enemy.");
                                        output.add(node);
                                    }
                                    else {
                                        int tank = 0;
                                        for (Card card : player2Row1) {
                                            if (Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(),
                                                    "Warden")) {
                                                tank = 1;
                                                break;
                                            }
                                        }

                                        if (action.getCardAttacked().getX() == 1) {
                                            if (tank == 1) {
                                                if (!(Objects.equals(player2Row1.get(action.getCardAttacked().getY()).getName(),
                                                        "Goliath") ||
                                                        Objects.equals(
                                                                player2Row1.get(action.getCardAttacked().getY()).getName(),
                                                                "Warden"))) {
                                                    node.put("command", action.getCommand());
                                                    ObjectNode cardAttacker = objectMapper.createObjectNode();
                                                    cardAttacker.put("x", action.getCardAttacker().getX());
                                                    cardAttacker.put("y", action.getCardAttacker().getY());
                                                    node.set("cardAttacker", cardAttacker);
                                                    ObjectNode cardAttacked = objectMapper.createObjectNode();
                                                    cardAttacked.put("x", action.getCardAttacked().getX());
                                                    cardAttacked.put("y", action.getCardAttacked().getY());
                                                    node.set("cardAttacked", cardAttacked);
                                                    node.put("error", "Attacked card is not of type 'Tank'.");
                                                    output.add(node);
                                                } else {
                                                    if (player1Row3.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                        TheRipper theRipper = (TheRipper) player1Row3.get(action.getCardAttacker().getY());
                                                        theRipper.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                        player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                    }
                                                    else if (player1Row3.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                        Miraj miraj = (Miraj) player1Row3.get(action.getCardAttacker().getY());
                                                        miraj.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                        player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                    }
                                                    else if (player1Row3.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                        TheCursedOne theCursedOne = (TheCursedOne) player1Row3.get(action.getCardAttacker().getY());
                                                        theCursedOne.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                        if (player2Row1.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                            player2Row1.remove(action.getCardAttacked().getY());
                                                        player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                    }
                                                }
                                            } else {
                                                if (player1Row3.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player1Row3.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                    player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player1Row3.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player1Row3.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                    player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player1Row3.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player1Row3.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                    if (player2Row1.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player2Row1.remove(action.getCardAttacked().getY());
                                                    player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                            }
                                        } else if (action.getCardAttacked().getX() == 0) {
                                            if (tank == 1) {
                                                node.put("command", action.getCommand());
                                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                                cardAttacker.put("x", action.getCardAttacker().getX());
                                                cardAttacker.put("y", action.getCardAttacker().getY());
                                                node.set("cardAttacker", cardAttacker);
                                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                                cardAttacked.put("x", action.getCardAttacked().getX());
                                                cardAttacked.put("y", action.getCardAttacked().getY());
                                                node.set("cardAttacked", cardAttacked);
                                                node.put("error", "Attacked card is not of type 'Tank'.");
                                                output.add(node);
                                            } else {
                                                if (player1Row3.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player1Row3.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                                    player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player1Row3.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player1Row3.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                                    player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player1Row3.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player1Row3.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                                    if (player2Row0.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player2Row0.remove(action.getCardAttacked().getY());
                                                    player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (action.getCardAttacker().getX() == 1) {
                            if (player2Row1.get(action.getCardAttacker().getY()).isFrozen()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card is frozen.");
                                output.add(node);
                            }
                            else if (player2Row1.get(action.getCardAttacker().getY()).isHasAttacked()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card has already attacked this turn.");
                                output.add(node);
                            }
                            else {
                                if (player2Row1.get(action.getCardAttacker().getY()) instanceof Disciple) {
                                    if (action.getCardAttacked().getX() == 2 || action.getCardAttacked().getX() == 3) {
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        ObjectNode cardAttacked = objectMapper.createObjectNode();
                                        cardAttacked.put("x", action.getCardAttacked().getX());
                                        cardAttacked.put("y", action.getCardAttacked().getY());
                                        node.set("cardAttacked", cardAttacked);
                                        node.put("error", "Attacked card does not belong to the current player.");
                                        output.add(node);
                                    }
                                    else {
                                        Disciple disciple = (Disciple) player2Row1.get(action.getCardAttacker().getY());
                                        if (action.getCardAttacked().getX() == 1) {
                                            disciple.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                            player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                        else {
                                            disciple.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                            player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                    }
                                }
                                else {
                                    if (action.getCardAttacked().getX() == 0 || action.getCardAttacked().getX() == 1) {
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        ObjectNode cardAttacked = objectMapper.createObjectNode();
                                        cardAttacked.put("x", action.getCardAttacked().getX());
                                        cardAttacked.put("y", action.getCardAttacked().getY());
                                        node.set("cardAttacked", cardAttacked);
                                        node.put("error", "Attacked card does not belong to the enemy.");
                                        output.add(node);                                    }
                                    else {
                                        int tank = 0;
                                        for (Card card : player1Row2) {
                                            if (Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(),
                                                    "Warden")) {
                                                tank = 1;
                                                break;
                                            }
                                        }

                                        if (action.getCardAttacked().getX() == 2) {
                                            if (tank == 1) {
                                                if (!(Objects.equals(player1Row2.get(action.getCardAttacked().getY()).getName(),
                                                        "Goliath") ||
                                                        Objects.equals(
                                                                player1Row2.get(action.getCardAttacked().getY()).getName(),
                                                                "Warden"))) {
                                                    node.put("command", action.getCommand());
                                                    ObjectNode cardAttacker = objectMapper.createObjectNode();
                                                    cardAttacker.put("x", action.getCardAttacker().getX());
                                                    cardAttacker.put("y", action.getCardAttacker().getY());
                                                    node.set("cardAttacker", cardAttacker);
                                                    ObjectNode cardAttacked = objectMapper.createObjectNode();
                                                    cardAttacked.put("x", action.getCardAttacked().getX());
                                                    cardAttacked.put("y", action.getCardAttacked().getY());
                                                    node.set("cardAttacked", cardAttacked);
                                                    node.put("error", "Attacked card is not of type 'Tank'.");
                                                    output.add(node);
                                                } else {
                                                    if (player2Row1.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                        TheRipper theRipper = (TheRipper) player2Row1.get(action.getCardAttacker().getY());
                                                        theRipper.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                        player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                    }
                                                    else if (player2Row1.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                        Miraj miraj = (Miraj) player2Row1.get(action.getCardAttacker().getY());
                                                        miraj.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                        player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                    }
                                                    else if (player2Row1.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                        TheCursedOne theCursedOne = (TheCursedOne) player2Row1.get(action.getCardAttacker().getY());
                                                        theCursedOne.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                        if (player1Row2.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                            player1Row2.remove(action.getCardAttacked().getY());
                                                        player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                    }
                                                }
                                            } else {
                                                if (player2Row1.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player2Row1.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                    player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player2Row1.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player2Row1.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                    player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player2Row1.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player2Row1.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                    if (player1Row2.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player1Row2.remove(action.getCardAttacked().getY());
                                                    player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);

                                                }
                                            }
                                        } else if (action.getCardAttacked().getX() == 3) {
                                            if (tank == 1) {
                                                node.put("command", action.getCommand());
                                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                                cardAttacker.put("x", action.getCardAttacker().getX());
                                                cardAttacker.put("y", action.getCardAttacker().getY());
                                                node.set("cardAttacker", cardAttacker);
                                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                                cardAttacked.put("x", action.getCardAttacked().getX());
                                                cardAttacked.put("y", action.getCardAttacked().getY());
                                                node.set("cardAttacked", cardAttacked);
                                                node.put("error", "Attacked card is not of type 'Tank'.");
                                                output.add(node);                                            } else {
                                                if (player2Row1.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player2Row1.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                                    player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player2Row1.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player2Row1.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                                    player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player2Row1.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player2Row1.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                                    if (player1Row3.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player1Row3.remove(action.getCardAttacked().getY());
                                                    player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getCardAttacker().getX() == 0) {
                            if (player2Row0.get(action.getCardAttacker().getY()).isFrozen()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card is frozen.");
                                output.add(node);
                            }
                            else if (player2Row0.get(action.getCardAttacker().getY()).isHasAttacked()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                cardAttacked.put("x", action.getCardAttacked().getX());
                                cardAttacked.put("y", action.getCardAttacked().getY());
                                node.set("cardAttacked", cardAttacked);
                                node.put("error", "Attacker card has already attacked this turn.");
                                output.add(node);
                            }
                            else {
                                if (player2Row0.get(action.getCardAttacker().getY()) instanceof Disciple) {
                                    if (action.getCardAttacked().getX() == 2 || action.getCardAttacked().getX() == 3) {
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        ObjectNode cardAttacked = objectMapper.createObjectNode();
                                        cardAttacked.put("x", action.getCardAttacked().getX());
                                        cardAttacked.put("y", action.getCardAttacked().getY());
                                        node.set("cardAttacked", cardAttacked);
                                        node.put("error", "Attacked card does not belong to the current player.");
                                        output.add(node);
                                    }
                                    else {
                                        Disciple disciple = (Disciple) player2Row0.get(action.getCardAttacker().getY());
                                        if (action.getCardAttacked().getX() == 1) {
                                            disciple.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                            player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                        else {
                                            disciple.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                            player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                        }
                                    }
                                }
                                else {
                                    if (action.getCardAttacked().getX() == 0 || action.getCardAttacked().getX() == 1) {
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        ObjectNode cardAttacked = objectMapper.createObjectNode();
                                        cardAttacked.put("x", action.getCardAttacked().getX());
                                        cardAttacked.put("y", action.getCardAttacked().getY());
                                        node.set("cardAttacked", cardAttacked);
                                        node.put("error", "Attacked card does not belong to the enemy.");
                                        output.add(node);
                                    }
                                    else {
                                        int tank = 0;
                                        for (Card card : player1Row2) {
                                            if (Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(),
                                                    "Warden")) {
                                                tank = 1;
                                                break;
                                            }
                                        }

                                        if (action.getCardAttacked().getX() == 2) {
                                            if (tank == 1) {
                                                if (!(Objects.equals(player1Row2.get(action.getCardAttacked().getY()).getName(),
                                                        "Goliath") ||
                                                        Objects.equals(
                                                                player1Row2.get(action.getCardAttacked().getY()).getName(),
                                                                "Warden"))) {
                                                    node.put("command", action.getCommand());
                                                    ObjectNode cardAttacker = objectMapper.createObjectNode();
                                                    cardAttacker.put("x", action.getCardAttacker().getX());
                                                    cardAttacker.put("y", action.getCardAttacker().getY());
                                                    node.set("cardAttacker", cardAttacker);
                                                    ObjectNode cardAttacked = objectMapper.createObjectNode();
                                                    cardAttacked.put("x", action.getCardAttacked().getX());
                                                    cardAttacked.put("y", action.getCardAttacked().getY());
                                                    node.set("cardAttacked", cardAttacked);
                                                    node.put("error", "Attacked card is not of type 'Tank'.");
                                                    output.add(node);
                                                } else {
                                                    if (player2Row0.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                        TheRipper theRipper = (TheRipper) player2Row0.get(action.getCardAttacker().getY());
                                                        theRipper.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                        player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                    }
                                                    else if (player2Row0.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                        Miraj miraj = (Miraj) player2Row0.get(action.getCardAttacker().getY());
                                                        miraj.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                        player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                    }
                                                    else if (player2Row0.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                        TheCursedOne theCursedOne = (TheCursedOne) player2Row0.get(action.getCardAttacker().getY());
                                                        theCursedOne.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                        if (player1Row2.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                            player1Row2.remove(action.getCardAttacked().getY());
                                                        player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                    }
                                                }
                                            } else {
                                                if (player2Row0.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player2Row0.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                    player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player2Row0.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player2Row0.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                    player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player2Row0.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player2Row0.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                    if (player1Row2.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player1Row2.remove(action.getCardAttacked().getY());
                                                    player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                            }
                                        } else if (action.getCardAttacked().getX() == 3) {
                                            if (tank == 1) {
                                                node.put("command", action.getCommand());
                                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                                cardAttacker.put("x", action.getCardAttacker().getX());
                                                cardAttacker.put("y", action.getCardAttacker().getY());
                                                node.set("cardAttacker", cardAttacker);
                                                ObjectNode cardAttacked = objectMapper.createObjectNode();
                                                cardAttacked.put("x", action.getCardAttacked().getX());
                                                cardAttacked.put("y", action.getCardAttacked().getY());
                                                node.set("cardAttacked", cardAttacked);
                                                node.put("error", "Attacked card is not of type 'Tank'.");
                                                output.add(node);
                                            } else {
                                                if (player2Row0.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player2Row0.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                                    player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player2Row0.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player2Row0.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                                    player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                                else if (player2Row0.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player2Row0.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                                    if (player1Row3.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player1Row3.remove(action.getCardAttacked().getY());
                                                    player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else if (Objects.equals(action.getCommand(), "useAttackHero")) {
                    if (playerTurn % 2 != 0) {
                        if (action.getCardAttacker().getX() == 2) {
                            if (player1Row2.get(action.getCardAttacker().getY()).isFrozen()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                node.put("error", "Attacker card is frozen.");
                                output.add(node);
                            }
                            else if (player1Row2.get(action.getCardAttacker().getY()).isHasAttacked()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                node.put("error", "Attacker card has already attacked this turn.");
                                output.add(node);
                            }
                            else {
                                int tank = 0;
                                for (Card card : player2Row1) {
                                    if (Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(),
                                            "Warden")) {
                                        tank = 1;
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        node.put("error", "Attacked card is not of type 'Tank'.");
                                        output.add(node);
                                        break;
                                    }
                                }
                                if (tank == 0) {
                                    activePlayer2.getHero().setHealth(activePlayer2.getHero().getHealth() -
                                            player1Row2.get(action.getCardAttacker().getY()).getAttackDamage());
                                    if (activePlayer2.getHero().getHealth() <= 0) {
                                        deadHero2 = true;
                                    }
                                    player1Row2.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                }
                            }
                        }
                        else if (action.getCardAttacker().getX() == 3) {
                            if (player1Row3.get(action.getCardAttacker().getY()).isFrozen()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                node.put("error", "Attacker card is frozen.");
                                output.add(node);
                            }
                            else if (player1Row3.get(action.getCardAttacker().getY()).isHasAttacked()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                node.put("error", "Attacker card has already attacked this turn.");
                                output.add(node);                            }
                            else {
                                int tank = 0;
                                for (Card card : player2Row1) {
                                    if (Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(),
                                            "Warden")) {
                                        tank = 1;
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        node.put("error", "Attacked card is not of type 'Tank'.");
                                        output.add(node);
                                        break;
                                    }
                                }
                                if (tank == 0) {
                                    activePlayer2.getHero().setHealth(activePlayer2.getHero().getHealth() -
                                            player1Row3.get(action.getCardAttacker().getY()).getAttackDamage());
                                    if (activePlayer2.getHero().getHealth() <= 0) {
                                        deadHero2 = true;
                                    }
                                    player1Row3.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                }
                            }
                        }
                    }
                    else {
                        if (action.getCardAttacker().getX() == 1) {
                            if (player2Row1.get(action.getCardAttacker().getY()).isFrozen()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                node.put("error", "Attacker card is frozen.");
                                output.add(node);
                            }
                            else if (player2Row1.get(action.getCardAttacker().getY()).isHasAttacked()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                node.put("error", "Attacker card has already attacked this turn.");
                                output.add(node);
                            }
                            else {
                                int tank = 0;
                                for (Card card : player1Row2) {
                                    if (Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(),
                                            "Warden")) {
                                        tank = 1;
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        node.put("error", "Attacked card is not of type 'Tank'.");
                                        output.add(node);
                                        break;
                                    }
                                }
                                if (tank == 0) {
                                    activePlayer1.getHero().setHealth(activePlayer1.getHero().getHealth() -
                                            player2Row1.get(action.getCardAttacker().getY()).getAttackDamage());
                                    if (activePlayer1.getHero().getHealth() <= 0) {
                                        deadHero1 = true;
                                    }
                                    player2Row1.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                }
                            }
                        }
                        else if (action.getCardAttacker().getX() == 0) {
                            if (player2Row0.get(action.getCardAttacker().getY()).isFrozen()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                node.put("error", "Attacker card is frozen.");
                                output.add(node);
                            }
                            else if (player2Row0.get(action.getCardAttacker().getY()).isHasAttacked()) {
                                node.put("command", action.getCommand());
                                ObjectNode cardAttacker = objectMapper.createObjectNode();
                                cardAttacker.put("x", action.getCardAttacker().getX());
                                cardAttacker.put("y", action.getCardAttacker().getY());
                                node.set("cardAttacker", cardAttacker);
                                node.put("error", "Attacker card has already attacked this turn.");
                                output.add(node);                            }
                            else {
                                int tank = 0;
                                for (Card card : player1Row2) {
                                    if (Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(),
                                            "Warden")) {
                                        tank = 1;
                                        node.put("command", action.getCommand());
                                        ObjectNode cardAttacker = objectMapper.createObjectNode();
                                        cardAttacker.put("x", action.getCardAttacker().getX());
                                        cardAttacker.put("y", action.getCardAttacker().getY());
                                        node.set("cardAttacker", cardAttacker);
                                        node.put("error", "Attacked card is not of type 'Tank'.");
                                        output.add(node);
                                        break;
                                    }
                                }
                                if (tank == 0) {
                                    activePlayer1.getHero().setHealth(activePlayer1.getHero().getHealth() -
                                            player2Row0.get(action.getCardAttacker().getY()).getAttackDamage());
                                    if (activePlayer1.getHero().getHealth() <= 0) {
                                        deadHero1 = true;
                                    }
                                    player2Row0.get(action.getCardAttacker().getY()).setHasAttacked(true);
                                }
                            }
                        }
                    }
                }
                else if (Objects.equals(action.getCommand(), "useHeroAbility")) {
                    if (playerTurn % 2 != 0) {
                        if (activePlayer1.getHero().getMana() > activePlayer1.getMana()) {
                            node.put("command", action.getCommand());
                            node.put("affectedRow", action.getAffectedRow());
                            node.put("error", "Not enough mana to use hero's ability.");
                            output.add(node);
                        }
                        else if (activePlayer1.getHero().isHasAttacked()) {
                            node.put("command", action.getCommand());
                            node.put("affectedRow", action.getAffectedRow());
                            node.put("error", "Hero has already attacked this turn.");
                            output.add(node);
                        }
                        else {
                            if (Objects.equals(activePlayer1.getHero().getName(), "Lord Royce") ||
                                    Objects.equals(activePlayer1.getHero().getName(), "Empress Thorina")) {
                                if (action.getAffectedRow() == 2 || action.getAffectedRow() == 3) {
                                    node.put("command", action.getCommand());
                                    node.put("affectedRow", action.getAffectedRow());
                                    node.put("error", "Selected row does not belong to the enemy.");
                                    output.add(node);
                                }
                                else {
                                    if (Objects.equals(activePlayer1.getHero().getName(), "Lord Royce")) {
                                        String targetCard = "";
                                        int maxAttack = -1;
                                        if (action.getAffectedRow() == 0) {
                                            for (Card card : player2Row0) {
                                                if (card.getAttackDamage() > maxAttack) {
                                                    maxAttack = card.getAttackDamage();
                                                    targetCard = card.getName();
                                                }
                                            }
                                            for (Card card : player2Row0) {
                                                if (Objects.equals(card.getName(), targetCard) &&
                                                        card.getAttackDamage() == maxAttack) {
                                                    card.setFrozen(true);
                                                    activePlayer1.getHero().setHasAttacked(true);
                                                    activePlayer1.setMana(activePlayer1.getMana() -
                                                            activePlayer1.getHero().getMana());
                                                    break;
                                                }
                                            }
                                        }
                                        if (action.getAffectedRow() == 1) {
                                            for (Card card : player2Row1) {
                                                if (card.getAttackDamage() > maxAttack) {
                                                    maxAttack = card.getAttackDamage();
                                                    targetCard = card.getName();
                                                }
                                            }
                                            for (Card card : player2Row1) {
                                                if (Objects.equals(card.getName(), targetCard) &&
                                                        card.getAttackDamage() == maxAttack) {
                                                    card.setFrozen(true);
                                                    activePlayer1.getHero().setHasAttacked(true);
                                                    activePlayer1.setMana(activePlayer1.getMana() -
                                                            activePlayer1.getHero().getMana());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (Objects.equals(activePlayer1.getHero().getName(), "Empress Thorina")) {
                                        String targetCard = new String();
                                        int maxHealth = 0;
                                        if (action.getAffectedRow() == 0) {
                                            for (Card card : player2Row0) {
                                                if (card.getHealth() > maxHealth) {
                                                    maxHealth = card.getHealth();
                                                    targetCard = card.getName();
                                                }
                                            }
                                            for (Card card : player2Row0) {
                                                if (Objects.equals(card.getName(), targetCard) &&
                                                        card.getHealth() == maxHealth) {
                                                    player2Row0.remove(card);
                                                    activePlayer1.getHero().setHasAttacked(true);
                                                    activePlayer1.setMana(activePlayer1.getMana() -
                                                            activePlayer1.getHero().getMana());
                                                    break;
                                                }
                                            }
                                        }
                                        if (action.getAffectedRow() == 1) {
                                            for (Card card : player2Row1) {
                                                if (card.getHealth() > maxHealth) {
                                                    maxHealth = card.getHealth();
                                                    targetCard = card.getName();
                                                }
                                            }
                                            for (Card card : player2Row1) {
                                                if (Objects.equals(card.getName(), targetCard) &&
                                                        card.getHealth() == maxHealth) {
                                                    player2Row1.remove(card);
                                                    activePlayer1.getHero().setHasAttacked(true);
                                                    activePlayer1.setMana(activePlayer1.getMana() -
                                                            activePlayer1.getHero().getMana());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (Objects.equals(activePlayer1.getHero().getName(), "King Mudface") ||
                                    Objects.equals(activePlayer1.getHero().getName(), "General Kocioraw")) {
                                if (action.getAffectedRow() == 0 || action.getAffectedRow() == 1) {
                                    node.put("command", action.getCommand());
                                    node.put("affectedRow", action.getAffectedRow());
                                    node.put("error", "Selected row does not belong to the current player.");
                                    output.add(node);
                                }
                                else {
                                    if (Objects.equals(activePlayer1.getHero().getName(), "King Mudface")) {
                                        if (action.getAffectedRow() == 2) {
                                            for (Card card : player1Row2)
                                               card.setHealth(card.getHealth() + 1);
                                            activePlayer1.getHero().setHasAttacked(true);
                                            activePlayer1.setMana(activePlayer1.getMana() -
                                                    activePlayer1.getHero().getMana());
                                        }
                                        if (action.getAffectedRow() == 3) {
                                            for (Card card : player1Row3)
                                                card.setHealth(card.getHealth() + 1);
                                            activePlayer1.getHero().setHasAttacked(true);
                                            activePlayer1.setMana(activePlayer1.getMana() -
                                                    activePlayer1.getHero().getMana());
                                        }
                                    }
                                    if (Objects.equals(activePlayer1.getHero().getName(), "General Kocioraw")) {
                                        if (action.getAffectedRow() == 2) {
                                            for (Card card : player1Row2)
                                                card.setAttackDamage(card.getAttackDamage() + 1);
                                            activePlayer1.getHero().setHasAttacked(true);
                                            activePlayer1.setMana(activePlayer1.getMana() -
                                                    activePlayer1.getHero().getMana());
                                        }
                                        if (action.getAffectedRow() == 3) {
                                            for (Card card : player1Row3)
                                                card.setAttackDamage(card.getAttackDamage() + 1);
                                            activePlayer1.getHero().setHasAttacked(true);
                                            activePlayer1.setMana(activePlayer1.getMana() -
                                                    activePlayer1.getHero().getMana());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (activePlayer2.getHero().getMana() > activePlayer2.getMana()) {
                            node.put("command", action.getCommand());
                            node.put("affectedRow", action.getAffectedRow());
                            node.put("error", "Not enough mana to use hero's ability.");
                            output.add(node);
                        }
                        else if (activePlayer2.getHero().isHasAttacked()) {
                            node.put("command", action.getCommand());
                            node.put("affectedRow", action.getAffectedRow());
                            node.put("error", "Hero has already attacked this turn.");
                            output.add(node);
                        }
                        else {
                            if (Objects.equals(activePlayer2.getHero().getName(), "Lord Royce") ||
                                    Objects.equals(activePlayer2.getHero().getName(), "Empress Thorina")) {
                                if (action.getAffectedRow() == 0 || action.getAffectedRow() == 1) {
                                    node.put("command", action.getCommand());
                                    node.put("affectedRow", action.getAffectedRow());
                                    node.put("error", "Selected row does not belong to the enemy.");
                                    output.add(node);
                                }
                                else {
                                    if (Objects.equals(activePlayer2.getHero().getName(), "Lord Royce")) {
                                        String targetCard = "";
                                        int maxAttack = -1;
                                        if (action.getAffectedRow() == 2) {
                                            for (Card card : player1Row2) {
                                                if (card.getAttackDamage() > maxAttack) {
                                                    maxAttack = card.getAttackDamage();
                                                    targetCard = card.getName();
                                                }
                                            }
                                            for (Card card : player1Row2) {
                                                if (Objects.equals(card.getName(), targetCard) &&
                                                        card.getAttackDamage() == maxAttack) {
                                                    card.setFrozen(true);
                                                    activePlayer2.getHero().setHasAttacked(true);
                                                    activePlayer2.setMana(activePlayer2.getMana() -
                                                            activePlayer2.getHero().getMana());
                                                    break;
                                                }
                                            }
                                        }
                                        if (action.getAffectedRow() == 3) {
                                            for (Card card : player1Row3) {
                                                if (card.getAttackDamage() > maxAttack) {
                                                    maxAttack = card.getAttackDamage();
                                                    targetCard = card.getName();
                                                }
                                            }
                                            for (Card card : player1Row3) {
                                                if (Objects.equals(card.getName(), targetCard) &&
                                                        card.getAttackDamage() == maxAttack) {
                                                    card.setFrozen(true);
                                                    activePlayer2.getHero().setHasAttacked(true);
                                                    activePlayer2.setMana(activePlayer2.getMana() -
                                                            activePlayer2.getHero().getMana());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (Objects.equals(activePlayer2.getHero().getName(), "Empress Thorina")) {
                                        String targetCard = new String();
                                        int maxHealth = 0;
                                        if (action.getAffectedRow() == 2) {
                                            for (Card card : player1Row2) {
                                                if (card.getHealth() > maxHealth) {
                                                    maxHealth = card.getHealth();
                                                    targetCard = card.getName();
                                                }
                                            }
                                            for (Card card : player1Row2) {
                                                if (Objects.equals(card.getName(), targetCard) &&
                                                        card.getHealth() == maxHealth) {
                                                    player1Row2.remove(card);
                                                    activePlayer2.getHero().setHasAttacked(true);
                                                    activePlayer2.setMana(activePlayer2.getMana() -
                                                            activePlayer2.getHero().getMana());
                                                    break;
                                                }
                                            }
                                        }
                                        if (action.getAffectedRow() == 3) {
                                            for (Card card : player1Row3) {
                                                if (card.getHealth() > maxHealth) {
                                                    maxHealth = card.getHealth();
                                                    targetCard = card.getName();
                                                }
                                            }
                                            for (Card card : player1Row3) {
                                                if (Objects.equals(card.getName(), targetCard) &&
                                                        card.getHealth() == maxHealth) {
                                                    player1Row3.remove(card);
                                                    activePlayer2.getHero().setHasAttacked(true);
                                                    activePlayer2.setMana(activePlayer2.getMana() -
                                                            activePlayer2.getHero().getMana());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (Objects.equals(activePlayer2.getHero().getName(), "King Mudface") ||
                                    Objects.equals(activePlayer2.getHero().getName(), "General Kocioraw")) {
                                if (action.getAffectedRow() == 2 || action.getAffectedRow() == 3) {
                                    node.put("command", action.getCommand());
                                    node.put("affectedRow", action.getAffectedRow());
                                    node.put("error", "Selected row does not belong to the current player.");
                                    output.add(node);
                                }
                                else {
                                    if (Objects.equals(activePlayer2.getHero().getName(), "King Mudface")) {
                                        if (action.getAffectedRow() == 0) {
                                            for (Card card : player2Row0)
                                                card.setHealth(card.getHealth() + 1);
                                            activePlayer2.getHero().setHasAttacked(true);
                                            activePlayer2.setMana(activePlayer2.getMana() -
                                                    activePlayer2.getHero().getMana());
                                        }
                                        if (action.getAffectedRow() == 1) {
                                            for (Card card : player2Row1)
                                                card.setHealth(card.getHealth() + 1);
                                            activePlayer2.getHero().setHasAttacked(true);
                                            activePlayer2.setMana(activePlayer2.getMana() -
                                                    activePlayer2.getHero().getMana());
                                        }
                                    }
                                    if (Objects.equals(activePlayer2.getHero().getName(), "General Kocioraw")) {
                                        if (action.getAffectedRow() == 0) {
                                            for (Card card : player2Row0)
                                                card.setAttackDamage(card.getAttackDamage() + 1);
                                            activePlayer2.getHero().setHasAttacked(true);
                                            activePlayer2.setMana(activePlayer2.getMana() -
                                                    activePlayer2.getHero().getMana());
                                        }
                                        if (action.getAffectedRow() == 1) {
                                            for (Card card : player2Row1)
                                                card.setAttackDamage(card.getAttackDamage() + 1);
                                            activePlayer2.getHero().setHasAttacked(true);
                                            activePlayer2.setMana(activePlayer2.getMana() -
                                                    activePlayer2.getHero().getMana());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else if (Objects.equals(action.getCommand(), "getPlayerOneWins")) {
                    node.put("command", action.getCommand());
                    node.put("output", player1Wins);
                    output.add(node);

                }
                else if (Objects.equals(action.getCommand(), "getPlayerTwoWins")) {
                    node.put("command", action.getCommand());
                    node.put("output", player2Wins);
                    output.add(node);
                }
                else if (Objects.equals(action.getCommand(), "getTotalGamesPlayed")) {
                    node.put("command", action.getCommand());
                    node.put("output", totalGamesPlayed);
                    output.add(node);
                }
            }
        }


        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}
