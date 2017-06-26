package sample;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;

/**
 * Created by Ambroise on 06/12/2016.
 */
public class TeamSizeEditDialog extends Dialog {

    private TextField teamSizeTextField;

    public TeamSizeEditDialog () {

        // Set the dialog style.
        initStyle(StageStyle.UTILITY);
        // Set the button types.
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        // Set Title
        setTitle("Nombre de joueurs");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setPrefWidth(250);

        teamSizeTextField = new TextField();
        teamSizeTextField.setPromptText("Taille de l'équipe");
        gridPane.add(teamSizeTextField, 0, 0);
        getDialogPane().setContent(gridPane);

    }

    public TeamSizeEditDialog (String teamSize) {

        // Set the dialog style.
        initStyle(StageStyle.UTILITY);
        // Set the button types.
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        // Set Title
        setTitle("Nombre de joueurs");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setPrefWidth(250);

        teamSizeTextField = new TextField(teamSize);
        gridPane.add(teamSizeTextField, 0, 0);
        getDialogPane().setContent(gridPane);

    }

    public String getTeamSizeTextField() {
        return teamSizeTextField.getText();
    }
}
