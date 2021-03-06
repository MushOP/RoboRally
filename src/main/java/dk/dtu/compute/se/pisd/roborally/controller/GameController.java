/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.view.ExitDialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * This is the controller class responsible for execution of phases and cards
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController extends Subject {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        // TODO Assignment V1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free()
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if the player is moved

    }

    // XXX: V2
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX: V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }
    /**
     * @author Ekkart Kindler, ekki@dtu.dk
     * @author Jens Olesen s201729
     */
    // XXX: V2
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step = board.getStep() + 1;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        doActions();
                        checkGameOver();
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }
    
    public void executeCommandOptionAndContinue(@NotNull Command option){
        Player currentPlayer = board.getCurrentPlayer();

        if (currentPlayer != null && board.getPhase() == Phase.PLAYER_INTERACTION){

            board.setPhase(Phase.ACTIVATION);

            executeCommand(currentPlayer, option);

            int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
            if (nextPlayerNumber < board.getPlayersNumber()) {
                board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
            } else {
                int step = board.getStep() + 1;
                if (step < Player.NO_REGISTERS) {
                    makeProgramFieldsVisible(step);
                    board.setStep(step);
                    board.setCurrentPlayer(board.getPlayer(0));
                } else {
                    startProgrammingPhase();
                }
            } continuePrograms();
        }
    }

    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    // TODO Assignment V2
    /**
     * @author Rachid
     * @author Jens Olesen s201729
     */
    public boolean moveForward(@NotNull Player player) {
        Space current = player.getSpace();
        if(current != null && player.board == current.board){
            Space target = board.getNeighbour(current, player.getHeading());
            if (target != null &&
                    target.canMoveTo(player.getHeading(), true) &&
                    current.canMoveTo(player.getHeading(), false)){
                if (target.getPlayer() == null) {
                    player.setSpace(target);
                    return true;
                } else if (pushPlayer(target.getPlayer(), player.getHeading())) {
                    player.setSpace(target);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @author Jens Olesen s201729
     *
     */
    private boolean pushPlayer(Player player, @NotNull Heading heading) {
        try {
            Space current = player.getSpace();
            if (current != null && player.board == current.board) {
                Space target = board.getNeighbour(current, heading);
                if (target != null && target.getPlayer() == null &&
                        target.canMoveTo(heading, true) &&
                        current.canMoveTo(heading, false)) {
                    player.setSpace(target);
                    return true;
                }
            }
        }
        catch (NullPointerException e) {
            System.out.println("player is null in pushPlayer");
        }
        return false;
    }

    /**
     * @author Rachid
     *
     */
    // TODO Assignment V2
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
    }

    /**
     * @author Rachid
     * @author Jens Olesen s201729
     */
    // TODO Assignment V2
    public void turnRight(@NotNull Player player) {

        Space current = player.getSpace();

        if(current != null && player.board == current.board){

            player.setHeading(player.getHeading().next());
        }
    }

    /**
     * @author Rachid
     * @author Jens Olesen s201729
     */
    // TODO Assignment V2
    public void turnLeft(@NotNull Player player) {

        Space current = player.getSpace();

        if(current != null && player.board == current.board){

            player.setHeading(player.getHeading().prev());
        }
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }
    /**
     * @author Jens Olesen s201729
     * Executes the actions of any field that a player is positioned on
     * @return false if any action didn't succeed
     */
    private boolean  doActions() {
        boolean status = true;
        GameController gameController = new GameController(board);
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            if (board.getPlayer(i).getSpace().getActions().size() != 0) {
                for (int j = 0; j < board.getPlayer(i).getSpace().getActions().size(); j++) {
                    if (!(board.getPlayer(i).getSpace().getActions().get(j).doAction(gameController, board.getPlayer(i).getSpace()))) {
                        status = false;
                    }
                }
            }
        }
        return status;
    }
    /**
     * @author Jens Olesen s201729
     *
     */
    private void checkGameOver() {
        if (getWinner() > -1) {
            notifyChange();
        }
    }

    /**
     * @author Jens Olesen s201729
     * Method that updates a player's score when they land on a checkpoint
     */
    public int getWinner() {
        int checkpointQuantity = board.getCheckpointNumber();
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            if (board.getPlayer(i).getScore() == checkpointQuantity) {
                return i;
            }
        }
        return -1;
    }
    /**
     * @author Rachid, Zeinab, Muaz
     * Method that updates a player's score when they land on a checkpoint
     */
    public void landCheckpoint(int ID, Player player){
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
    }
    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }
}