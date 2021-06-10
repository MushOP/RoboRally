package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Zeinab
 * @author Rachid
 * @author Muaz
 */

public class Checkpoint extends FieldAction {
    int ID;
    public Checkpoint(int ID){
        this.ID = ID;
    }
    public int getID(){
        return ID;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Player player = space.getPlayer();
        gameController.landCheckpoint(ID,player);
        return true;
    }
}
