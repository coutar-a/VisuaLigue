package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Charles on 10/10/2016.
 */
public class NewSportDialog extends Dialog {

    /* - Dialog widgets - */
    private TextField textFieldSportName;
    private ComboBox<GameObjective> comboBoxObjective;
    private Button addNewGameObjective;
    private TextArea textAreaSportDescription;
    /* Text field for the teams and its number of players.*/
    private TextField textFieldNumberOfTeams;
    private TextField textFieldNumberOfPlayers;

    private File fieldFile;

    private Button buttonAddPosition;
    private Button buttonRemovePosition;
    private ListView<Player> listViewRoles;
    private ObservableList<Player> rolesList;

    private ImageView imageFieldTypeHockey;
    private ImageView imageFieldTypeFootball;
    private ImageView imageFieldTypeBasketball;
    private ToggleGroup toggleGroupFieldType;
    private RadioButton radioButtonFieldTypeHockey;
    private RadioButton radioButtonFieldTypeFootball;
    private RadioButton radioButtonFieldTypeBasketball;
    private RadioButton radioButtonFieldTypeCustom;

    private TextField textFieldFieldUnits;
    private TextField textFieldFieldLength;
    private TextField textFieldFieldWidth;
    private ImageView fieldImageView; /* Image view for field. */
    private Button buttonImportFieldImage; /* Button for field image. */
    private FileChooser fileChooserFieldImage; /* File chooser for field image. */

