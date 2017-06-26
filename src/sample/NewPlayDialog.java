package sample;


import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * Created by Charles on 10/10/2016.
 */
public class NewPlayDialog extends Dialog {

    /* -  Dialog widgets - */
    TextField textFieldPlayTitle;
    ComboBox<Sport> comboBoxPlaySport;
    Button buttonNewSport;
    TextArea textAreaPlayDescription;

    /* -  Constructor - */
    public NewPlayDialog(FrontController frontController,
                         ObservableList<Sport> sportObservableList,
                         ObservableList<GameObjective> gameObjectiveObservableArray,
                         String playTitle,
                         String playDescription) {
        // Set the dialog style.
        initStyle(StageStyle.UTILITY);
        // Set the button types.
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        // Set Title
        setTitle("VisuaLigue - Nouveau Jeu");
        // Create the grid pane to place widgets.
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setPrefWidth(500);
        // Initialize the widgets.
        textFieldPlayTitle = new TextField();
        textFieldPlayTitle.setPromptText("Titre de la stratégie");
        if (playTitle != null)
            textFieldPlayTitle.setText(playTitle);
        comboBoxPlaySport = new ComboBox<>(sportObservableList);
        comboBoxPlaySport.setPromptText("Sports");
        comboBoxPlaySport.setPrefWidth(150);
        comboBoxPlaySport.setCellFactory(new Callback<ListView<Sport>, ListCell<Sport>>() {

            @Override
            public ListCell<Sport> call(ListView<Sport> p) {
                final ListCell<Sport> cell = new ListCell<Sport>() {
                    @Override
                    protected void updateItem(Sport sport, boolean bln) {
                        super.updateItem(sport, bln);
                        if (sport != null) {
                            setText(sport.getName());
                        } else {
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });
        comboBoxPlaySport.setButtonCell(new ListCell<Sport>() {
            @Override
            protected void updateItem(Sport item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        comboBoxPlaySport.getSelectionModel().selectFirst();
        buttonNewSport = new Button("+");
        buttonNewSport.setPrefWidth(50);
        buttonNewSport.setAlignment(Pos.CENTER);
        buttonNewSport.setOnAction(event -> frontController.showNewSportDialog(null, null, null, null, -1, -1, null, null, null, null, null, null));
        textAreaPlayDescription = new TextArea();
        textAreaPlayDescription.setPromptText("Description");
        if (playDescription != null)
            textAreaPlayDescription.setText(playDescription);
        // Adding widgets to the grid pane.
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        column1.setPercentWidth(20);
        column2.setPercentWidth(80);
        gridPane.getColumnConstraints().addAll(column1, column2);
        gridPane.add(new Label("Titre:"), 0, 0, 2, 1);
        gridPane.add(textFieldPlayTitle, 0, 1, 2, 1);
        gridPane.add(new Label("Sport:"), 0, 2, 1, 1);
        gridPane.add(comboBoxPlaySport, 0, 3, 1, 1);
        gridPane.add(buttonNewSport, 1, 3, 1, 1);
        gridPane.add(new Label("Description"), 0, 4, 2, 1);
        gridPane.add(textAreaPlayDescription, 0, 5, 2, 1);
        // Set the grid pane as content for the Dialog.
        getDialogPane().setContent(gridPane);
    }

    /* - Methods - */

    /**
     * Returns the title entered by the user in the text field.
     *
     * @return String : Title of the new play.
     */
    public String getPlayTitle() {
        return textFieldPlayTitle.getText();
    }

    /**
     * Returns the sport chosen by the user in the combo box.
     *
     * @return Sport : Sport associated with the new play.
     */
    public Sport getPlaySport() {
        return comboBoxPlaySport.getValue();
    }

    /**
     * Returns the description entered by the user in the text area.
     *
     * @return String : Description of the new play.
     */
    public String getPlayDescription() {
        return textAreaPlayDescription.getText();
    }
}
