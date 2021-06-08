package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;

import java.util.Optional;

public class ExitDialog {

    public static void showDialog(Player winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Win screen");
        alert.setHeaderText(null);
        alert.setContentText("Player " + winner + " has won the game");
        alert.showAndWait();

    }
}
