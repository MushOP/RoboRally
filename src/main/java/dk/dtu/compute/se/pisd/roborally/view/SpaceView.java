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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.Gear;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 75; // 60; // 75;
    final public static int SPACE_WIDTH = 75;  // 60; // 75;

    public final Space space;

    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        this.getChildren().clear();
        Player player = space.getPlayer();
        showWalls(space.getWalls());
        showCBelt();
        showRotationBelt();
        updateCheckpoint();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }


    private void updateCheckpoint() {
        if (space.isActionType(Checkpoint.class)) {
            Circle circle= new Circle(15.0);
            circle.setFill(Color.BLUE);
            this.getChildren().add(circle);
        }
    }

    private void showRotationBelt() {
        if (space.isActionType(Gear.class)) {
            Circle circle= new Circle(10.0);
            Gear gear = ((Gear) space.getActions().get(0));
            if (gear.getDirection() == 0) {
                circle.setFill(Color.GREEN);
            } else {
                circle.setFill(Color.RED);
            }
            this.getChildren().add(circle);
        }
    }



    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
        }
    }

    public void showWalls(@NotNull ArrayList<Heading> walls) {

        for (int i = 0; i < walls.size(); i++) {

            if (walls.get(i) == Heading.NORTH) {
                showWall(0,2,SPACE_WIDTH,2);

            } else if (walls.get(i) == Heading.EAST) {
                showWall(SPACE_WIDTH-2,0,SPACE_WIDTH-2,SPACE_HEIGHT);

            } else if (walls.get(i) == Heading.SOUTH) {
                showWall(0,SPACE_HEIGHT-2,SPACE_WIDTH,SPACE_HEIGHT-2);

            } else if (walls.get(i) == Heading.WEST) {
                showWall(2,0,2,SPACE_HEIGHT);

            }
        }
    }

    /**
     * ...
     *
     * @author Ekkart Kindler, ekki@dtu.dk
     * @author Jens Olesen
     */

    public void showWall(int x1, int y1, int x2, int y2) {
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.setLineWidth(5);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.strokeLine(x1, y1,x2, y2);
        this.getChildren().add(canvas);
    }


    private void showCBelt() {
        if (space.isActionType(ConveyorBelt.class)) {
            FieldAction conveyerbelt = space.getAction(ConveyorBelt.class);
            Polygon arrow = new Polygon(00.0, 00.0,
                    16.0, 32.0,
                    32.0, 0.0 );
            try {
                arrow.setFill(Color.ORANGE);
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*((ConveyorBelt) conveyerbelt).getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }
}
