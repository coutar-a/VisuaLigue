package sample;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;


/**
 * Created by Ambroise on 15/11/2016.
 */

//TODO : document this in the class diagram
public class PlayerCreationDialog extends Dialog {

    private TextField playerNameTextField;

    public PlayerCreationDialog () {

        // Set the dialog style.
        initStyle(StageStyle.UTILITY);
        // Set the button types.
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        // Set Title
        setTitle("Nouveau joueur");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setPrefWidth(250);

        playerNameTextField = new TextField();
        playerNameTextField.setPromptText("Nom du joueur");
        gridPane.add(playerNameTextField, 0, 0);
        getDialogPane().setContent(gridPane);


    }

    public PlayerCreationDialog (String name) {

        // Set the dialog style.
        initStyle(StageStyle.UTILITY);
        // Set the button types.
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        // Set Title
        setTitle("Modifier un joueur...");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setPrefWidth(250);

        playerNameTextField = new TextField(name);
        gridPane.add(playerNameTextField, 0, 0);
        getDialogPane().setContent(gridPane);


    }

    public String getPlayerName() {
        return playerNameTextField.getText();
    }

    public void setPlayerName(String playerNameTextField) {
        this.playerNameTextField.setText(playerNameTextField);
    }

}