    /* - Constructor - */
    public NewSportDialog(FrontController frontController, ObservableList<GameObjective> gameObjectiveObservableArray) {
        // Set the dialog style.
        initStyle(StageStyle.UTILITY);
        // Set the button types.
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        // Set Title
        setTitle("VisuaLigue - Nouveau Sport");
        // Instanciating the panes to place the widgets.
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        GridPane gridPaneLeft = new GridPane();
        GridPane gridPaneRight = new GridPane();
        gridPaneLeft.setHgap(10);
        gridPaneLeft.setVgap(10);
        gridPaneLeft.setPadding(new Insets(10, 10, 10, 10));
        gridPaneLeft.setPrefWidth(350);
        gridPaneRight.setHgap(10);
        gridPaneRight.setVgap(10);
        gridPaneRight.setPadding(new Insets(10, 10, 10, 10));
        gridPaneRight.setPrefWidth(650);
        splitPane.setPrefWidth(1000);
        splitPane.setPrefHeight(500);
        splitPane.setPadding(new Insets(10, 10, 10, 10));

        // Initialize the dialog widgets.
        textFieldSportName = new TextField();
        textFieldSportName.setPromptText("Nom du sport");
        comboBoxObjective = new ComboBox<>(gameObjectiveObservableArray);
        comboBoxObjective.setPrefWidth(150);
        comboBoxObjective.setCellFactory(new Callback<ListView<GameObjective>,ListCell<GameObjective>>(){

            @Override
            public ListCell<GameObjective> call(ListView<GameObjective> p) {
                final ListCell<GameObjective> cell = new ListCell<GameObjective>(){
                    @Override
                    protected void updateItem(GameObjective gameObjective, boolean bln) {
                        super.updateItem(gameObjective, bln);
                        if(gameObjective != null){
                            setText(gameObjective.getsName());
                        }else{
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });
        comboBoxObjective.setButtonCell(new ListCell<GameObjective>() {
            @Override
            protected void updateItem(GameObjective item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getsName());
                }
            }
        });
        comboBoxObjective.getSelectionModel().selectFirst();

        comboBoxObjective.setPromptText("Type d'objectif");
        addNewGameObjective = new Button(" + ");
        addNewGameObjective.setOnAction(event -> frontController.showNewGameObjectiveDialog());
        /* Setting the text field for numbers of teams and of layers. */
        textFieldNumberOfTeams = new TextField();
        textFieldNumberOfTeams.setPromptText("Nombre d'équipes");
        textFieldNumberOfPlayers = new TextField();
        textFieldNumberOfPlayers.setPromptText("Nombre de joueurs par équipe");
        textAreaSportDescription = new TextArea();
        textAreaSportDescription.setPromptText("Description");
        buttonAddPosition = new Button("+");
        buttonAddPosition.setOnAction(event -> addPosition());
        buttonRemovePosition = new Button("-");
        buttonRemovePosition.setOnAction(event -> removePosition());
        rolesList = FXCollections.observableArrayList();
        listViewRoles = new ListView<>(rolesList);

        listViewRoles.setCellFactory(new Callback<ListView<Player>, ListCell<Player>>() {


            @Override
            public ListCell<Player> call(ListView<Player> p) {
                ListCell<Player> cell = new ListCell<Player>(){
                    @Override
                    protected void updateItem(Player item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty){
                            setText(null);
                            setGraphic(null);
                        }else if (item != null) {
                            setText(item.getsName());
                        }
                    }
                };
                return cell;
            }

        });
        listViewRoles.setEditable(true);
        listViewRoles.setItems(rolesList);
        imageFieldTypeHockey = new ImageView(new Image("drawable/hockey_small.png"));
        imageFieldTypeFootball = new ImageView(new Image("drawable/football_small.png"));
        imageFieldTypeBasketball = new ImageView(new Image("drawable/basketball_small.png"));
        toggleGroupFieldType = new ToggleGroup();
        radioButtonFieldTypeHockey = new RadioButton();
        radioButtonFieldTypeHockey.setToggleGroup(toggleGroupFieldType);
        radioButtonFieldTypeFootball = new RadioButton();
        radioButtonFieldTypeFootball.setToggleGroup(toggleGroupFieldType);
        radioButtonFieldTypeBasketball = new RadioButton();
        radioButtonFieldTypeBasketball.setToggleGroup(toggleGroupFieldType);
        radioButtonFieldTypeCustom = new RadioButton();
        radioButtonFieldTypeCustom.setToggleGroup(toggleGroupFieldType);
        textFieldFieldUnits = new TextField();
        textFieldFieldUnits.setPromptText("Unités");
        textFieldFieldLength = new TextField();
        textFieldFieldLength.setPromptText("Longueur");
        textFieldFieldWidth = new TextField();
        textFieldFieldWidth.setPromptText("Largeur");

        /* Field image widget settings. */
        /* The image view. */
        fieldImageView = new ImageView();
        fieldImageView.setFitHeight(100);
        fieldImageView.setFitWidth(100);
        fieldImageView.setPreserveRatio(true);

        /*The button.*/
        buttonImportFieldImage = new Button("Importer une image...");
        buttonImportFieldImage.setOnMouseClicked(event -> chooseFieldImage());

        /* The file chooser. */
        fileChooserFieldImage = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooserFieldImage.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        // When a radio button is selected, the field image view has to become null. Otherwise, they could be two
        // images selected at the same time.
        //radioButtonFieldTypeHockey.setOnAction(actionEvent -> fieldImageView = null);
        //radioButtonFieldTypeFootball.setOnAction(actionEvent -> fieldImageView = null);
        //radioButtonFieldTypeBasketball.setOnAction(actionEvent -> fieldImageView = null);

        // Adding the dialog widgets to the grid panes :
        // Left pane
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        column1.setPercentWidth(20);
        column2.setPercentWidth(80);
        gridPaneLeft.getColumnConstraints().addAll(column1, column2);
        gridPaneLeft.add(new Label("Nom du sport:"), 0, 0, 2, 1);
        gridPaneLeft.add(textFieldSportName, 0, 1, 3, 1);
        gridPaneLeft.add(new Label("Type d'objectif:"), 0, 2, 2, 1);
        gridPaneLeft.add(comboBoxObjective, 0, 3, 1, 1);
        gridPaneLeft.add(addNewGameObjective, 1, 3, 1, 1);
        gridPaneLeft.add(new Label("Description:"), 0, 4, 3, 1);
        gridPaneLeft.add(textAreaSportDescription, 0, 5, 3, 1);

          /* Adding the numbers of teams and of players. */
        gridPaneLeft.add(new Label("Nombre d'équipes:"), 0, 6, 2, 1);
        gridPaneLeft.add(textFieldNumberOfTeams, 0, 7, 3, 1);
        gridPaneLeft.add(new Label("Nombre de joueurs par équipe: (Laisser ce champ vide pour desactiver cette option)"), 0, 8, 2, 1);
        gridPaneLeft.add(textFieldNumberOfPlayers, 0, 9, 3, 1);

        gridPaneLeft.add(new Label("Position(s):"), 0, 10, 2, 1);
        gridPaneLeft.add(buttonAddPosition, 0, 12, 1, 1);
        gridPaneLeft.add(buttonRemovePosition, 1, 12, 1, 1);
        gridPaneLeft.add(listViewRoles, 0, 11, 3, 1);

        // Right Pane
        column1 = new ColumnConstraints();
        column2 = new ColumnConstraints();
        ColumnConstraints column3 = new ColumnConstraints();
        column1.setPercentWidth(33.33333333);
        column2.setPercentWidth(33.33333333);
        column3.setPercentWidth(33.33333333);
        gridPaneRight.getColumnConstraints().addAll(column1, column2, column3);
        gridPaneRight.add(new Label("Type de terrain:"), 0, 0, 3, 1);
        gridPaneRight.add(imageFieldTypeHockey, 0, 1, 1, 1);
        gridPaneRight.add(imageFieldTypeFootball, 1, 1, 1, 1);
        gridPaneRight.add(imageFieldTypeBasketball, 2, 1, 1, 1);
        gridPaneRight.add(radioButtonFieldTypeHockey, 0, 2, 1, 1);
        gridPaneRight.add(radioButtonFieldTypeFootball, 1, 2, 1, 1);
        gridPaneRight.add(radioButtonFieldTypeBasketball, 2, 2, 1, 1);

        // Adding the image view and image importation button.
        gridPaneRight.add(new Label("Importer une image:"), 1 ,3, 3, 1);
        gridPaneRight.add(buttonImportFieldImage, 1, 4, 1, 1);
        gridPaneRight.add(radioButtonFieldTypeCustom, 2, 4, 1, 1);
        gridPaneRight.add(fieldImageView, 1, 5, 1, 1);

        // Adding the dimension fields.
        gridPaneRight.add(new Label("Dimensions du terrain:"), 1, 11, 1, 1);
        gridPaneRight.add(textFieldFieldLength, 1, 12, 1, 1);
        gridPaneRight.add(textFieldFieldWidth, 1, 13, 1, 1);
        gridPaneRight.add(textFieldFieldUnits, 1, 14, 1, 1);

        // Adding the left and right grid panes to the split pane.
        splitPane.getItems().add(gridPaneLeft);
        splitPane.getItems().add(gridPaneRight);
        // Setting the split pane as content for the Dialog.
        getDialogPane().setContent(splitPane);
    }

    public NewSportDialog(FrontController frontController,
                          ObservableList<GameObjective> gameObjectiveObservableArray,
                          String newSportName,
                          Image newSportThumbnail,
                          GameObjective newGameObjective,
                          String newSportDescription,
                          int newNumberOfTeam,
                          int newNumberofPlayersPerTeam,
                          List<Player> newPlayerTypes,
                          Image newGameFieldImage,
                          String newGameFieldImagePath,
                          Integer newGameFieldLength,
                          Integer newGameFieldWidth,
                          String newGameFieldUnits) {
        // Set the dialog style.
        initStyle(StageStyle.UTILITY);
        // Set the button types.
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        // Set Title
        setTitle("VisuaLigue - Nouveau Sport");
        // Instanciating the panes to place the widgets.
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        GridPane gridPaneLeft = new GridPane();
        GridPane gridPaneRight = new GridPane();
        gridPaneLeft.setHgap(10);
        gridPaneLeft.setVgap(10);
        gridPaneLeft.setPadding(new Insets(10, 10, 10, 10));
        gridPaneLeft.setPrefWidth(350);
        gridPaneRight.setHgap(10);
        gridPaneRight.setVgap(10);
        gridPaneRight.setPadding(new Insets(10, 10, 10, 10));
        gridPaneRight.setPrefWidth(650);
        splitPane.setPrefWidth(1000);
        splitPane.setPrefHeight(500);
        splitPane.setPadding(new Insets(10, 10, 10, 10));

        // Initialize the dialog widgets.
        textFieldSportName = new TextField();
        textFieldSportName.setPromptText("Nom du sport");
        if (newSportName != null)
            textFieldSportName.setText(newSportName);
        comboBoxObjective = new ComboBox<>(gameObjectiveObservableArray);
        comboBoxObjective.setPrefWidth(150);
        comboBoxObjective.setCellFactory(new Callback<ListView<GameObjective>,ListCell<GameObjective>>(){

            @Override
            public ListCell<GameObjective> call(ListView<GameObjective> p) {
                final ListCell<GameObjective> cell = new ListCell<GameObjective>(){
                    @Override
                    protected void updateItem(GameObjective gameObjective, boolean bln) {
                        super.updateItem(gameObjective, bln);
                        if(gameObjective != null){
                            setText(gameObjective.getsName());
                        }else{
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });
        comboBoxObjective.setButtonCell(new ListCell<GameObjective>() {
            @Override
            protected void updateItem(GameObjective item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getsName());
                }
            }
        });
        if (newGameObjective != null) {

            comboBoxObjective.getSelectionModel().select(newGameObjective);
            /*
            ObservableList<GameObjective> list = comboBoxObjective.getItems();
            for (int i = 0; i < list.size() ; i++) {
                if (list.get(i).getsName() == newGameObjective.getsName()) {
                    comboBoxObjective.getSelectionModel().select(i);
                }
            }*/
        }
        else
           comboBoxObjective.getSelectionModel().selectFirst();

        comboBoxObjective.setPromptText("Type d'objectif");
        addNewGameObjective = new Button(" + ");
        addNewGameObjective.setOnAction(event -> frontController.showNewGameObjectiveDialog());
        /* Setting the text field for numbers of teams and of layers. */
        textFieldNumberOfTeams = new TextField();
        textFieldNumberOfTeams.setPromptText("Nombre d'équipes");
        if (newNumberOfTeam != (-1)) {
            textFieldNumberOfTeams.setText(Integer.toString(newNumberOfTeam));
        }
        textFieldNumberOfPlayers = new TextField();
        textFieldNumberOfPlayers.setPromptText("Nombre de joueurs par équipe");
        if (newNumberofPlayersPerTeam != (-1)) {
            textFieldNumberOfPlayers.setText(Integer.toString(newNumberofPlayersPerTeam));
        }
        textAreaSportDescription = new TextArea();
        textAreaSportDescription.setPromptText("Description");
        if (newSportDescription != null) {
            textAreaSportDescription.setText(newSportDescription);
        }
        buttonAddPosition = new Button("+");
        buttonAddPosition.setOnAction(event -> addPosition());
        buttonRemovePosition = new Button("-");
        buttonRemovePosition.setOnAction(event -> removePosition());
        rolesList = newPlayerTypes != null ? FXCollections.observableArrayList(newPlayerTypes) : FXCollections.observableArrayList();
        listViewRoles = new ListView<>(rolesList);

        listViewRoles.setCellFactory(new Callback<ListView<Player>, ListCell<Player>>() {


            @Override
            public ListCell<Player> call(ListView<Player> p) {
                ListCell<Player> cell = new ListCell<Player>(){
                    @Override
                    protected void updateItem(Player item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty){
                            setText(null);
                            setGraphic(null);
                        }else if (item != null) {
                            setText(item.getsName());
                        }
                    }
                };
                return cell;
            }

        });
        listViewRoles.setEditable(true);
        listViewRoles.setItems(rolesList);
        imageFieldTypeHockey = new ImageView(new Image("drawable/hockey_small.png"));
        imageFieldTypeFootball = new ImageView(new Image("drawable/football_small.png"));
        imageFieldTypeBasketball = new ImageView(new Image("drawable/basketball_small.png"));
        toggleGroupFieldType = new ToggleGroup();
        radioButtonFieldTypeHockey = new RadioButton();
        radioButtonFieldTypeHockey.setToggleGroup(toggleGroupFieldType);
        radioButtonFieldTypeFootball = new RadioButton();
        radioButtonFieldTypeFootball.setToggleGroup(toggleGroupFieldType);
        radioButtonFieldTypeBasketball = new RadioButton();
        radioButtonFieldTypeBasketball.setToggleGroup(toggleGroupFieldType);
        radioButtonFieldTypeCustom = new RadioButton();
        radioButtonFieldTypeCustom.setToggleGroup(toggleGroupFieldType);
        //toggleGroupFieldType.selectToggle();
        //System.out.println();
        textFieldFieldUnits = new TextField();
        textFieldFieldUnits.setPromptText("Unités");
        textFieldFieldUnits.setText(newGameFieldUnits != null ? newGameFieldUnits : "");
        textFieldFieldLength = new TextField();
        textFieldFieldLength.setPromptText("Longueur");
        textFieldFieldLength.setText(newGameFieldLength != null ? Integer.toString(newGameFieldLength) : "");
        textFieldFieldWidth = new TextField();
        textFieldFieldWidth.setPromptText("Largeur");
        textFieldFieldWidth.setText(newGameFieldWidth != null ? Integer.toString(newGameFieldWidth) : "");

        /* Field image widget settings. */
        /* The image view. */
        fieldImageView = new ImageView();
        fieldImageView.setFitHeight(100);
        fieldImageView.setFitWidth(100);
        fieldImageView.setPreserveRatio(true);

        /*The button.*/
        buttonImportFieldImage = new Button("Importer une image...");
        buttonImportFieldImage.setOnMouseClicked(event -> chooseFieldImage());

        /* The file chooser. */
        fileChooserFieldImage = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooserFieldImage.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        // When a radio button is selected, the field image view has to become null. Otherwise, they could be two
        // images selected at the same time.
        //radioButtonFieldTypeHockey.setOnAction(actionEvent -> fieldImageView = null);
        //radioButtonFieldTypeFootball.setOnAction(actionEvent -> fieldImageView = null);
        //radioButtonFieldTypeBasketball.setOnAction(actionEvent -> fieldImageView = null);

        // Adding the dialog widgets to the grid panes :
        // Left pane
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        column1.setPercentWidth(20);
        column2.setPercentWidth(80);
        gridPaneLeft.getColumnConstraints().addAll(column1, column2);
        gridPaneLeft.add(new Label("Nom du sport:"), 0, 0, 2, 1);
        gridPaneLeft.add(textFieldSportName, 0, 1, 3, 1);
        gridPaneLeft.add(new Label("Type d'objectif:"), 0, 2, 2, 1);
        gridPaneLeft.add(comboBoxObjective, 0, 3, 1, 1);
        gridPaneLeft.add(addNewGameObjective, 1, 3, 1, 1);
        gridPaneLeft.add(new Label("Description:"), 0, 4, 3, 1);
        gridPaneLeft.add(textAreaSportDescription, 0, 5, 3, 1);

          /* Adding the numbers of teams and of players. */
        gridPaneLeft.add(new Label("Nombre d'équipes:"), 0, 6, 2, 1);
        gridPaneLeft.add(textFieldNumberOfTeams, 0, 7, 3, 1);
        gridPaneLeft.add(new Label("Nombre de joueurs par équipe: (Laisser ce champ vide pour desactiver cette option)"), 0, 8, 2, 1);
        gridPaneLeft.add(textFieldNumberOfPlayers, 0, 9, 3, 1);

        gridPaneLeft.add(new Label("Position(s):"), 0, 10, 2, 1);
        gridPaneLeft.add(buttonAddPosition, 0, 12, 1, 1);
        gridPaneLeft.add(buttonRemovePosition, 1, 12, 1, 1);
        gridPaneLeft.add(listViewRoles, 0, 11, 3, 1);

        // Right Pane
        column1 = new ColumnConstraints();
        column2 = new ColumnConstraints();
        ColumnConstraints column3 = new ColumnConstraints();
        column1.setPercentWidth(33.33333333);
        column2.setPercentWidth(33.33333333);
        column3.setPercentWidth(33.33333333);
        gridPaneRight.getColumnConstraints().addAll(column1, column2, column3);
        gridPaneRight.add(new Label("Type de terrain:"), 0, 0, 3, 1);
        gridPaneRight.add(imageFieldTypeHockey, 0, 1, 1, 1);
        gridPaneRight.add(imageFieldTypeFootball, 1, 1, 1, 1);
        gridPaneRight.add(imageFieldTypeBasketball, 2, 1, 1, 1);
        gridPaneRight.add(radioButtonFieldTypeHockey, 0, 2, 1, 1);
        gridPaneRight.add(radioButtonFieldTypeFootball, 1, 2, 1, 1);
        gridPaneRight.add(radioButtonFieldTypeBasketball, 2, 2, 1, 1);

        // Adding the image view and image importation button.
        gridPaneRight.add(new Label("Importer une image:"), 1 ,3, 3, 1);
        gridPaneRight.add(buttonImportFieldImage, 1, 4, 1, 1);
        gridPaneRight.add(radioButtonFieldTypeCustom, 2, 4, 1, 1);
        gridPaneRight.add(fieldImageView, 1, 5, 1, 1);

        // Adding the dimension fields.
        gridPaneRight.add(new Label("Dimensions du terrain:"), 1, 11, 1, 1);
        gridPaneRight.add(textFieldFieldLength, 1, 12, 1, 1);
        gridPaneRight.add(textFieldFieldWidth, 1, 13, 1, 1);
        gridPaneRight.add(textFieldFieldUnits, 1, 14, 1, 1);

        // Adding the left and right grid panes to the split pane.
        splitPane.getItems().add(gridPaneLeft);
        splitPane.getItems().add(gridPaneRight);
        // Setting the split pane as content for the Dialog.
        getDialogPane().setContent(splitPane);
    }
    /* - Public methods - */

    private void addPosition() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Ajouter position");
        dialog.setHeaderText("Entrez le nom de la nouvelle position");
        dialog.setContentText("Nom de la position: ");
        Optional<String> result = dialog.showAndWait();

        // Check if there is a new position to add.
        if (result.isPresent())
        {
            // The result must not be empty to be add.
            if (!result.get().isEmpty())
            {
                // Adding the role.
                rolesList.addAll(new Player(String.valueOf(result.get()), new Image("drawable/blue_circle.png"), "drawable/blue_circle.png", null));
            }
            // If the result is empty, the user has to be inform of his mistake.
            else
            {
                // Creating the information's alert.
                Alert emptyRole = new Alert(Alert.AlertType.INFORMATION);

                // Setting title, header text and text.
                emptyRole.setTitle("Rôle vide");
                emptyRole.setHeaderText("Le rôle n'est pas valide!");
                emptyRole.setContentText("Oups, il semblerait que vous essayez de créer un rôle vide.\nUne telle " +
                        "action est impossible, car elle n'a pas de sens.\nVeuillez recommencer en incluant un " +
                        "nom à votre rôle cette fois.");

                // Showing the alert and waiting the user's confirmation.
                emptyRole.showAndWait();
            }
        }
    }

    private void removePosition() {
        rolesList.remove(listViewRoles.getSelectionModel().getSelectedItem());
    }

    /**
     * Returns the name entered by the user for the new sport.
     * @return String : Name of the new sport.
     */
    public String getSportName() {
        return textFieldSportName.getText();
    }

    /**
     * Returns the GameObjective type chosen by the user in the combo box.
     * @return GameObjective : Type of game objective for the sport.
     */
    public GameObjective getGameObjective() {
        return comboBoxObjective.getValue();
    }

    /**
     * Returns the sport description entered by the user in the text area.
     * @return String : Description of the new sport.
     */
    public String getSportDescription() {
        return textAreaSportDescription.getText();
    }

    /**
     * Add the game playerTypes entered by the user in the playerTypes list.
     * @return List<String> : Game playerTypes for the new sport.
     */
    public List<Player> getGamePositions() {
        return listViewRoles.getItems();
    }

    /**
     * Set the selected image as the new gamefield image using the radio buttons toggle group.
     * @return Image : Image selected by the user using the toggle group.
     */
    public Image getGameFieldImage()
    {
        // Creation of the return image.
        Image image = null;

        // Cas where the image was imported by the user.
        if (fieldImageView.getImage() != null)
        {
            image = fieldImageView.getImage();
        }
        // Case where the radio button selected is the hockey one.
        else if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeHockey)
        {
            image = new Image("drawable/hockey.png");
        }
        // Case where the radio button selected is the football one.
        else if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeFootball)
        {
            image = new Image("drawable/football.png");
        }
        // Case where the radio button selected is the basketball one.
        else if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeBasketball)
        {
            image = new Image("drawable/basketball.png");
        }

        // Return.
        return image;
    }


    public String getGameFieldImagePath()
    {
        // Creation of the return image.
        String image = null;

        // Case where the image was imported by the user.
        if (fieldImageView.getImage() != null)
        {
            image = "file:///" + fieldFile.getAbsolutePath();
        }
        // Case where the radio button selected is the hockey one.
        else if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeHockey)
        {
            image = "drawable/hockey.png";
        }
        // Case where the radio button selected is the football one.
        else if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeFootball)
        {
            image = "drawable/football.png";
        }
        // Case where the radio button selected is the basketball one.
        else if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeBasketball)
        {
            image = "drawable/basketball.png";
        }

        // Return.
        return image;
    }

    /**
     * Set the selected image as the new sport image using the radio buttons toggle group.
     * @return Image : Image selected by the user using the toggle group.
     */
    public Image getSportThumbnail()
    {
        Image image = null;
        if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeHockey)
        {
            image = new Image("drawable/hockey_small.png");
        }
        else if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeFootball)
        {
            image = new Image("drawable/football_small.png");
        }
        else if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeBasketball)
        {
            image = new Image("drawable/basketball_small.png");
        }
        else if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeCustom)
        {
            image = fieldImageView.getImage();
        }
        return image;
    }

    public String getSportThumbnailImagePath()
    {
        String image = "drawable/hockey_small.png";
        if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeFootball) {
            image = "drawable/football_small.png";
        } else if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeBasketball) {
            image = "drawable/basketball_small.png";
        }else if (toggleGroupFieldType.getSelectedToggle() == radioButtonFieldTypeCustom){
            image = this.fieldFile.getAbsolutePath();
        }
        return image;
    }

    /**
     * Returns the length of the game field entered by the user.
     * @return Integer : Length of the game field.
     */
    public Integer getFieldLength()
    {
        // Get the length.
        String sLength = textFieldFieldLength.getText();

        // Return variable.
        Integer iLength = 0;

        // Case where sLength isn't empty.
        if (!sLength.isEmpty())
        {
            // Try conversion.
            try
            {
                iLength = Integer.parseInt(sLength);
            }
            // Exception thrown by parseInt().
            catch (NumberFormatException e)
            {
                // Exception do nothing because iLength is already initialize to 0.
            }

        }

        // Return.
        return iLength;
    }

    /**
     * Returns the width of the game field entered by the user.
     * @return Integer : Width of the game field.
     */
    public Integer getFieldWidth()
    {
        // Get the length.
        String sWidth = textFieldFieldWidth.getText();

        // Return variable.
        Integer iWidth = 0;

        // Case where sLength isn't empty.
        if (!sWidth.isEmpty())
        {
            // Try conversion.
            try
            {
                iWidth = Integer.parseInt(sWidth);
            }
            // Exception thrown by parseInt().
            catch (NumberFormatException e)
            {
                // Exception do nothing because iWidth is already initialize to 0.
            }
        }

        // Return.
        return iWidth;
    }

    /**
     * Returns the type of units chosen by the user.
     * @return String : Type of units entered by entered.
     */
    public String getFieldsUnits() {
        return textFieldFieldUnits.getText();
    }

    /**
     * This method is used to choose a file which will correspond to the field's image.
     */
    public void chooseFieldImage()
    {

        // Opening the dialog which will allow the user to choose the desired image for his field.
        this.fieldFile = fileChooserFieldImage.showOpenDialog(getOwner());
        try
        {
            // Trying to reading the file.
            BufferedImage bufferedImage = ImageIO.read(this.fieldFile);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);

            // If the image was read, the preview will be set to this image.
            fieldImageView.setImage(image);

            // The radio button have to be deselected.
            toggleGroupFieldType.selectToggle(radioButtonFieldTypeCustom);
        }
        catch (Exception ex)
        {
            // In case of any exceptions during the choice, the image will be null.
            fieldImageView.setImage(null);
            this.fieldFile = null;
        }
    }

    /**
     * This method is the getter of the number of teams.
     * @return An int representing the number of teams is returned.
     */
    public int getTextFieldNumberOfTeams()
    {
        // Creating the return value.
        int returnValue;
        returnValue = -1;

        // Trying to parse the text field.
        try
        {
            returnValue = Integer.parseInt(textFieldNumberOfTeams.getText());

            // If the parse worked, we determine if the number is strictly superior to zero.
            if (returnValue < 1)
            {
                // There is an error.
                returnValue = -1;
            }
            // The maximal number of teams is 5.
            else if (returnValue > 5)
            {
                // Adjusting the number.
                returnValue = -1;
            }
        }
        catch (NumberFormatException e)
        {
            // Doesn't do nothing because returnValue was initialized at -1.
        }

        // Return.
        return returnValue;
    }

    /**
     * This method is the getter of the number of players.
     * @return An int representing the number of players is returned.
     */
    public int getTextFieldNumberOfPlayers()
    {
        // Creating the return value.
        int returnValue;
        returnValue = -1;

        // Trying to parse the text field.
        try
        {
            returnValue = Integer.parseInt(textFieldNumberOfPlayers.getText());

            // If the parse worked, we determine if the number is strictly superior to zero.
            if (returnValue < 1)
            {
                // There is an error.
                returnValue = -1;
            }
            // The maximal number of players is 25.
            else if (returnValue > 25)
            {
                // Adjust the number to throw error.
                returnValue = -1;
            }

        }
        catch (NumberFormatException e)
        {
            // Because this field is optionnal, if the text is empty, then we assume that the number is maximal.
            if (textFieldNumberOfPlayers.getText().isEmpty())
            {
                // Adjust the number.
                returnValue = 25;
            }
        }

        // Return.
        return returnValue;
    }
    /* - Private Methods - */

}
