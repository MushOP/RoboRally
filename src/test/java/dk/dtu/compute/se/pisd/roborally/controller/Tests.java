package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.dal.GameInDB;
import dk.dtu.compute.se.pisd.roborally.dal.IRepository;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.control.TextInputDialog;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dk.dtu.compute.se.pisd.roborally.dal.RepositoryAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class Tests {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    @Test
    void movePlayerTest() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        Player player = new Player(board, null,"Player 1");
        Space result = board.getSpace(1, 2);
        player.setSpace(result);

        assertEquals(result, player.getSpace());
    }

    @Test
    void setNameTest() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        Player player = new Player(board, null,"Player 1");
        player.setName("Test");
        assertEquals("Test", player.getName());
    }

    @Test
    void changeScoreTest() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        Player player = new Player(board, null,"Player 1");
        board.addPlayer(player);
        player.changeScore(2);
        assertEquals(2, player.getScore());
    }

    @Test
    void moveForwardTest() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        Player player = new Player(board, null,"Player 1");
        board.addPlayer(player);

        player.setSpace(board.getSpace(1,1));
        Space current = player.getSpace();
        if(current != null && player.board == current.board){
            Space target = board.getNeighbour(current, player.getHeading());
            if (target != null &&
                    target.canMoveTo(player.getHeading(), true) &&
                    current.canMoveTo(player.getHeading(), false)){
                if (target.getPlayer() == null) {
                    player.setSpace(target);
                    Space result = board.getSpace(1, 2);
                    assertEquals(result, player.getSpace());
                }
            }
        }
    }

    @Test
    void landCheckpointTest(){
        int ID = 0;
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        Player player = new Player(board, null,"Player 1");
        boolean unique = true;
        ArrayList<Integer> landedCheckpoints = player.getLandedCheckpoints();
        for (int i = 0; i < player.getLandedCheckpoints().size(); i++){
            if(landedCheckpoints.get(i) == ID){
                unique = false;
            }
        }
        if (unique){
            landedCheckpoints.add(ID);
            player.changeScore(1);
        }
        assertEquals(1, player.getScore());
    }

    @Test
    void getWinnerTest() {
        Checkpoint checkpoint = new Checkpoint(0);

        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        Space target = board.getSpace(1, 3);
        target.getActions().add(checkpoint);
        final List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

        for (int i = 0; i < 3; i++) {
            Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
            board.addPlayer(player);
            player.setSpace(board.getSpace(0 % board.width, i+2));
        }

        int checkpointQuantity = board.getCheckpointNumber();
        board.getPlayer(1).changeScore(1);
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            if (board.getPlayer(i).getScore() == checkpointQuantity) {
                assertEquals(1, i);
                break;
            }
        }
    }
}