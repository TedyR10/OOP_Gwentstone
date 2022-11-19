package main;

import Minions.*;
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

        Player startingPlayer1 = new Player(inputData, 1);
        Player startingPlayer2 = new Player(inputData, 2);

        for (int i = 0; i < inputData.getGames().size(); i++) {
            Game gameInput = new Game(inputData.getGames().get(i));
            GameSetup gameSetup = new GameSetup(gameInput, startingPlayer1, startingPlayer2);
            ArrayList<ActionsInput> actions = gameInput.getActions();
            Player activePlayer1 = gameSetup.getActivePlayer1();
            Player activePlayer2 = gameSetup.getActivePlayer2();
            System.out.println(filePath1);
            ArrayList<Card> player1Row2 = new ArrayList<Card>();
            ArrayList<Card> player1Row3 = new ArrayList<Card>();
            ArrayList<Card> player2Row0 = new ArrayList<Card>();
            ArrayList<Card> player2Row1 = new ArrayList<Card>();

            int playerTurn = gameInput.getStartingPlayer();
            activePlayer1.drawCard();
            activePlayer2.drawCard();
            for (ActionsInput action : actions) {

                ObjectNode node = objectMapper.createObjectNode();

                if (Objects.equals(action.getCommand(), "getPlayerDeck")) {
                    //node.putPOJO("command", output);
                    System.out.println("command: " + action.getCommand());
                    int playerIdx = action.getPlayerIdx();
                    System.out.println("playerIdx: " + playerIdx);

                    //node.putPOJO("playerIdx", playerIdx);
                    if (playerIdx == 1) {
                        System.out.println(activePlayer1.getCurrentDeck());
                    }
                    if (playerIdx == 2) {
                        System.out.println(activePlayer2.getCurrentDeck());
                    }
                }

                else if (Objects.equals(action.getCommand(), "getPlayerHero")) {
                    int playerIdx = action.getPlayerIdx();
                    System.out.println("command: " + action.getCommand());
                    System.out.println("playerIdx: " + playerIdx);

                    //node.putPOJO("playerIdx", playerIdx);
                    if (playerIdx == 1) {
                        System.out.println(activePlayer1.getHero());
                    }
                    if (playerIdx == 2) {
                        System.out.println(activePlayer2.getHero());
                    }
                }
                else if (Objects.equals(action.getCommand(), "getPlayerTurn")) {
                    System.out.println("command: " + action.getCommand());
                    if (playerTurn % 2 == 0)
                        System.out.println("2");
                    else System.out.println("1");
                }
                else if (Objects.equals(action.getCommand(), "getCardsInHand")) {
                    System.out.println("command: " + action.getCommand());
                    if (action.getPlayerIdx() == 2) {
                        System.out.println("playerIdx: 2");
                        System.out.println(activePlayer2.getHand());
                    }
                    else {
                        System.out.println("playerIdx: 1");
                        System.out.println(activePlayer1.getHand());
                    }
                }
                else if (Objects.equals(action.getCommand(), "getPlayerMana")) {
                    System.out.println("command: " + action.getCommand());
                    if (action.getPlayerIdx() == 2) {
                        System.out.println(action.getPlayerIdx());
                        System.out.println(activePlayer2.getMana());
                    }
                    else {
                        System.out.println(action.getPlayerIdx());
                        System.out.println(activePlayer1.getMana());
                    }
                }
                else if (Objects.equals(action.getCommand(), "endPlayerTurn")) {
                    System.out.println("command: " + action.getCommand());
                    if (playerTurn % 2 == 0) {
                        activePlayer2.setEndTurn(true);
                        if (activePlayer1.isEndTurn() && activePlayer2.isEndTurn()) {
                            activePlayer1.newTurn();
                            activePlayer2.newTurn();
                            activePlayer1.setEndTurn(false);
                            activePlayer2.setEndTurn(false);
                        }
                        for (Card card : player2Row0) {
                            if (card.isFrozen())
                                card.setFrozen(false);
                            if (card.isHasAttacked())
                                card.setHasAttacked(false);
                        }
                        for (Card card : player2Row1) {
                            if (card.isFrozen())
                                card.setFrozen(false);
                            if (card.isHasAttacked())
                                card.setHasAttacked(false);
                        }
                    }
                    else {
                        activePlayer1.setEndTurn(true);
                        if (activePlayer1.isEndTurn() && activePlayer2.isEndTurn()) {
                            activePlayer1.newTurn();
                            activePlayer2.newTurn();
                            activePlayer1.setEndTurn(false);
                            activePlayer2.setEndTurn(false);
                        }
                        for (Card card : player1Row2) {
                            if (card.isFrozen())
                                card.setFrozen(false);
                            if (card.isHasAttacked())
                                card.setHasAttacked(false);
                        }
                        for (Card card : player1Row3) {
                            if (card.isFrozen())
                                card.setFrozen(false);
                            if (card.isHasAttacked())
                                card.setHasAttacked(false);
                        }
                    }
                    playerTurn++;
                }
                else if (Objects.equals(action.getCommand(), "placeCard")) {
                    System.out.println("command: " + action.getCommand());
                    int handIdx = action.getHandIdx();
                    if (playerTurn % 2 == 0) {
                        if (handIdx < activePlayer2.getHand().size()) {
                            if (activePlayer2.getHand().get(handIdx) instanceof Environment) {
                                System.out.println("Cannot place environment card on table.");
                            } else if (activePlayer2.getHand().get(handIdx).getMana() > activePlayer2.getMana()) {
                                System.out.println("Not enough mana to place card on table.");
                            } else {
                                if (activePlayer2.getHand().get(handIdx) instanceof TheRipper ||
                                        activePlayer2.getHand().get(handIdx) instanceof Miraj ||
                                        activePlayer2.getHand().get(handIdx) instanceof Goliath ||
                                        activePlayer2.getHand().get(handIdx) instanceof Warden) {
                                    if (player2Row1.size() < 5) {
                                        player2Row1.add(activePlayer2.getHand().get(handIdx));
                                        int previousMana = activePlayer2.getMana();
                                        int manaNext = previousMana - activePlayer2.getHand().get(handIdx).getMana();
                                        activePlayer2.setMana(manaNext);
                                        System.out.println("Player 2 placed a card worth " + activePlayer2.getHand().get(handIdx).getMana()
                                                + " mana. Remaining mana: " + activePlayer2.getMana());
                                        activePlayer2.getHand().remove(handIdx);
                                    }
                                    else System.out.println("Cannot place card on table since row is full.");
                                }
                                else if (activePlayer2.getHand().get(handIdx) instanceof Sentinel ||
                                        activePlayer2.getHand().get(handIdx) instanceof Berserker ||
                                        activePlayer2.getHand().get(handIdx) instanceof TheCursedOne ||
                                        activePlayer2.getHand().get(handIdx) instanceof Disciple) {
                                    if (player2Row0.size() < 5) {
                                        player2Row0.add(activePlayer2.getHand().get(handIdx));
                                        int previousMana = activePlayer2.getMana();
                                        int manaNext = previousMana - activePlayer2.getHand().get(handIdx).getMana();
                                        activePlayer2.setMana(manaNext);
                                        System.out.println("Player 2 placed a card worth " + activePlayer2.getHand().get(handIdx).getMana()
                                                + " mana. Remaining mana: " + activePlayer2.getMana());
                                        activePlayer2.getHand().remove(handIdx);
                                    }
                                    else System.out.println("Cannot place card on table since row is full.");
                                }
                            }
                        }
                    }
                    else {
                        if (handIdx < activePlayer1.getHand().size()) {
                            if (activePlayer1.getHand().get(handIdx) instanceof Environment) {
                                System.out.println("Cannot place environment card on table.");
                            } else if (activePlayer1.getHand().get(handIdx).getMana() > activePlayer1.getMana()) {
                                System.out.println("Not enough mana to place card on table.");
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
                                        System.out.println("Player 1 placed a card worth " + activePlayer1.getHand().get(handIdx).getMana()
                                                + " mana. Remaining mana: " + activePlayer1.getMana());
                                        activePlayer1.getHand().remove(handIdx);
                                    }
                                    else System.out.println("Cannot place card on table since row is full.");
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
                                        System.out.println("Player 1 placed a card worth " + activePlayer1.getHand().get(handIdx).getMana()
                                                + " mana. Remaining mana: " + activePlayer1.getMana());
                                        activePlayer1.getHand().remove(handIdx);
                                    }
                                    else System.out.println("Cannot place card on table since row is full.");
                                }
                            }
                        }
                    }
                }
                else if (Objects.equals(action.getCommand(), "getCardsOnTable")) {
                    System.out.println("command: " + action.getCommand());
                    ArrayList<Card> allCards = new ArrayList<Card>();
                    allCards.addAll(player2Row0);
                    //System.out.println("Cards on player2 table: " + player2Table);
                    allCards.addAll(player2Row1);
                   // System.out.println("Cards on player1 table: " + player1Table);
                    allCards.addAll(player1Row2);
                    //System.out.println("Cards in player 1 hand: " + activePlayer1.getHand());
                    allCards.addAll(player1Row3);
                    System.out.println(allCards);
                }
                else if (Objects.equals(action.getCommand(), "getEnvironmentCardsInHand")) {
                    System.out.println("command: " + action.getCommand());
                    System.out.println(action.getPlayerIdx());
                    ArrayList<Card> envrCards = new ArrayList<Card>();
                    if (action.getPlayerIdx() == 1) {
                        for (Card card : activePlayer1.getHand()) {
                            if (card instanceof Environment) {
                                envrCards.add(card);
                            }
                        }
                        System.out.println(envrCards);
                    }
                    else {
                        for (Card card : activePlayer2.getHand()) {
                            if (card instanceof Environment) {
                                envrCards.add(card);
                            }
                        }
                        System.out.println(envrCards);
                    }
                }
                else if (Objects.equals(action.getCommand(), "getCardAtPosition")) {
                    System.out.println("command: " + action.getCommand());
                    System.out.println("x: " + action.getX());
                    System.out.println("y: " + action.getY());
                    if (action.getX() == 0) {
                        if (action.getY() < player2Row0.size())
                            System.out.println(player2Row0.get(action.getY()));
                        else System.out.println("No card available at that position.");
                    }
                    else if (action.getX() == 1) {
                        if (action.getY() < player2Row1.size())
                            System.out.println(player2Row1.get(action.getY()));
                        else System.out.println("No card available at that position.");
                    }
                    else if (action.getX() == 2) {
                        if (action.getY() < player1Row2.size())
                            System.out.println(player1Row2.get(action.getY()));
                        else System.out.println("No card available at that position.");
                    }
                    else if (action.getX() == 3) {
                        if (action.getY() < player1Row3.size())
                            System.out.println(player1Row3.get(action.getY()));
                        else System.out.println("No card available at that position.");
                    }
                }
                else if (Objects.equals(action.getCommand(), "useEnvironmentCard")) {
                    System.out.println("command: " + action.getCommand());
                    System.out.println("handIdx: " + action.getHandIdx());
                    System.out.println("affectedRow: " + action.getAffectedRow());
                    if (playerTurn % 2 != 0) {
                        if (!(activePlayer1.getHand().get(action.getHandIdx()) instanceof Environment)) {
                            System.out.println("Chosen card is not of type environment.");
                        }
                        else if (activePlayer1.getHand().get(action.getHandIdx()).getMana() > activePlayer1.getMana())
                            System.out.println("Not enough mana to use environment card.");
                        else if (action.getAffectedRow() == 2 || action.getAffectedRow() == 3)
                            System.out.println("Chosen row does not belong to the enemy.");
                        else {
                            if (Objects.equals(activePlayer1.getHand().get(action.getHandIdx()).getName(),
                                    "Firestorm")) {
                                if (action.getAffectedRow() == 0) {
                                    int size = player2Row0.size();
                                    for (int j = 0; j < size; j++) {
                                        if (player2Row0.get(j).getHealth() == 1) {
                                            player2Row0.remove(player2Row0.get(j));
                                            j--;
                                            size--;
                                        }
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
                                        if (player2Row1.get(j).getHealth() == 1) {
                                            player2Row1.remove(player2Row1.get(j));
                                            j--;
                                            size--;
                                        }
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
                                    //may need to put it into minions?
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
                                    if (player1Row3.size() < 5) {
                                        int maxHealth = 0;
                                        for (Card card : player2Row0) {
                                            if (card.getHealth() > maxHealth) {
                                                maxHealth = card.getHealth();
                                            }
                                        }
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
                                    else System.out.println("Cannot steal enemy card since the player's row is full.");
                                }
                                else if (action.getAffectedRow() == 1) {
                                    if (player1Row2.size() < 5) {
                                        int maxHealth = 0;
                                        for (Card card : player2Row1) {
                                            if (card.getHealth() > maxHealth) {
                                                maxHealth = card.getHealth();
                                            }
                                        }
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
                                    else System.out.println("Cannot steal enemy card since the player's row is full.");
                                }
                            }
                        }
                    }
                    else {
                        if (!(activePlayer2.getHand().get(action.getHandIdx()) instanceof Environment)) {
                            System.out.println("Chosen card is not of type environment.");
                        }
                        else if (activePlayer2.getHand().get(action.getHandIdx()).getMana() > activePlayer2.getMana())
                            System.out.println("Not enough mana to use environment card.");
                        else if (action.getAffectedRow() == 0 || action.getAffectedRow() == 1)
                            System.out.println("Chosen row does not belong to the enemy.");
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
                                    else System.out.println("Cannot steal enemy card since the player's row is full.");
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
                                    else System.out.println("Cannot steal enemy card since the player's row is full.");
                                }
                            }
                        }
                    }
                }
                else if (Objects.equals(action.getCommand(), "getFrozenCardsOnTable")) {
                    System.out.println("command: " + action.getCommand());
                    ArrayList<Card> frozenCards = new ArrayList<Card>();
                    for (Card card : player2Row0)
                        if (card.isFrozen())
                            frozenCards.add(card);
                    for (Card card : player2Row1)
                        if (card.isFrozen())
                            frozenCards.add(card);
                    for (Card card : player1Row2)
                        if (card.isFrozen())
                            frozenCards.add(card);
                    for (Card card : player1Row3)
                        if (card.isFrozen())
                            frozenCards.add(card);
                    System.out.println(frozenCards);
                }
                else if (Objects.equals(action.getCommand(), "cardUsesAttack")) {
                    System.out.println("command: " + action.getCommand());
                    System.out.println("cardAttacker: ");
                    System.out.println("x: " + action.getCardAttacker().getX());
                    System.out.println("y: " + action.getCardAttacker().getY());
                    System.out.println("cardAttacked: ");
                    System.out.println("x: " + action.getCardAttacked().getX());
                    System.out.println("y: " + action.getCardAttacked().getY());
                    if (playerTurn % 2 != 0) {
                        if (action.getCardAttacked().getX() == 2 || action.getCardAttacked().getX() == 3) {
                            System.out.println("Attacked card does not belong to the enemy.");
                        }
                        else if (action.getCardAttacker().getX() == 2) {
                            if (player1Row2.get(action.getCardAttacker().getY()).isFrozen())
                                System.out.println("Attacker card is frozen.");
                            else if (player1Row2.get(action.getCardAttacker().getY()).isHasAttacked())
                                System.out.println("Attacker card has already attacked this turn.");
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
                                            System.out.println("Attacked card is not of type 'Tank’.");
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
                                        System.out.println("Attacked card is not of type 'Tank’.");
                                    }
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
                            if (player1Row3.get(action.getCardAttacker().getY()).isFrozen())
                                System.out.println("Attacker card is frozen.");
                            else if (player1Row3.get(action.getCardAttacker().getY()).isHasAttacked())
                                System.out.println("Attacker card has already attacked this turn.");
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
                                            System.out.println("Attacked card is not of type 'Tank’.");
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
                                        System.out.println("Attacked card is not of type 'Tank’.");
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
                            System.out.println("Attacked card does not belong to the enemy.");
                        }
                        else if (action.getCardAttacker().getX() == 1) {
                            if (player2Row1.get(action.getCardAttacker().getY()).isFrozen())
                                System.out.println("Attacker card is frozen.");
                            else if (player2Row1.get(action.getCardAttacker().getY()).isHasAttacked())
                                System.out.println("Attacker card has already attacked this turn.");
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
                                            System.out.println("Attacked card is not of type 'Tank’.");
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
                                        System.out.println("Attacked card is not of type 'Tank’.");
                                    }
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
                            if (player2Row0.get(action.getCardAttacker().getY()).isFrozen())
                                System.out.println("Attacker card is frozen.");
                            else if (player2Row0.get(action.getCardAttacker().getY()).isHasAttacked())
                                System.out.println("Attacker card has already attacked this turn.");
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
                                            System.out.println("Attacked card is not of type 'Tank’.");
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
                                        System.out.println("Attacked card is not of type 'Tank’.");
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
                else if (Objects.equals(action.getCommand(), "cardUsesAbility")) {
                    System.out.println("command: " + action.getCommand());
                    System.out.println("cardAttacker: ");
                    System.out.println("x: " + action.getCardAttacker().getX());
                    System.out.println("y: " + action.getCardAttacker().getY());
                    System.out.println("cardAttacked: ");
                    System.out.println("x: " + action.getCardAttacked().getX());
                    System.out.println("y: " + action.getCardAttacked().getY());
                    if (playerTurn % 2 != 0) {
                        if (action.getCardAttacker().getX() == 2) {
                            if (player1Row2.get(action.getCardAttacker().getY()).isFrozen())
                                System.out.println("Attacker card is frozen.");
                            else if (player1Row2.get(action.getCardAttacker().getY()).isHasAttacked())
                                System.out.println("Attacker card has already attacked this turn.");
                            else {
                                if (player1Row2.get(action.getCardAttacker().getY()) instanceof Disciple) {
                                    if (action.getCardAttacked().getX() == 0 || action.getCardAttacked().getX() == 1) {
                                        System.out.println("Attacked card does not belong to the current player.");
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
                                        System.out.println("Attacked card does not belong to the enemy.");
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
                                                    System.out.println("Attacked card is not of type 'Tank’.");
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
                                                System.out.println("Attacked card is not of type 'Tank’.");
                                            } else {
                                                if (player1Row2.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player1Row2.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player1Row2.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player1Row2.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player1Row2.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player1Row2.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                                    if (player2Row0.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player2Row0.remove(action.getCardAttacked().getY());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getCardAttacker().getX() == 3) {
                            if (player1Row3.get(action.getCardAttacker().getY()).isFrozen())
                                System.out.println("Attacker card is frozen.");
                            else if (player1Row3.get(action.getCardAttacker().getY()).isHasAttacked())
                                System.out.println("Attacker card has already attacked this turn.");
                            else {
                                if (player1Row2.get(action.getCardAttacker().getY()) instanceof Disciple) {
                                    if (action.getCardAttacked().getX() == 0 || action.getCardAttacked().getX() == 1) {
                                        System.out.println("Attacked card does not belong to the current player.");
                                    }
                                    else {
                                        Disciple disciple = (Disciple) player1Row2.get(action.getCardAttacker().getY());
                                        if (action.getCardAttacked().getX() == 2) {
                                            disciple.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                        }
                                        else {
                                            disciple.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                        }
                                    }
                                }
                                else {
                                    if (action.getCardAttacked().getX() == 2 || action.getCardAttacked().getX() == 3) {
                                        System.out.println("Attacked card does not belong to the enemy.");
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
                                                    System.out.println("Attacked card is not of type 'Tank’.");
                                                } else {
                                                    if (player1Row3.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                        TheRipper theRipper = (TheRipper) player1Row3.get(action.getCardAttacker().getY());
                                                        theRipper.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                    }
                                                    else if (player1Row3.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                        Miraj miraj = (Miraj) player1Row3.get(action.getCardAttacker().getY());
                                                        miraj.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                    }
                                                    else if (player1Row3.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                        TheCursedOne theCursedOne = (TheCursedOne) player1Row3.get(action.getCardAttacker().getY());
                                                        theCursedOne.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                        if (player2Row1.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                            player2Row1.remove(action.getCardAttacked().getY());
                                                    }
                                                }
                                            } else {
                                                if (player1Row3.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player1Row3.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player1Row3.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player1Row3.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player1Row3.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player1Row3.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                                    if (player2Row1.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player2Row1.remove(action.getCardAttacked().getY());
                                                }
                                            }
                                        } else if (action.getCardAttacked().getX() == 0) {
                                            if (tank == 1) {
                                                System.out.println("Attacked card is not of type 'Tank’.");
                                            } else {
                                                if (player1Row3.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player1Row3.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player1Row3.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player1Row3.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player1Row3.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player1Row3.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                                    if (player2Row0.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player2Row0.remove(action.getCardAttacked().getY());
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
                            if (player2Row1.get(action.getCardAttacker().getY()).isFrozen())
                                System.out.println("Attacker card is frozen.");
                            else if (player2Row1.get(action.getCardAttacker().getY()).isHasAttacked())
                                System.out.println("Attacker card has already attacked this turn.");
                            else {
                                if (player2Row1.get(action.getCardAttacker().getY()) instanceof Disciple) {
                                    if (action.getCardAttacked().getX() == 2 || action.getCardAttacked().getX() == 3) {
                                        System.out.println("Attacked card does not belong to the current player.");
                                    }
                                    else {
                                        Disciple disciple = (Disciple) player2Row1.get(action.getCardAttacker().getY());
                                        if (action.getCardAttacked().getX() == 1) {
                                            disciple.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                        }
                                        else {
                                            disciple.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                        }
                                    }
                                }
                                else {
                                    if (action.getCardAttacked().getX() == 0 || action.getCardAttacked().getX() == 1) {
                                        System.out.println("Attacked card does not belong to the enemy.");
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
                                                    System.out.println("Attacked card is not of type 'Tank’.");
                                                } else {
                                                    if (player2Row1.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                        TheRipper theRipper = (TheRipper) player2Row1.get(action.getCardAttacker().getY());
                                                        theRipper.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                    }
                                                    else if (player2Row1.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                        Miraj miraj = (Miraj) player2Row1.get(action.getCardAttacker().getY());
                                                        miraj.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                    }
                                                    else if (player2Row1.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                        TheCursedOne theCursedOne = (TheCursedOne) player2Row1.get(action.getCardAttacker().getY());
                                                        theCursedOne.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                        if (player1Row2.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                            player1Row2.remove(action.getCardAttacked().getY());
                                                    }
                                                }
                                            } else {
                                                if (player2Row1.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player2Row1.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player2Row1.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player2Row1.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player2Row1.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player2Row1.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                    if (player1Row2.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player1Row2.remove(action.getCardAttacked().getY());
                                                }
                                            }
                                        } else if (action.getCardAttacked().getX() == 3) {
                                            if (tank == 1) {
                                                System.out.println("Attacked card is not of type 'Tank’.");
                                            } else {
                                                if (player2Row1.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player2Row1.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player2Row1.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player2Row1.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player2Row1.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player2Row1.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                                    if (player1Row3.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player1Row3.remove(action.getCardAttacked().getY());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getCardAttacker().getX() == 0) {
                            if (player2Row0.get(action.getCardAttacker().getY()).isFrozen())
                                System.out.println("Attacker card is frozen.");
                            else if (player2Row0.get(action.getCardAttacker().getY()).isHasAttacked())
                                System.out.println("Attacker card has already attacked this turn.");
                            else {
                                if (player2Row0.get(action.getCardAttacker().getY()) instanceof Disciple) {
                                    if (action.getCardAttacked().getX() == 2 || action.getCardAttacked().getX() == 3) {
                                        System.out.println("Attacked card does not belong to the current player.");
                                    }
                                    else {
                                        Disciple disciple = (Disciple) player2Row0.get(action.getCardAttacker().getY());
                                        if (action.getCardAttacked().getX() == 1) {
                                            disciple.action((Minion) player2Row1.get(action.getCardAttacked().getY()));
                                        }
                                        else {
                                            disciple.action((Minion) player2Row0.get(action.getCardAttacked().getY()));
                                        }
                                    }
                                }
                                else {
                                    if (action.getCardAttacked().getX() == 0 || action.getCardAttacked().getX() == 1) {
                                        System.out.println("Attacked card does not belong to the enemy.");
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
                                                    System.out.println("Attacked card is not of type 'Tank’.");
                                                } else {
                                                    if (player2Row0.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                        TheRipper theRipper = (TheRipper) player2Row0.get(action.getCardAttacker().getY());
                                                        theRipper.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                    }
                                                    else if (player2Row0.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                        Miraj miraj = (Miraj) player2Row0.get(action.getCardAttacker().getY());
                                                        miraj.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                    }
                                                    else if (player2Row0.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                        TheCursedOne theCursedOne = (TheCursedOne) player2Row0.get(action.getCardAttacker().getY());
                                                        theCursedOne.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                        if (player1Row2.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                            player1Row2.remove(action.getCardAttacked().getY());
                                                    }
                                                }
                                            } else {
                                                if (player2Row0.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player2Row0.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player2Row0.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player2Row0.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player2Row0.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player2Row0.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player1Row2.get(action.getCardAttacked().getY()));
                                                    if (player1Row2.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player1Row2.remove(action.getCardAttacked().getY());
                                                }
                                            }
                                        } else if (action.getCardAttacked().getX() == 3) {
                                            if (tank == 1) {
                                                System.out.println("Attacked card is not of type 'Tank’.");
                                            } else {
                                                if (player2Row0.get(action.getCardAttacker().getY()) instanceof TheRipper) {
                                                    TheRipper theRipper = (TheRipper) player2Row0.get(action.getCardAttacker().getY());
                                                    theRipper.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player2Row0.get(action.getCardAttacker().getY()) instanceof Miraj) {
                                                    Miraj miraj = (Miraj) player2Row0.get(action.getCardAttacker().getY());
                                                    miraj.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                                }
                                                else if (player2Row0.get(action.getCardAttacker().getY()) instanceof TheCursedOne) {
                                                    TheCursedOne theCursedOne = (TheCursedOne) player2Row0.get(action.getCardAttacker().getY());
                                                    theCursedOne.action((Minion) player1Row3.get(action.getCardAttacked().getY()));
                                                    if (player1Row3.get(action.getCardAttacked().getY()).getHealth() == 0)
                                                        player1Row3.remove(action.getCardAttacked().getY());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}
