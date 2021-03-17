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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {

    public final Board board;

    public final int x;
    public final int y;
    private boolean hasWall = false;

    Wall walls = new Wall();

    private Player player;

    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;

        if (x > 5) {

            walls.heading = Heading.WEST;

//            walls[0].heading = Heading.WEST;
//            walls[1].heading = Heading.WEST;
//            walls[2].heading = Heading.WEST;
//            walls[3].heading = Heading.WEST;
            this.hasWall = true;
        }

//        if ((x + y) % 2 == 0) {
//            boolean wall = true;
//        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    public boolean hasWall() {
        return hasWall;
    }

    public boolean canMoveTo(Heading heading) {

        if (!hasWall) {
            return true;

        } else if (walls.heading == heading || walls.heading == heading.prev().prev()) {

            return false;
        }

//        for (int i = 0; i < walls.length; i++) {
//
//            if (walls[i].heading == heading || walls[i].heading == heading.prev().prev()) {
//
//                return false;
//            }
//        }
        return true;
    }



}
