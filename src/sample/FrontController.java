package sample;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.util.Callback;
import javafx.util.Pair;
import org.omg.CORBA.FREE_MEM;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

// Types of View modes to watch user-made strategies.
enum ViewMode {
    Realtime, FrameByFrame
}

public class FrontController implements Initializable {

    private final double MAX_SCALE = 3.0; // Maximum scale for canvas.
    private final double MIN_SCALE = 0.5; // Minimum scale for canvas.

    /* - Attributes - */
    BackEnd backEnd;
    Dragboard dragboard;
    Scene scene;
    ObservableList<Play> playListContent;
    ObservableList<DraggableObject> othersListContent;
    ViewMode viewMode = ViewMode.FrameByFrame;

    boolean showPlayerInformations = true;
    boolean canvasGrabbed = false;
    private DoubleProperty canvasScale = new SimpleDoubleProperty(1.0); // How much is the canvas scalled.
    private int iGlobalCursor = 0;
    private String sGlobalmode = "F";

    // Private variable which indicates if the app is currently playing a play.
    private boolean isPlaying = false;

    // Private variable needed by stop button.
    private boolean threadStop = false;

    // Private variable need by paused button.
    private boolean wasPaused = false;
    private long imagePausedOn = 0;

    // Private variable which determine how long the thread for the playback sleeps.
    private long sleepingTime = 1000;


    // Counter for text update.
    private int nbSleeps = 0;
    private int nbMinutes = 0;
    private int nbSecondes = 0;

    // Mapping for the mode.
    private HashMap<String, String> modeMap = new HashMap<>();

    /* - Interface widgets - */
    @FXML
    private GridPane gridPane;
    // Menu bar
    @FXML
    private MenuBar menubarMenu;
    // Files menu
    @FXML
    private MenuItem menuNewPlay;
    @FXML
    private MenuItem menuNewSport;
    @FXML
    private MenuItem menuNewGameObjective;
    @FXML
    private MenuItem menuNewObstacle;
    @FXML
    private MenuItem menuSave;
    @FXML
    private MenuItem menuSaveUnder;
    @FXML
    private MenuItem menuExport;
    @FXML
    private MenuItem menuExportBackend;
    @FXML
    private MenuItem menuImportBackend;
    @FXML
    private MenuItem menuQuit;
    // Edit menu
    @FXML
    private MenuItem menuRedo;
    @FXML
    private MenuItem menuUndo;
    @FXML
    private CheckMenuItem menuShowPlayerInformations;
    // Help menu
    @FXML
    private MenuItem menuDocumentation;
    @FXML
    private MenuItem menuHelp;
    // Left Panes
    @FXML
    private TreeView treeViewPlays;
    @FXML
    private TreeView treeViewOthers;
    // Scroll Pane for canvas
    @FXML
    private ScrollPane scrollPaneForCanvas;

    // Field canvas
    @FXML
    private Canvas canvasField;
    @FXML
    private Label labelGameFieldWidth;
    @FXML
    private Label labelGameFieldHeight;
    // Playback controls
    @FXML
    private ImageView buttonRewind;
    @FXML
    private ImageView buttonSlower;
    @FXML
    private ImageView buttonPlayPause;
    @FXML
    private ImageView buttonPause;
    @FXML
    private ImageView buttonStop;
    @FXML
    private ImageView buttonFaster;
    @FXML
    private ImageView buttonFastForward;
    @FXML
    private CustomTimeLine customTimeLine;
    @FXML
    private Label labelTimeleft;
    @FXML
    private ToggleButton togglebuttonViewMode;

    /* - Constructor - */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Adding first mode.
        modeMap.put("0", "F");
        // Does anyone know what this does?
        Bridge bridge = Bridge.getInstance();

        //checking for a save.
        File dataFolder = new File("./data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        File f;
        if (bridge.importPath == null) {
            f = new File("./data/backend.ser");
            System.out.println("Normal load");
            if (f.exists()) {
                FileManager fileMngr = FileManager.getInstance();
                backEnd = fileMngr.loadData();
            } else {
                backEnd = new BackEnd();
            }
        } else {
            f = new File(bridge.importPath);
            bridge.importPath = null;
            System.out.println("Import load");
            if (f.exists()) {
                FileManager fileMngr = FileManager.getInstance();
                backEnd = fileMngr.loadData(f.getAbsolutePath());
                backEnd.currentPlay.setlGhosts(new ArrayList<>());
            } else {
                backEnd = new BackEnd();
            }
        }
        //if save exist load save

        // Ajusting the canvas size depending on user screen size.
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        canvasField.setWidth(primaryScreenBounds.getWidth() / 2.5);
        canvasField.setHeight(primaryScreenBounds.getHeight() / 2.5);


        playListContent = backEnd.lPlayList;
            /* Sets the action listeners on the interface widgets : */
        // Files menu
        menuNewPlay.setOnAction(event -> showNewPlayDialog(null, null));
        menuNewSport.setOnAction(event -> showNewSportDialog(null, null, null, null, -1, -1, null, null, null, null, null, null));
        menuNewGameObjective.setOnAction(event -> showNewGameObjectiveDialog());
        menuNewObstacle.setOnAction(event -> showNewObstacleDialog());
        menuSave.setOnAction(event -> backEnd.saveSession());
        menuSaveUnder.setOnAction(event -> saveSessionUnder());
        menuExport.setOnAction(event -> exportPlay());
        menuExportBackend.setOnAction(event -> exportBackend());
        menuImportBackend.setOnAction(event -> importBackend());
        menuQuit.setOnAction(event -> backEnd.saveAndExit());
        // Edit menu

        /*
        menuRedo.setOnAction(event -> backEnd.redoAction());
        menuUndo.setOnAction(event -> backEnd.undoAction());
        */

        menuRedo.setOnAction(event -> redoWrapper());
        menuUndo.setOnAction(event -> undoWrapper());
        menuShowPlayerInformations.setOnAction(event -> toggleShowPlayerInformations());
        // Help menu
        menuDocumentation.setOnAction(event -> backEnd.showDocumentation());
        menuHelp.setOnAction(event -> backEnd.showHelp());
        // Side panels
        treeViewPlays.setOnMouseClicked(event -> switchCurrentPlayTo(getPlayListSelection()));
        treeViewOthers.setOnDragDetected(event -> startDrag(event));

        // Canvas
        canvasField.setOnMouseClicked(event -> selectObject(event));
        canvasField.setOnDragOver(event -> canvasDragEventHandler(event));
        canvasField.setOnDragDropped(event -> canvasDropEventHandler(event));
        canvasField.setOnMouseDragged(event -> onDragStartedOnObject(event));
        canvasField.setOnMouseReleased(event -> deselectObjects(event));
        canvasField.setOnMouseEntered(event -> onMouseEntersCanvas());
        canvasField.setOnMouseMoved(event -> onMouseOverCanvas(event));
        canvasField.setOnMouseExited(event -> onMouseExitsCanvas());
        canvasField.setOnScroll(scrollEvent -> onScrollEvent(scrollEvent));

        // Adding the canvasScale bindings of canvas.
        canvasField.scaleXProperty().bind(canvasScale);
        canvasField.scaleYProperty().bind(canvasScale);

        // Media controls
        buttonRewind.setOnMouseClicked(event -> rewindPlayback());
        buttonSlower.setOnMouseClicked(event -> slowPlayback());
        buttonPlayPause.setOnMouseClicked(event -> playPausePlayback());
        buttonPause.setOnMouseClicked(event -> pausePlayback());
        buttonStop.setOnMouseClicked(event -> stopPlayback());
        buttonFaster.setOnMouseClicked(event -> acceleratePlayback());
        buttonFastForward.setOnMouseClicked(event -> fastForwardPlayback());
        togglebuttonViewMode.setOnMouseClicked(event -> toggleViewMode());

        // Initialize the ListViews (Plays and others) :
        initPlayList();
        othersListContent = FXCollections.observableArrayList();
        scene = gridPane.getScene();
        labelTimeleft.setText(customTimeLine.getCurrentFrame() + " / " + customTimeLine.getNumberOfFrames());
        //setting text alignment for canvasField
        canvasField.getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);

        Platform.setImplicitExit(false);
        this.setBridge();
    }

    public void askConfirmationOverideActualSave() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure ?");
        alert.setContentText("You imported a save into visualigue. Do you want to save the current state of the application ? Or do you want to revert back to before the importation ?");

        ButtonType buttonTypeOne = new ButtonType("Save current state");
        ButtonType buttonTypeTwo = new ButtonType("Revert to before importation");
        ButtonType buttonTypeCancel = new ButtonType("Cancel exit");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            System.out.println("OK -> save");
            FileManager file = FileManager.getInstance();
            file.saveData(backEnd);
            System.exit(0);
        } else if (result.get() == buttonTypeTwo) {
            System.out.println("cancel -> exit");
            System.exit(0);
        }
    }

    private void setBridge() {
        Bridge bridge = Bridge.getInstance();
        bridge.fc = this;

    }

    public void startSavingOnExit() {
        this.backEnd.saveSession();
    }


    // Shows a dialog to help user create a new play.
    public void showNewPlayDialog(String newPlayTitle, String newPlayDescription) {
        // Shows a 'NewPlayDialog' to the user.
        NewPlayDialog newPlayDialog = new NewPlayDialog(this, backEnd.lSportList, backEnd.lGameObjective, newPlayTitle, newPlayDescription);

        // Adding an optional return for cancel's detection.
        Optional<ButtonType> cancel = newPlayDialog.showAndWait();

        // Checking for cancel.
        if (!cancel.isPresent() || cancel.get() == ButtonType.CANCEL) {
            return;
        }

        // Gathering the information from the dialog :
        String playTitle = newPlayDialog.getPlayTitle();
        Sport playSport = newPlayDialog.getPlaySport();
        String playDescription = newPlayDialog.getPlayDescription();

        // Check if all fields have been filled in.
        Alert missingFields = new Alert(Alert.AlertType.INFORMATION);
        missingFields.setTitle("Renseignement manquant");
        missingFields.setHeaderText("Vous tentez de créer un jeu sans avoir spécifié une des informations.");
        // Check for title.
        if (playTitle.isEmpty()) {
            missingFields.setContentText("Oups, il semblerait que vous ayez omis de mettre un titre au jeu que vous " +
                    "voulez créer.\nVeuillez recommencer en incluant cet élément.");
            missingFields.showAndWait();
            showNewPlayDialog(playTitle, playDescription);
        }
        // All fields are filled.
        else {
            // Pass information to BackEnd Class.
            addNewPlay(playTitle, playSport, playDescription);
            initPlayList();
        }
    }

    // Add a new play in the backEnd play list.
    private void addNewPlay(String playTitle, Sport playSport, String playDescription) {
        // Add new play to the Plays panel.
        backEnd.createNewPlay(playTitle, playSport, playDescription);
    }

    // TODO ; Ajouter au diagramme de classe
    public String showPlayerCreationDialog() {
        PlayerCreationDialog playerCreationDialog = new PlayerCreationDialog(); //send backend directly ? nah
        Optional<ButtonType> cancel = playerCreationDialog.showAndWait();

        if (!cancel.isPresent() || cancel.get() == ButtonType.CANCEL) {
            return (new String());
        }

        return (new String(playerCreationDialog.getPlayerName()));
    }

    // Shows a dialog to help user create a new sport.
    public void showNewSportDialog(String newSportName,
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


        // Shows a 'NewSportDialog' to the user.
        NewSportDialog newSportDialog = new NewSportDialog(this,
                backEnd.lGameObjective,
                newSportName,
                newSportThumbnail,
                newGameObjective,
                newSportDescription,
                newNumberOfTeam,
                newNumberofPlayersPerTeam,
                newPlayerTypes,
                newGameFieldImage,
                newGameFieldImagePath,
                newGameFieldLength,
                newGameFieldWidth,
                newGameFieldUnits);
        Optional<ButtonType> cancel = newSportDialog.showAndWait();

        // Did the user cancel the creation?
        if (!cancel.isPresent() || cancel.get() == ButtonType.CANCEL) {
            // Return.
            return;
        }

        // Gathering the information from the dialog :
        String sportName = newSportDialog.getSportName();
        Image sportThumbnail = newSportDialog.getSportThumbnail();
        //String sSportThumbnail = newSportDialog.getSportThumbnailImagePath();
        GameObjective gameObjective = newSportDialog.getGameObjective();
        String sportDescription = newSportDialog.getSportDescription();
        int numberOfTeams = newSportDialog.getTextFieldNumberOfTeams();
        int numberOfPlayersPerTeam = newSportDialog.getTextFieldNumberOfPlayers();
        List<Player> playerTypes = newSportDialog.getGamePositions();
        Image gameFieldImage = newSportDialog.getGameFieldImage();
        String sGameFieldImagePath = newSportDialog.getGameFieldImagePath();
        Integer gameFieldLength = newSportDialog.getFieldLength();
        Integer gameFieldWidth = newSportDialog.getFieldWidth();
        String gameFieldUnits = newSportDialog.getFieldsUnits();

        // Check if all fields have been filled in.
        Alert missingFields = new Alert(Alert.AlertType.INFORMATION);
        missingFields.setTitle("Renseignements manquants");
        missingFields.setHeaderText("Vous tentez de créer un sport sans avoir spécifié une des informations.");

        // Check for sport's name, thumbnail and description.
        if (sportName.isEmpty() || sportThumbnail == null) {
            missingFields.setContentText("Oups, il semblerait que vous ayez omis de mettre des informations relatives" +
                    " au sport en lui-même.\nVous devez savoir que, pour créer un sport, vous devez obligatoirement" +
                    " lui donner un titre et une image.\nVeuillez donc recommencer en vous assurant " +
                    "d'inclure ces éléments.");
            missingFields.showAndWait();

            // Return.
            //return;
            showNewSportDialog(sportName,
                    sportThumbnail,
                    gameObjective,
                    sportDescription,
                    numberOfTeams,
                    numberOfPlayersPerTeam,
                    playerTypes,
                    gameFieldImage,
                    sGameFieldImagePath,
                    gameFieldLength,
                    gameFieldWidth,
                    gameFieldUnits);
        }
        // Check if there is at least one team and one player per team.
        if (numberOfTeams < 1 || numberOfPlayersPerTeam < 1) {
            missingFields.setContentText("Oups, il semblerait que vous ayez omis de mettre des informations relatives" +
                    " au sport en lui-même.\nVous devez savoir que, pour créer un sport, vous devez obligatoirement" +
                    " mettre au minimum une équipe et un joueur par équipe.\nIl est à noter qu'un champ vide pour " +
                    "le nombre de joueurs est considéré comme le nombre maximal, c'est-à-dire 25.\nIl est aussi à not" +
                    "er que le nombre maximal d'équipe est 5.\nVeuillez donc recommencer en vous assurant d'inclure " +
                    "ou de corriger ces éléments.");
            missingFields.showAndWait();

            System.out.println("numberOfteams bad");
            // Return.
            //return;
            showNewSportDialog(sportName,
                    sportThumbnail,
                    gameObjective,
                    sportDescription,
                    numberOfTeams,
                    numberOfPlayersPerTeam,
                    playerTypes,
                    gameFieldImage,
                    sGameFieldImagePath,
                    gameFieldLength,
                    gameFieldWidth,
                    gameFieldUnits);
        }
        // Check for game field's image, length, width and units.
        if (gameFieldImage == null || gameFieldLength == 0 || gameFieldWidth == 0 || gameFieldUnits.isEmpty()) {
            missingFields.setContentText("Oups, il semblerait que vous ayez omis de rentrer les informations concern" +
                    "ant le terrain sur lequel se déroule le sport à créer.\nVous devez savoir que, pour créer le " +
                    "terrain, il lui faut une image, une largeur, une longueur et une unité de mesure.\nVeuillez " +
                    "recommencer en n'oubliant pas d'inclure ces éléments.");
            missingFields.showAndWait();

            // Return.
            //return;
            showNewSportDialog(sportName,
                    sportThumbnail,
                    gameObjective,
                    sportDescription,
                    numberOfTeams,
                    numberOfPlayersPerTeam,
                    playerTypes,
                    gameFieldImage,
                    sGameFieldImagePath,
                    gameFieldLength,
                    gameFieldWidth,
                    gameFieldUnits);
        }
        // All fields are filled.
        else {
            // Add new sport available to construct plays.
            //addNewSport(sportName, sportThumbnail, gameObjective, sportDescription, playerTypes, gameFieldImage, sGameFiledImagePath,
            addNewSport(sportName, sportThumbnail, gameObjective, sportDescription, playerTypes, numberOfTeams, numberOfPlayersPerTeam, gameFieldImage, sGameFieldImagePath,
                    gameFieldLength, gameFieldWidth, gameFieldUnits);
        }
    }

    // Add a new sport in the backEnd sport list.
    public void addNewSport(String name,
                            Image thumbnail,
                            GameObjective objective,
                            String description,
                            List<Player> playerTypes,
                            int numberOfTeams,
                            int numberOfPlayersPerTeam,
                            Image gameFieldImage,
                            String sGameFiledImagePath,
                            Integer gameFieldLength,
                            Integer gameFieldWidth,
                            String gameFieldUnits) {
        // Send informations to backEnd.
        backEnd.createNewSport(name,
                thumbnail,
                objective,
                description,
                playerTypes,
                numberOfTeams,
                numberOfPlayersPerTeam,
                gameFieldImage,
                sGameFiledImagePath,
                gameFieldLength,
                gameFieldWidth,
                gameFieldUnits);
        initPlayList();
    }

    @FXML
    public void saveAsPng() {

        this.backEnd.setItemsNewPos(0);
        this.drawCurrentPlayOnCanvas();

        GraphicsContext gc = canvasField.getGraphicsContext2D();

        gc.save();
        this.drawShapes(gc);

        WritableImage image = this.canvasField.snapshot(new SnapshotParameters(), null);

        initPlayList();
        gc.restore();
        this.backEnd.setItemsNewPos(this.customTimeLine.getCurrentFrame());
        drawCurrentPlayOnCanvas();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image(*.png)", "*.png"));
        File file = fileChooser.showSaveDialog(null);
        if (!file.getName().contains(".")) {
            file = new File(file.getAbsolutePath() + ".png");
        }


        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }


        //Second copy of the img into are asset folder
        File file2 = new File("./data/" + java.util.UUID.randomUUID() + ".png");
        backEnd.currentPlay.setPlayThumbnail(image, file2.getAbsolutePath());
        if (file2 != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file2);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

    private void drawShapes(GraphicsContext gc) {

        ArrayList<Player> lPlayers = backEnd.currentPlay.getlPlayers();
        ArrayList<GameObjective> lGameObjectives = backEnd.currentPlay.getlGameObjectives();
        ArrayList<Obstacle> lObstacles = backEnd.currentPlay.getlObstacles();

        List<Triple> lItemPos = null;
        double x1 = -1;
        double x2 = -1;
        double y1 = -1;
        double y2 = -1;


        gc.setFill(Color.BLACK);
        gc.setLineWidth(2);

        for (Player player : lPlayers) {
            lItemPos = player.getlPositions();

            if (player.getsImagePath().equals("drawable/blue_circle.png")) {
                gc.setFill(Color.BLUE);
                gc.setStroke(Color.BLUE);
            }
            if (player.getsImagePath().equals("drawable/green_circle.png")) {
                gc.setFill(Color.LIGHTGREEN);
                gc.setStroke(Color.LIGHTGREEN);
            }
            if (player.getsImagePath().equals("drawable/orange_circle.png")) {
                gc.setFill(Color.ORANGE);
                gc.setStroke(Color.ORANGE);
            }
            if (player.getsImagePath().equals("drawable/pink_circle.png")) {
                gc.setFill(Color.PINK);
                gc.setStroke(Color.PINK);
            }
            if (player.getsImagePath().equals("drawable/red_circle.png")) {
                gc.setFill(Color.RED);
                gc.setStroke(Color.RED);
            }


            for (int i = 0; i < (lItemPos.size() - 1); i++) {
                x1 = lItemPos.get(i).x;
                y1 = lItemPos.get(i).y;
                x2 = lItemPos.get(i + 1).x;
                y2 = lItemPos.get(i + 1).y;
                this.drawArrow(gc, x1, y1, x2, y2);
            }
        }

        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);

        for (GameObjective gameObjective : lGameObjectives) {
            lItemPos = gameObjective.getlPositions();
            for (int i = 0; i < (lItemPos.size() - 1); i++) {
                x1 = lItemPos.get(i).x;
                y1 = lItemPos.get(i).y;
                x2 = lItemPos.get(i + 1).x;
                y2 = lItemPos.get(i + 1).y;
                this.drawArrow(gc, x1, y1, x2, y2);
            }
        }

        for (Obstacle obstacle : lObstacles) {
            lItemPos = obstacle.getlPositions();
            for (int i = 0; i < (lItemPos.size() - 1); i++) {
                x1 = lItemPos.get(i).x;
                y1 = lItemPos.get(i).y;
                x2 = lItemPos.get(i + 1).x;
                y2 = lItemPos.get(i + 1).y;
                this.drawArrow(gc, x1, y1, x2, y2);
            }
        }
    }

    void drawArrow(GraphicsContext gc, double x1, double y1, double x2, double y2) {


        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);

        Transform transform = Transform.translate(x1, y1);
        transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
        gc.setTransform(new Affine(transform));

        gc.strokeLine(0, 0, len, 0);
        gc.fillPolygon(new double[]{len, len - 8, len - 8, len}, new double[]{0, -8, 8, 0},
                4);
    }

    public void showNewGameObjectiveDialog() {
        NewGameObjectiveDialog newGameObjectiveDialog = new NewGameObjectiveDialog();
        Optional<ButtonType> cancel = newGameObjectiveDialog.showAndWait();
        // Verifying the cancel.
        if (!cancel.isPresent() || cancel.get() == ButtonType.CANCEL) {
            return;
        }

        String name = newGameObjectiveDialog.getGameObjectiveName();
        Image image = newGameObjectiveDialog.getGameObjectiveImage();
        String imagePath = newGameObjectiveDialog.getGameObjectiveImagePath();
        double scale = newGameObjectiveDialog.getGameObjectiveScale(); // TODO : Ajouter le scale
        // Ajoute le nouvel gameObjective au backEnd.
        addNewGameObjective(name, image, imagePath, scale);
    }

    public void addNewGameObjective(String name, Image image, String imagePath, double scale) {
        backEnd.createNewGameObjective(name, image, imagePath, scale);
        System.out.println("Added new objective (Front)");
        initOthersList();
    }

    // Shows a dialog to help user create a new obstacle.
    public void showNewObstacleDialog() {
        NewObstacleDialog newObstacleDialog = new NewObstacleDialog();

        // Adding a return variable to check if there was a cancel.
        Optional<ButtonType> cancel = newObstacleDialog.showAndWait();

        // Verifying the cancel.
        if (!cancel.isPresent() || cancel.get() == ButtonType.CANCEL) {
            return;
        }

        // Gathering information from the dialog :
        String obstacleName = newObstacleDialog.getObstacleName();
        Image obstacleImage = newObstacleDialog.getObstacleImage();
        String sObstacleImagePath = newObstacleDialog.getObstacleImagePath();
        double scale = newObstacleDialog.getObstacleScale(); // TODO : ajouter le scale

        // Verify if the name and image are present.
        Alert missingFields = new Alert(Alert.AlertType.INFORMATION);
        missingFields.setTitle("Renseignement manquant");
        missingFields.setHeaderText("Erreur : il manque un ou plusieurs champs pour permettre la création de " +
                "l'obstacle.");

        // Checking if name and image are correct.
        if (obstacleName.isEmpty() && obstacleImage == null) {
            // Ajusting title.
            missingFields.setTitle("Renseignements manquants");

            // Giving more information to user.
            missingFields.setContentText("Oups, il semblerait que vous essayez de créer un obstacle sans donner les" +
                    " bonnes informations.\nL'application détecte qu'il manque un titre non vide et qu'il faut une " +
                    "image sans erreur.\nVeuillez recommencer en corrigeant ces deux défauts.");

            // Showing and waiting.
            missingFields.showAndWait();
        }
        // Checking if name is correct but the image isn't.
        else if (!obstacleName.isEmpty() && obstacleImage == null) {
            // Giving more information to user.
            missingFields.setContentText("Oups, il semblerait que vous essayez de créer un obstacle sans donner les" +
                    " bonnes informations.\nL'application détecte qu'il faut une image sans erreur pour permettre la " +
                    "création.\nVeuillez recommencer en corrigeant ce défaut.");

            // Showing and waiting.
            missingFields.showAndWait();
        }
        // Checking if name isn't correct but the image is.
        else if (obstacleName.isEmpty() && obstacleImage != null) {
            // Giving more information to user.
            missingFields.setContentText("Oups, il semblerait que vous essayez de créer un obstacle sans donner les" +
                    " bonnes informations.\nL'application détecte qu'il faut un titre non vide pour permettre la " +
                    "création.\nVeuillez recommencer en corrigeant ce défaut.");

            // Showing and waiting.
            missingFields.showAndWait();
        }
        // All fields to fill are ok. Proceed creation.
        else {
            // Add the new obstacle to construct plays.
            addNewObstacle(obstacleName, obstacleImage, sObstacleImagePath, scale);
        }
    }

    // Add a new sport in backEnd obstacle list.
    private void addNewObstacle(String obstacleName, Image obstacleImage, String sObstacleImagePath, double scale) {
        backEnd.createNewObstacle(obstacleName, obstacleImage, sObstacleImagePath, scale);
        initOthersList();
    }

    // Initialise the 'Play' panel, ready to receive new ones from user.
    private void initPlayList() {

        List<Sport> sports = backEnd.lSportList;
        treeViewPlays.setRoot(null);
        TreeItem<Play> root = new TreeItem<Play>(new SportTitle("Jeux"));
        Button deleteButton = new Button("x");

        for (Sport sport : sports) {
            // Adding plays to sport
            TreeItem<Play> sportCategory = new TreeItem<>(new SportTitle(sport.getName()));
            List<Play> plays = backEnd.lPlayList;
            for (int i = 0; i < plays.size(); ++i) {
                if (plays.get(i).getsSport().getName().equals(sport.getName())) {
                    sportCategory.getChildren().add(new TreeItem<Play>(plays.get(i)));
                }
            }
            root.getChildren().add(sportCategory);
        }
        treeViewPlays.setRoot(root);
        treeViewPlays.setCellFactory(new Callback<TreeView<Play>, TreeCell<Play>>() {

            @Override
            public TreeCell<Play> call(TreeView<Play> p) {
                TreeCell<Play> cell = new TreeCell<Play>() {
                    @Override
                    protected void updateItem(Play item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item instanceof SportTitle) {
                            if (empty || item == null) {
                                setText(null);
                                setGraphic(null);
                            } else if (item != null) {
                                Label sportName = new Label(item.getsSport().getName());
                                Button deleteButton = new Button("x");
                                deleteButton.setScaleX(0.6);
                                deleteButton.setScaleY(0.6);
                                deleteButton.setOnAction(event -> deleteSport(item.getsSport(), customTimeLine));
                                HBox grid = new HBox(10, sportName, deleteButton);
                                grid.setAlignment(Pos.CENTER_LEFT);
                                //setText(item.getsSport().getName());
                                setTextAlignment(TextAlignment.LEFT);
                                setFont(Font.font(16));
                                setGraphic(grid);
                                setOnMouseClicked(event -> modifySport(event, item.getsSport()));
                            }
                        } else {
                            if (empty || item == null) {
                                setText(null);
                                setGraphic(null);
                            } else if (item != null) {
                                // Instanciating the objects in the cell :
                                ImageView imageView = new ImageView();
                                Label title = new Label(item.getsTitle());
                                title.setAlignment(Pos.CENTER);
                                title.setPadding(new Insets(5, 10, 5, 10));
                                Button deleteButton = new Button("x");
                                deleteButton.setScaleX(0.6);
                                deleteButton.setScaleY(0.6);
                                deleteButton.setOnAction(event -> deletePlay(item));
                                deleteButton.setAlignment(Pos.TOP_RIGHT);
                                HBox itemInfos = new HBox(10, imageView, title);
                                itemInfos.setAlignment(Pos.CENTER_LEFT);
                                HBox deleteBox = new HBox(deleteButton);
                                deleteBox.setAlignment(Pos.TOP_RIGHT);
                                // Placing objects in the grid :
                                HBox grid = new HBox(10, itemInfos, deleteBox);
                                grid.setAlignment(Pos.CENTER_LEFT);
                                grid.setMaxWidth(treeViewPlays.getWidth());
                                //grid.setPrefSize(treeViewPlays.getWidth(), 40);
                                //setText(item.getsTitle());
                                imageView.setImage(item.getPlayThumbnail());
                                imageView.setFitHeight(40);
                                imageView.setFitWidth(80);
                                imageView.setSmooth(true);
                                setGraphic(grid);
                                setWidth(treeViewPlays.getWidth());
                                setAlignment(Pos.CENTER_LEFT);
                                setOnMouseEntered(event -> setEffect(new Glow(0.3)));
                                setOnMouseExited(event -> setEffect(null));
                            }
                        }

                    }
                };
                return cell;
            }
        });
        treeViewPlays.setEditable(true);

    }

    // Initialise the 'others' panel according to the currentPlay.
    private void initOthersList() {
        System.out.println(backEnd.currentPlay);
        if (backEnd.currentPlay != null) {

            // Adding players
            TreeItem<DraggableObject> treeLabelPlayers = new TreeItem<>(new DraggableObjectTitle("Joueurs"));
            ArrayList<TreeItem> players = getCurrentPlayPlayerList();
            for (int i = 0; i < players.size(); ++i) {
                treeLabelPlayers.getChildren().add(players.get(i));
            }
            // Adding enemies
            TreeItem<DraggableObject> treeLabelEnemies = new TreeItem<>(new DraggableObjectTitle("Adversaires"));
            ArrayList<TreeItem> enemies = getCurrentPlayEnemyList();
            for (int i = 0; i < enemies.size(); ++i) {
                treeLabelEnemies.getChildren().add(enemies.get(i));
            }
            // Adding objectives
            TreeItem<DraggableObject> treeLabelObjective = new TreeItem<>(new DraggableObjectTitle("Objectifs"));
            treeLabelObjective.getChildren().add(getCurrentPlayGameObjective().get(0));
            // Adding obstacles.
            TreeItem<DraggableObject> treeLabelOstacles = new TreeItem<>(new DraggableObjectTitle("Obstacles"));
            ArrayList<TreeItem> obstacles = getCurrentPlayObstaclesList();
            for (int i = 0; i < obstacles.size(); ++i) {
                treeLabelOstacles.getChildren().add(obstacles.get(i));
            }
            TreeItem<DraggableObject> root = new TreeItem<DraggableObject>(new DraggableObjectTitle("Objets"));

            root.getChildren().add(treeLabelPlayers);
            root.getChildren().add(treeLabelEnemies);
            root.getChildren().add(treeLabelObjective);
            root.getChildren().add(treeLabelOstacles);


            treeViewOthers.setCellFactory(new Callback<TreeView, TreeCell>() {
                @Override
                public TreeCell call(TreeView param) {

                    TreeCell<DraggableObject> cell = new TreeCell<DraggableObject>() {
                        @Override
                        protected void updateItem(DraggableObject item, boolean empty) {
                            super.updateItem(item, empty);

                            // If item is a DraggableObjectTitle object.
                            if (item instanceof DraggableObjectTitle) {
                                if (item.getsName().equals("Objets")) {
                                    setPressed(true);
                                    setText("");
                                }
                                if (empty) {
                                    setText(null);
                                } else if (item != null) {
                                    setText(item.sName);
                                    setTextAlignment(TextAlignment.CENTER);
                                    setFont(Font.font(16));
                                }

                                // If item is a dragabble object.
                            } else {
                                if (empty) {
                                    setText(null);
                                    setGraphic(null);
                                } else if (item != null) {
                                    setText(item.sName);
                                    setFont(Font.font(16));
                                    setAlignment(Pos.BASELINE_LEFT);
                                    setTextAlignment(TextAlignment.LEFT);
                                    ImageView imageView = new ImageView();
                                    imageView.setImage(item.image);
                                    imageView.setPreserveRatio(true);
                                    imageView.setFitHeight(40);
                                    imageView.setFitWidth(40);
                                    imageView.setSmooth(true);
                                    if (imageView != null) setGraphic(imageView);
                                    else setGraphic(null);
                                    setOnMouseEntered(event -> setEffect(new Glow(0.3)));
                                    setOnMouseExited(event -> setEffect(null));
                                }
                            }
                        }
                    };
                    cell.setAlignment(Pos.CENTER_LEFT);
                    return cell;
                }
            });
            treeViewOthers.setEditable(true);
            treeViewOthers.setRoot(root);
        }
    }

    // Returns a list of the draggable players for the 'others' panel.
    private ArrayList<TreeItem> getCurrentPlayPlayerList() {
        ArrayList<TreeItem> list = new ArrayList<>();
        List<Player> players = backEnd.currentPlay.getsSport().getPlayerTypes();
        for (Player player : players) {
            player.setImage(new Image("drawable/blue_circle.png"));
            list.add(new TreeItem(player));
        }
        return list;
    }

    // Returns a list of the draggable enemies for the 'others' panel.
    private ArrayList<TreeItem> getCurrentPlayEnemyList() {
        ArrayList<TreeItem> list = new ArrayList<>();
        List<Player> enemies = backEnd.currentPlay.getsSport().getPlayerTypes();
        for (int team = 1; team < backEnd.currentPlay.getsSport().getNumberOfTeams(); ++team) {
            for (Player player : enemies) {
                Player enemy = new Player(player);
                switch (team) {
                    case 1:
                        enemy.setImage(new Image("drawable/red_circle.png"));
                        enemy.setsImagePath("drawable/red_circle.png");
                        break;
                    case 2:
                        enemy.setImage(new Image("drawable/green_circle.png"));
                        enemy.setsImagePath("drawable/green_circle.png");
                        break;
                    case 3:
                        enemy.setImage(new Image("drawable/orange_circle.png"));
                        enemy.setsImagePath("drawable/orange_circle.png");
                        break;
                    case 4:
                        enemy.setImage(new Image("drawable/pink_circle.png"));
                        enemy.setsImagePath("drawable/pink_circle.png");
                        break;
                    case 5:
                        enemy.setImage(new Image("drawable/yellow_circle.png"));
                        enemy.setsImagePath("drawable/yellow_circle.png");
                        break;
                }
                list.add(new TreeItem(enemy));
            }
        }
        return list;
    }

    // Returns a list of the draggable game objectives for the 'others' panel.
    private ArrayList<TreeItem> getCurrentPlayGameObjective() {
        ArrayList<TreeItem> list = new ArrayList<>();
        GameObjective gameObjective = backEnd.currentPlay.getsSport().getObjective();
        list.add(new TreeItem(gameObjective));
        return list;
    }

    // Returns a list of the draggable obstacles for the 'others' panel.
    private ArrayList<TreeItem> getCurrentPlayObstaclesList() {
        ArrayList<TreeItem> list = new ArrayList<>();

        for (int i = 0; i < backEnd.lObstacles.size(); ++i) {
            Obstacle obstacle = backEnd.lObstacles.get(i);
            list.add(new TreeItem(obstacle));
        }
        return list;
    }

    /**
     * Returns the selected Play in the
     * plays tree view.
     *
     * @return Play : Selected play in the treeView.
     */
    private Play getPlayListSelection() {
        TreeItem selection = treeViewPlays.getTreeItem(treeViewPlays.getSelectionModel().getSelectedIndex());
        Play selectionPlay = (Play) (selection != null ? selection.getValue() : null);
        if (!(selectionPlay instanceof SportTitle)) {
            return selectionPlay;
        }
        return null;
    }

    // This is called when the user selects a play in the play list.
    private void switchCurrentPlayTo(Play newPlay) {
        if (newPlay != null && !newPlay.equals(backEnd.currentPlay)) {
            backEnd.currentPlay = newPlay;
            backEnd.currentPlay.eraseGhosts();
            // Scale null and translate null.
            setCanvasScale(1.0);
            canvasField.setTranslateX((scrollPaneForCanvas.getWidth() - canvasField.getWidth()) / 2);
            canvasField.setTranslateY((scrollPaneForCanvas.getHeight() - canvasField.getHeight()) / 2);
            drawCurrentPlayOnCanvas();
            initOthersList(); // Refills the 'others' panel according to currentPlay.

            // Setting the number of frame.
            int numberOfFrame = backEnd.getMaxFrameByFrame();
            customTimeLine.setNumberOfFrames(numberOfFrame);

            // Setting the current frame to 0.
            customTimeLine.setCurrentFrame(0);

            // Enable drag object.
            treeViewOthers.setDisable(false);

            // Setting the label.
            labelTimeleft.setText(customTimeLine.getCurrentFrame() + " / " + customTimeLine.getNumberOfFrames());
            customTimeLine.ajustProgress();
        }
    }

    // Draws the selected play on the canvas.
    private void drawCurrentPlayOnCanvas() {
        if (backEnd.currentPlay != null && !(backEnd.currentPlay instanceof SportTitle)) {

            // Refresh game field.
            drawGameField();

            // Draw players.
            List<Player> players = backEnd.currentPlay.getlPlayers();
            if (players.size() > 0) {
                for (Player player : players) {
                    drawPlayer(player);
                }
            }
            if (backEnd.currentPlay.getlGhosts() != null) {
                List<Player> ghosts = backEnd.currentPlay.getlGhosts();
                if (ghosts.size() > 0) {
                    for (Player ghost : ghosts) {
                        drawPlayerGhost(ghost);
                    }
                }
            }

            // Draw game objectives.
            List<GameObjective> gameObjectives = backEnd.currentPlay.getlGameObjectives();
            if (gameObjectives.size() > 0) {
                for (GameObjective gameObjective : gameObjectives) {
                    drawGameObjective(gameObjective);
                }
            }

            // Draw obstacles.
            List<Obstacle> obstacles = backEnd.currentPlay.getlObstacles();
            if (obstacles.size() > 0) {
                for (Obstacle obstacle : obstacles) {
                    drawObstacle(obstacle);
                }
            }
        } else {
            // Clear the canvas.
            canvasField.getGraphicsContext2D().clearRect(0, 0, canvasField.getWidth(), canvasField.getHeight());
        }
    }

    /**
     * Ajouter au DC
     * Draws the current play game field on the canvas.
     */
    private void drawGameField() {
        canvasField.getGraphicsContext2D().clearRect(0, 0, canvasField.getWidth(), canvasField.getHeight());
        canvasField.getGraphicsContext2D().drawImage(backEnd.currentPlay.getsSport().getGameField().getFieldImage(), 0, 0, canvasField.getWidth(), canvasField.getHeight());
    }

    /**
     * TODO : Ajouter au DC
     * Draw the player 'player' on the canvas at his current position.
     *
     * @param player : Player to draw.
     */
    private void drawPlayer(Player player) {
        double selectedScale = 1.25;
        // Player is selected :
        if (!player.isOverOtherDraggableObject() && (player.equals(backEnd.selectedObject) || player.equals(backEnd.grabbedObject))) {
            canvasField.getGraphicsContext2D().setEffect(new Glow(0.8));
           _drawPlayer(player, selectedScale);
            canvasField.getGraphicsContext2D().setEffect(null);
            // Player is not selected :
        } else if (player.isOverOtherDraggableObject() && (player.equals(backEnd.selectedObject) || player.equals(backEnd.grabbedObject))) {
            canvasField.getGraphicsContext2D().setEffect(new ColorAdjust(0, 0, 0, -0.7));
            _drawPlayer(player, 1);
            canvasField.getGraphicsContext2D().setEffect(null);
        } else {

            // If player has not been dragged : in grey
            if (!player.getHasBeenDragged() && customTimeLine.getCurrentFrame() != 0) {
                canvasField.getGraphicsContext2D().setEffect(new ColorAdjust(0, 0, 0, -0.7));
                _drawPlayer(player, 1);
                canvasField.getGraphicsContext2D().setEffect(null);
                // If player has not been dragged yet : in color
            } else if (player == backEnd.rotatedObject &&  player != backEnd.grabbedObject) {

                _drawPlayer(player, 1);
                _drawRotationCircle(player);

            } else {
                _drawPlayer(player, 1);
            }
        }

        // Show player informations if options is enabled :
        if (showPlayerInformations) {
            _drawPlayerInformations(player);
        }
    }

    private void _drawPlayer(Player player, double selectedScale) {

        ImageView iv = new ImageView(player.getImage());
        iv.setRotate(player.getRotation());
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = iv.snapshot(params, null);


        _drawShadows(player.getiPosX() - 15 * selectedScale, player.getiPosY() - 15 * selectedScale, selectedScale);
        canvasField.getGraphicsContext2D().drawImage(rotatedImage, player.getiPosX() - 15 * selectedScale, player.getiPosY() - 15 * selectedScale, 30 * selectedScale, 30 * selectedScale);
    }

    /**
     *
     * Draw the player ghost after moving.
     * @param  ghost Player to draw ghost of.
     */
    private void drawPlayerGhost(Player ghost) {

        ImageView iv = new ImageView(ghost.getImage());
        iv.setRotate(ghost.getRotation());
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = iv.snapshot(params, null);

        _drawShadows(ghost.getiPosX() - 15, ghost.getiPosY() - 15, 1);
        canvasField.getGraphicsContext2D().setEffect(new ColorAdjust(0, 0, 0, -0.5));
        canvasField.getGraphicsContext2D().drawImage(rotatedImage, ghost.getiPosX() - 15, ghost.getiPosY() - 15, 30, 30);
        canvasField.getGraphicsContext2D().setEffect(null);
        if (showPlayerInformations) {
            _drawPlayerInformations(ghost);
        }

    }

    /**
     * TODO : Ajouter au DC
     * Draw the gameobjective 'gameObjective' at his
     * current position.
     *
     * @param gameObjective : GameObjective to draw.
     */
    private void drawGameObjective(GameObjective gameObjective) {

        double scale = gameObjective.getScale();
        double selectedScale = gameObjective.getScale() * 1.25 * scale;
        if (!gameObjective.isOverOtherDraggableObject() && (gameObjective.equals(backEnd.selectedObject) || gameObjective.equals(backEnd.grabbedObject))) {
            canvasField.getGraphicsContext2D().setEffect(new Glow(0.8));
            this.canvasField.getGraphicsContext2D().drawImage(gameObjective.getImage(), gameObjective.getiPosX() - 15 * selectedScale, gameObjective.getiPosY() - 15 * selectedScale, 30 * selectedScale, 30 * selectedScale);
            canvasField.getGraphicsContext2D().setEffect(null);
        } else if (gameObjective.isOverOtherDraggableObject() && (gameObjective.equals(backEnd.selectedObject) || gameObjective.equals(backEnd.grabbedObject))) {
            canvasField.getGraphicsContext2D().setEffect(new ColorAdjust(0, 0, 0, -0.7));
            this.canvasField.getGraphicsContext2D().drawImage(gameObjective.getImage(), gameObjective.getiPosX() - 15 * selectedScale, gameObjective.getiPosY() - 15 * selectedScale, 30 * selectedScale, 30 * selectedScale);
            canvasField.getGraphicsContext2D().setEffect(null);
        } else {
            this.canvasField.getGraphicsContext2D().drawImage(gameObjective.getImage(), gameObjective.getiPosX() - 15 * scale, gameObjective.getiPosY() - 15 * scale, 30 * scale, 30 * scale);
        }
    }

    /**
     * TODO : Ajouter au DC
     * Draw the obstacle 'obstacle' at his current position.
     *
     * @param obstacle
     */
    private void drawObstacle(Obstacle obstacle) {
        double scale = obstacle.getScale();
        double selectedScale = obstacle.getScale() * 1.25 * scale;
        if (obstacle.equals(backEnd.selectedObject) || obstacle.equals(backEnd.grabbedObject)) {
            canvasField.getGraphicsContext2D().setEffect(new Glow(0.8));
            this.canvasField.getGraphicsContext2D().drawImage(obstacle.getImage(), obstacle.getiPosX() - 15 * selectedScale, obstacle.getiPosY() - 15 * selectedScale, 30 * selectedScale, 30 * selectedScale);
            canvasField.getGraphicsContext2D().setEffect(null);
        } else {
            this.canvasField.getGraphicsContext2D().drawImage(obstacle.getImage(), obstacle.getiPosX() - 15 * scale, obstacle.getiPosY() - 15 * scale, 30 * scale, 30 * scale);
        }
    }

    /**
     * Draws shadows underneath a player at the x,y position.
     *
     * @param x X position to draw shadow.
     * @param y Y position to draw shadow.
     */

    private void _drawShadows(double x, double y, double scale) {
        canvasField.getGraphicsContext2D().setFill(Color.BLACK);
        canvasField.getGraphicsContext2D().fillOval(x - 2, y - 2, 34 * scale, 34 * scale);
    }

    private void _drawRotationCircle(Player player) {

        if (player.getsImagePath().equals("drawable/blue_circle.png")) {
            canvasField.getGraphicsContext2D().setFill(Color.BLUE);
            canvasField.getGraphicsContext2D().setStroke(Color.BLUE);
        }
        if (player.getsImagePath().equals("drawable/green_circle.png")) {
            canvasField.getGraphicsContext2D().setFill(Color.LIGHTGREEN);
            canvasField.getGraphicsContext2D().setStroke(Color.LIGHTGREEN);
        }
        if (player.getsImagePath().equals("drawable/orange_circle.png")) {
            canvasField.getGraphicsContext2D().setFill(Color.ORANGE);
            canvasField.getGraphicsContext2D().setStroke(Color.ORANGE);
        }
        if (player.getsImagePath().equals("drawable/pink_circle.png")) {
            canvasField.getGraphicsContext2D().setFill(Color.PINK);
            canvasField.getGraphicsContext2D().setStroke(Color.PINK);
        }
        if (player.getsImagePath().equals("drawable/red_circle.png")) {
            canvasField.getGraphicsContext2D().setFill(Color.RED);
            canvasField.getGraphicsContext2D().setStroke(Color.RED);
        }

        canvasField.getGraphicsContext2D().setLineWidth(2);
        canvasField.getGraphicsContext2D().strokeOval(player.getiPosX() - 50, player.getiPosY() - 50, 100, 100);

    }

    /**
     * Draw the player informations on the canvas.
     *
     * @param player : Player to draw informations of.
     */
    private void _drawPlayerInformations(Player player) {

        if (player.equals(backEnd.selectedObject) || player.equals(backEnd.grabbedObject)) {
            canvasField.getGraphicsContext2D().fillText(player.getPlayerName() != null ?
                    player.getPlayerName() : null, player.getiPosX(), player.getiPosY() + 25 * 1.25);
            canvasField.getGraphicsContext2D().fillText(player.getsName() != null ?
                    player.getsName() : null, player.getiPosX(), player.getiPosY() + 35 * 1.25);
        } else {
            canvasField.getGraphicsContext2D().fillText(player.getPlayerName() != null ?
                    player.getPlayerName() : null, player.getiPosX(), player.getiPosY() + 25);
            canvasField.getGraphicsContext2D().fillText(player.getsName() != null ?
                    player.getsName() : null, player.getiPosX(), player.getiPosY() + 35);
        }


    }

    /**
     * TODO : Ajouter au diagramme de classes.
     * Select an object at the event position on left click.
     * On right click, unselect the object. If there is an object at
     * the mouse location, the object is set as the 'selectObject'.
     *
     * @param event MouseEvent click.
     */
    private void selectObject(MouseEvent event) {
        if (backEnd.currentPlay != null) {

            // Right Click :
            if (event.getButton() == MouseButton.SECONDARY) {
                backEnd.selectedObject = null;

                // Left Click :
            } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {

                // Check if click is on a player :
                for (Player player : backEnd.currentPlay.getlPlayers()) {
                    if (player.getBoundary().contains(event.getX(), event.getY())) {
                        if (backEnd.selectedObject == player) {
                            backEnd.selectedObject = null;
                            break;
                        } else {
                            backEnd.selectedObject = player;
                            PlayerCreationDialog playerCreationDialog = new PlayerCreationDialog(player.getPlayerName()); //send backend directly ? nah
                            Optional<ButtonType> cancel = playerCreationDialog.showAndWait();
                            player.setPlayerName(playerCreationDialog.getPlayerName());
                            backEnd.selectedObject = null;
                            break;
                        }
                    }
                } // Check if click is on a game objective :
                for (GameObjective gameObjective : backEnd.currentPlay.getlGameObjectives()) {
                    if (gameObjective.getBoundary().contains(event.getX(), event.getY())) {
                        if (backEnd.selectedObject == gameObjective) {
                            backEnd.selectedObject = null;
                            break;
                        } else {
                            backEnd.selectedObject = gameObjective;
                            break;
                        }
                    }
                } // Check if click is on a obstacle :
                for (Obstacle obstacle : backEnd.currentPlay.getlObstacles()) {
                    if (obstacle.getBoundary().contains(event.getX(), event.getY())) {
                        if (backEnd.selectedObject == obstacle) {
                            backEnd.selectedObject = null;
                            break;
                        } else {
                            backEnd.selectedObject = obstacle;
                            break;
                        }
                    }
                }
            }
        }
        // Refresh the canvas.
        drawCurrentPlayOnCanvas();
    }

    /**
     * TODO : Ajouter au diagramme de classes.
     * Attempt to grab an object to drag it and change his position. The event given
     * in argument is the mouse click. If there is an object at the mouse left click location,
     * the object is set as the 'grabbedObject'.
     *
     * @param event MouseEvent mouse click.
     */
    private void grabObject(MouseEvent event) {
        if (backEnd.currentPlay != null) {
            // Left click :
            if (event.getButton() == MouseButton.PRIMARY) {
                _grabObjectToDrag(event);
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                _grabObjectToRotate(event);
            }
            if (event.getButton() == MouseButton.SECONDARY && event.isAltDown() && backEnd.grabbedObject == null) {
                backEnd.grabbedObject = null;
                canvasGrabbed = true;
            }  else {
                canvasGrabbed = false;
            }
        }
        // Refreshing the canvas.
        drawCurrentPlayOnCanvas();
    }

    private void _grabObjectToDrag(MouseEvent event) {
        canvasGrabbed = false;
        backEnd.selectedObject = null; // Unselect the current selectObject.

        if (backEnd.grabbedObject != null) return; // If grab is already ongoing.

        // Check if click is on a player :
        for (Player player : backEnd.currentPlay.getlPlayers()) {
            if (player.getBoundary().contains(event.getX(), event.getY())) {
                if (backEnd.grabbedObject == player) {
                    backEnd.grabbedObject = null;
                    break;
                } else {
                    backEnd.grabbedObject = player;
                    backEnd.grabbedObject.setHasBeenDragged(true);
                    // Adding ghost of player after movement if not on frame one :
                    if (customTimeLine.getCurrentFrame() != 0) {
                        backEnd.currentPlay.addGhost(event.getX(), event.getY(), player);
                    }

                }
            }
        } // Check if click is on a game objective :
        for (GameObjective gameObjective : backEnd.currentPlay.getlGameObjectives()) {
            if (gameObjective.getBoundary().contains(event.getX(), event.getY())) {
                if (backEnd.grabbedObject == gameObjective) {
                    backEnd.grabbedObject = null;
                    break;
                } else backEnd.grabbedObject = gameObjective;
            }
        } // Check if click is on a obstacle :
        for (Obstacle obstacle : backEnd.currentPlay.getlObstacles()) {
            if (obstacle.getBoundary().contains(event.getX(), event.getY())) {
                if (backEnd.grabbedObject == obstacle) {
                    backEnd.grabbedObject = null;
                    break;
                } else backEnd.grabbedObject = obstacle;
            }
        }
    }

    private void _grabObjectToRotate(MouseEvent event) {
        canvasGrabbed = false;
        backEnd.rotatedObject = null; // Unselect the current selectObject.

        if (backEnd.rotatedObject != null) return; // If grab is already ongoing.

        // Check if click is on a player :
        for (Player player : backEnd.currentPlay.getlPlayers()) {
            if (player.getRotationBoundary().contains(event.getX(), event.getY())) {
                if (backEnd.rotatedObject == player) {
                    backEnd.rotatedObject = null;
                    break;
                } else {
                    backEnd.rotatedObject = player;
                }
            }
        } // Check if click is on a game objective :
        for (GameObjective gameObjective : backEnd.currentPlay.getlGameObjectives()) {
            if (gameObjective.getBoundary().contains(event.getX(), event.getY())) {
                if (backEnd.rotatedObject == gameObjective) {
                    backEnd.rotatedObject = null;
                    break;
                } else backEnd.rotatedObject = gameObjective;
            }
        } // Check if click is on a obstacle :
        for (Obstacle obstacle : backEnd.currentPlay.getlObstacles()) {
            if (obstacle.getBoundary().contains(event.getX(), event.getY())) {
                if (backEnd.rotatedObject == obstacle) {
                    backEnd.rotatedObject = null;
                    break;
                } else backEnd.rotatedObject = obstacle;
            }
        }
    }

    /**
     * TODO : Ajouter au diagramme de classes.
     * Deselects the 'grabbedObject' and the 'selectedObject'.
     *
     * @param event MouseEvent mouse click.
     */
    private void deselectObjects(MouseEvent event) {
        if (backEnd.grabbedObject != null && event.getButton() == MouseButton.PRIMARY) {

            // Updating players position on the timeline of current play.
            _updatePlayersTimeline(event);

            // Dropping only if not over an obstacle.
            if (!backEnd.grabbedObject.isOverDraggableObject()) {

                if (backEnd.pastOperations != null) {
                    backEnd.pastOperations.add(new OperationRecord(backEnd.grabbedObject, event.getX(), event.getY(), operationType.movement));
                }
                _deselectGrabbedObject();
            }
        } if (backEnd.rotatedObject != null && event.getButton() == MouseButton.SECONDARY) {
            backEnd.rotatedObject = null;
        } if (canvasGrabbed && event.getButton() == MouseButton.SECONDARY && event.isAltDown()) {
            canvasGrabbed = false;
        }
        drawCurrentPlayOnCanvas();
    }

    private void _updatePlayersTimeline(MouseEvent event) {
        int id = backEnd.grabbedObject.getObjectId();
        if (backEnd.grabbedObject instanceof Player) {
            if (viewMode.equals(ViewMode.Realtime)) {
                FrontController.this.backEnd.updatePlayer(id, event.getX(), event.getY(), "R", iGlobalCursor);
            }
            else
            {
                FrontController.this.backEnd.updatePlayer(id, event.getX(), event.getY(), "F", iGlobalCursor);
            }
        }
        else if (backEnd.grabbedObject instanceof Obstacle) {
            if (viewMode.equals(ViewMode.Realtime)) {
                System.out.println("pomme4terre");
                FrontController.this.backEnd.updateObstacle(id, event.getX(), event.getY(), "R", iGlobalCursor);
            }
            else
            {
                System.out.println("pomme3terre");
                FrontController.this.backEnd.updateObstacle(id, event.getX(), event.getY(), "F", iGlobalCursor);
            }
        }
        else if (backEnd.grabbedObject instanceof GameObjective) {
            if (viewMode.equals(ViewMode.Realtime)) {
                FrontController.this.backEnd.updateGameObjective(id, event.getX(), event.getY(), "R", iGlobalCursor);
            }
            else
            {
                FrontController.this.backEnd.updateGameObjective(id, event.getX(), event.getY(), "F", iGlobalCursor);
            }
        }
    }

    private void _deselectGrabbedObject() {
        backEnd.grabbedObject = null;
        backEnd.selectedObject = null;
        canvasGrabbed = false;
    }


    /**
     * TODO : Ajouter au diagramme de classes.
     * Select the draggable object at the 'event' location and
     * start to drag it along the mouse until the mouse is released.
     *
     * @param event MouseEvent mouse click.
     */
    private void onDragStartedOnObject(MouseEvent event) {
        grabObject(event);
        if (backEnd.grabbedObject != null) {
            ajustGrabbedObjectPosition(event.getX(), event.getY());
            // Check if real time mode. If it's the case, then the grabbedObject must have new positions.
            if (viewMode.equals(ViewMode.Realtime) && backEnd.grabbedObject != null) {
                _updatePlayers(event);
            }
        }
        if (backEnd.rotatedObject != null) {
            rotatePlayer(event, (Player) backEnd.rotatedObject);
        }

        if (canvasGrabbed && event.isSecondaryButtonDown()) {
            canvasField.setTranslateX(event.getSceneX() - (canvasField.getWidth() / 1));
            canvasField.setTranslateY(event.getSceneY() - (canvasField.getHeight() / 2));
        }
        drawCurrentPlayOnCanvas();
    }

    private void _updatePlayers(MouseEvent event) {
        // Case where grabbedObject is a player.
        if (backEnd.grabbedObject instanceof Player)
        {
            iGlobalCursor++;
            // Add object new position.
            backEnd.updatePlayer(backEnd.grabbedObject.getObjectId(), event.getX(), event.getY(), "R", iGlobalCursor);
        }
        // Case where grabbedObject is an obstacle.
        else if (backEnd.grabbedObject instanceof Obstacle)
        {
            iGlobalCursor++;
            // Add object new position.
            backEnd.updateObstacle(backEnd.grabbedObject.getObjectId(), event.getX(), event.getY(), "R", iGlobalCursor);
        }
        // Case where grabbedObject is a game objective.
        else if (backEnd.grabbedObject instanceof GameObjective)
        {
            iGlobalCursor++;
            // Add object new position.
            backEnd.updateGameObjective(backEnd.grabbedObject.getObjectId(), event.getX(), event.getY(), "R", iGlobalCursor);
        }
    }

    // Ajusts the game field Labels to the current mouse position.
    private void onMouseOverCanvas(MouseEvent event) {
        if (backEnd.currentPlay != null) adjustGameFieldLabels(event.getX(), event.getY());
        if (backEnd.grabbedObject != null) ajustGrabbedObjectPosition(event.getX(), event.getY());
    }

    // Show the game field Labels on mouse enters canvas.
    private void onMouseEntersCanvas() {
        if (backEnd.currentPlay != null) {
            labelGameFieldHeight.setVisible(true);
            labelGameFieldWidth.setVisible(true);
        }
    }

    // Hide the game field Labels on mouse exit canvas.
    private void onMouseExitsCanvas() {
        labelGameFieldHeight.setVisible(false);
        labelGameFieldWidth.setVisible(false);
    }

    // This handles the start of drag and drop gestures
    private void startDrag(Event event) {
        TreeItem itemObject = treeViewOthers.getTreeItem(treeViewOthers.getSelectionModel().getSelectedIndex());
        DraggableObject selectedObject = (DraggableObject) (itemObject != null ? itemObject.getValue() : null);
        Node source = (Node) event.getTarget();
        dragboard = source.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString("creation");
        dragboard.setContent(content);
        event.consume();
    }

    // This handles the dropping part of drag and drop gestures
    private void canvasDropEventHandler(DragEvent event) {

        //might not be necessary to inform source of successful drag'n'dropping
        boolean success = false;


        if (dragboard.hasString()) {
            String text = dragboard.getString();

            success = !success;
            if (text == "creation") {
                DraggableObject selectedObject = (DraggableObject) treeViewOthers.getTreeItem(treeViewOthers.getSelectionModel().getSelectedIndex()).getValue();
                DraggableObject newObject = null;
                int x_event = (int) event.getX();
                int y_event = (int) event.getY();

                //TODO : this chunk of code is abhorrent and I hate it --refactor
                if (!(selectedObject instanceof DraggableObjectTitle)) {
                    if (selectedObject instanceof Player) {
                        String tmp = showPlayerCreationDialog();
                        if (!tmp.equals("")) {
                            newObject = new Player(selectedObject.getsName(), x_event, y_event, selectedObject.getImage(), selectedObject.getsImagePath(), backEnd.currentPlay.getNewId(), backEnd.currentPlay.getTimestampZero());
                            Player tmpPlayer = (Player) newObject;
                            tmpPlayer.setPlayerName(tmp);
                        }

                    } else if (selectedObject instanceof GameObjective) {
                        newObject = new GameObjective(selectedObject.getsName(), x_event, y_event, selectedObject.getImage(), selectedObject.getsImagePath(), backEnd.currentPlay.getTimestampZero(), selectedObject.getScale());
                        newObject.setId(backEnd.currentPlay.getNewId());
                    } else if (selectedObject instanceof Obstacle) {
                        newObject = new Obstacle(selectedObject.getsName(), x_event, y_event, selectedObject.getImage(), selectedObject.getsImagePath(), backEnd.currentPlay.getTimestampZero(), selectedObject.getScale());
                        newObject.setId(backEnd.currentPlay.getNewId());
                    }
                    if (newObject != null) {
                        newObject.setiPosX(x_event);
                        newObject.setiPosY(y_event);

                        for (int i = 0; backEnd.firstDraggable() != null && i < backEnd.firstDraggable().getlPositions().size() - 1; i++)
                        {
                            newObject.addPositionFake(-1, -1, newObject.getTimestamp(), backEnd.firstDraggable().getlPositions().get(i).mode);
                        }

                        if (newObject instanceof Player) {

                            if (backEnd.currentPlay.getNewPlayerApproval(newObject.getsImagePath()))
                                backEnd.currentPlay.getlPlayers().add((Player) newObject);
                            else
                                newObject = null;
                        }
                        if (newObject instanceof GameObjective)
                            backEnd.currentPlay.getlGameObjectives().add((GameObjective) newObject);
                        if (newObject instanceof Obstacle) {
                            backEnd.currentPlay.getlObstacles().add((Obstacle) newObject);
                        }

                        drawCurrentPlayOnCanvas();
                    }
                }
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    // This handles dragover in drag and drop gestures
    private void canvasDragEventHandler(DragEvent event) {
        event.acceptTransferModes(TransferMode.ANY);
        event.consume();
    }

    // Ajust the game field Labels depending on the mouse position over the canvas.
    private void adjustGameFieldLabels(double mouseX, double mouseY) {
        Integer[] units = backEnd.convertPxtoRLUnits(mouseX, mouseY, canvasField);
        labelGameFieldWidth.setText("X: " + units[0].toString() + " " + backEnd.currentPlay.getsSport().getGameField().getUnit());
        labelGameFieldHeight.setText("Y: " + units[1].toString() + " " + backEnd.currentPlay.getsSport().getGameField().getUnit());
    }

    /**
     * TODO : Ajouter au diagramme de classes.
     * Ajusts the position of the current 'grabbedObject' and
     * then refresh the canvas.
     *
     * @param x New 'x' position for the object.
     * @param y New 'y' position for the object.
     */
    private void ajustGrabbedObjectPosition(double x, double y) {
        if (backEnd.grabbedObject != null && !canvasGrabbed) {

            // Checking if player is over a player :
            boolean overAnotherDraggable = false;

            if (backEnd.currentPlay.getlPlayers() != null) {
                for (Player player : backEnd.currentPlay.getlPlayers()) {
                    if (backEnd.grabbedObject.intersect(player)) {
                        overAnotherDraggable = true;
                    }
                }
            }

            if (backEnd.currentPlay.getlGameObjectives() != null) {
                // Checking if player is over a game objective :
                for (GameObjective gameObjective : backEnd.currentPlay.getlGameObjectives()) {
                    if (backEnd.grabbedObject.intersect(gameObjective)) {
                        overAnotherDraggable = true;
                    }
                }
            }

            if (backEnd.currentPlay.getlObstacles() != null) {
                // Checking if player is over an obstacle :
                for (Obstacle obstacle : backEnd.currentPlay.getlObstacles()) {
                    if (backEnd.grabbedObject.intersect(obstacle)) {
                        overAnotherDraggable = true;
                    }
                }
            }

            if (overAnotherDraggable) {
                backEnd.grabbedObject.setOverOtherDraggable(true);
            } else {
                backEnd.grabbedObject.setOverOtherDraggable(false);
            }

            canvasGrabbed = false;
            backEnd.grabbedObject.setiPosX((int) x);
            backEnd.grabbedObject.setiPosY((int) y);
            drawCurrentPlayOnCanvas();
        }
    }


    private void rotatePlayer(MouseEvent event, Player player) {
        Point.Double p1 = new Point.Double(player.getiPosX(), player.getiPosY());
        Point.Double p2 = new Point.Double(event.getX(), event.getY());
        double angle = Utility.getAngle(p1, p2);
        player.setRotation(angle);

    }

    // Toggles the view mode between 'Frame by frame' and 'Real-time' modes.
    private void toggleViewMode()
    {
        // Updating the internal time.
        nbSecondes++;

        // Is time correct?
        if (nbSecondes % 60 == 0)
        {
            // Adjust minutes.
            nbMinutes++;

            // Reset seconds.
            nbSecondes = 0;
        }

        // Switch to Frame by Frame
        if (viewMode == ViewMode.Realtime) {
            togglebuttonViewMode.setText("Image par image");
            viewMode = ViewMode.FrameByFrame;
            sGlobalmode = "F";
            this.fastForwardPlayback();
            labelTimeleft.setText(customTimeLine.getCurrentFrame() + " / " + customTimeLine.getNumberOfFrames());
        }

        // Switch to Real Time
        else if (viewMode == ViewMode.FrameByFrame
                && customTimeLine.getCurrentFrame() == customTimeLine.getNumberOfFrames())
        {
            System.out.println("Je suis marre.");
            togglebuttonViewMode.setText("Temps réel");
            viewMode = ViewMode.Realtime;
            labelTimeleft.setText(nbMinutes + ":" + nbSecondes);
            treeViewOthers.setDisable(true);
            sGlobalmode = "R";
            backEnd.currentPlay.eraseGhosts();

            drawCurrentPlayOnCanvas();


            // Ajusting the sleeping time.
            sleepingTime = 100;

            // Thread creation for time.
            Task<Void> threadRealTime = new Task<Void>() {
                @Override
                // Method call by the task.
                protected Void call() throws Exception {
                    // The sleep function need a catch.
                    try
                    {
                        // Iterate while we are in real time.
                        while (FrontController.this.viewMode.equals(ViewMode.Realtime)
                                && !FrontController.this.isPlaying)
                        {
                            // Sleeping time.
                            Thread.sleep(FrontController.this.sleepingTime);

                            // Increment timestamp.
                            FrontController.this.backEnd.currentPlay.setCurrentTimestamp(
                                    new Timestamp(FrontController.this.backEnd.currentPlay.getCurrentTimestamp().getTime()
                                            + 1));

                            //Increment the global cursor
//                            FrontController.this.iGlobalCursor++;

                            // Update number of sleeps.
                            FrontController.this.nbSleeps++;

                            // Update interface each minute.
                            if (FrontController.this.nbSleeps % 600 == 0)
                            {
                                // new minute.
                                FrontController.this.nbMinutes++;

                                // Reset secondes.
                                FrontController.this.nbSecondes = 0;
                            }
                            // Update interface second
                            else if (FrontController.this.nbSleeps % 10 == 0)
                            {
                                // Update seconds
                                FrontController.this.nbSecondes++;
                            }

                            // Ajusting the text field which indicates at which frame the user is.
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    // The view has to be real time.
                                    if (FrontController.this.viewMode.equals(ViewMode.Realtime))
                                    {
                                        // Set text.
                                        FrontController.this.labelTimeleft.setText(nbMinutes + ":" + nbSecondes);
                                    }
                                }
                            });
                        }

                    }
                    // Sleep exception.
                    catch (InterruptedException e) {
                    }

                    // No return in void function.
                    return null;
                }

            };

            // Function on thread succeed.
            threadRealTime.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {

                }
            });

            // Starting of the thread.
            new Thread(threadRealTime).start();
        }
    }

    /**
     * TODO : Ajouter au diagramme de classe.
     * Toggles the name above the players on canvas.
     */
    private void toggleShowPlayerInformations() {
        showPlayerInformations = !showPlayerInformations;
        drawCurrentPlayOnCanvas();
    }

    /**
     * Go to the last frame in Frame by frame mode and
     * rewind the viewing in the realtime mode.
     */
    public void rewindPlayback() {
        // Frame by frame mode :
        if (viewMode.equals(ViewMode.FrameByFrame) && !isPlaying) {
            // Can't be used if the frame is the first.
            if (customTimeLine.getCurrentFrame() == 0) {
                return;
            }
            // Erasing the ghosts of player movements.
            backEnd.currentPlay.eraseGhosts();
            // Interface stuff :
            customTimeLine.lastFrame();
            while (true) {
                this.iGlobalCursor--;
                if (backEnd.firstDraggable().getlPositions().get(iGlobalCursor).mode.equals("F"))
                    break;
            }
            labelTimeleft.setText(customTimeLine.getCurrentFrame() + " / " + customTimeLine.getNumberOfFrames());

            // Setting the old positions to each object.
            backEnd.readTriple(iGlobalCursor);

            //update timestamp
            backEnd.currentPlay.setCurrentTimestamp(
                    new Timestamp(backEnd.currentPlay.getCurrentTimestamp().getTime() - 1));

            // Blocking the user from adding new objects if it's not the first frame.
            if (customTimeLine.getCurrentFrame() != 0) {
                treeViewOthers.setDisable(true);
            } else {
                treeViewOthers.setDisable(false);
            }

            // Ajusting time line.
            customTimeLine.ajustProgress();

            // Set the players back to lower saturation.
            backEnd.currentPlay.setPlayersWithLowerSaturation();
            // Erase the ghost of players.
            backEnd.currentPlay.eraseGhosts();
            drawCurrentPlayOnCanvas();
        }
        // Realtime mode :
        else {

        }
    }

    /**
     * This function slow the playback by growing the sleep time.
     */
    public void slowPlayback() {
        // The play has to be playing to be slower.
        if (isPlaying) {
            sleepingTime *= 2;
        }
    }

    /**
     * Start / Pause the viewing in the realtime mode.
     */
    public void playPausePlayback() {
        // Realtime mode :
        if (/*viewMode.equals(ViewMode.FrameByFrame) && */!isPlaying) {

            int maxFrame = backEnd.getMaxFrameByFrame();
            if (maxFrame != customTimeLine.getNumberOfFrames())
            {
                backEnd.doubleLastEntry(-100, maxFrame + 1, sGlobalmode);
            }

            backEnd.currentPlay.eraseGhosts();
            // Getting the access to the play.
            isPlaying = true;

            // Disabling the adding of new object. This case happen when the user start his play at frame 0.
            treeViewOthers.setDisable(true);

            // The thread musn't be stop at his beginning.
            threadStop = false;

            customTimeLine.setCurrentFrame(0);


            // Thread creation.
            Task<Void> sleeper = new Task<Void>() {
                @Override
                // Method call by the task.
                protected Void call() throws Exception {
                    // The sleep function need a catch.
                    try {
                        String nextMode = "";
                        FrontController.this.sleepingTime = 1000;
                        for (int i = 0; i < FrontController.this.backEnd.firstDraggable().getlPositions().size(); i++) {
                            FrontController.this.iGlobalCursor = i;

                            // Case where the case wasn't paused but only stopped.
                            if (FrontController.this.threadStop && !FrontController.this.wasPaused) {
                                // Setting the current frame to 0.
                                FrontController.this.customTimeLine.setCurrentFrame(0);

                                // Setting the positions.
                                FrontController.this.backEnd.setItemsNewPos(0);

                                // Drawing the right positions.
                                FrontController.this.drawCurrentPlayOnCanvas();

                                // Ajusting progess.
                                FrontController.this.customTimeLine.ajustProgress();

                                // Adjusting button.
                                FrontController.this.viewMode = ViewMode.FrameByFrame;
                                FrontController.this.togglebuttonViewMode.setText("Image par image");

                                FrontController.this.iGlobalCursor = 0;
                            }

                            // Checking if the thread has to stop his work.
                            if (FrontController.this.threadStop) {
                                // Setting the image stopped on.
                                FrontController.this.imagePausedOn = i - 1;

                                // Exiting the thread.
                                break;
                            }

                            // Checking if the thread was previously paused.
                            if (FrontController.this.wasPaused) {
                                // Getting back the image.
                                i = (int)FrontController.this.imagePausedOn;

                                // Pause is done.
                                FrontController.this.wasPaused = false;

                                if (FrontController.this.backEnd.firstDraggable().getlPositions().get(i).mode.equals("R"))
                                    FrontController.this.sleepingTime = 100;

                                int iFrameBeforePose = FrontController.this.backEnd.getNbOfFrameForI(i);
                                FrontController.this.customTimeLine.setCurrentFrame(iFrameBeforePose);
                            }

                            int j = i + 1;

                            if (j != FrontController.this.backEnd.firstDraggable().getlPositions().size())
                            {
                                nextMode = FrontController.this.backEnd.readTriple(j);
                            }

                            // Setting the new old positions.
                            final String modeOfPlay = FrontController.this.backEnd.readTriple(i);
                            FrontController.this.drawCurrentPlayOnCanvas();

                            if (!modeOfPlay.equals(nextMode))
                            {
                                if (Objects.equals(nextMode, "R"))
                                {
                                    FrontController.this.sleepingTime = 50;
                                }
                                else if (Objects.equals(nextMode, "F"))
                                {
                                    FrontController.this.sleepingTime = 1000;
                                }
                            }

                            // Ajusting the current frame and the progress.
                            if (modeOfPlay.equals("F") && i != 0)
                            {
                                FrontController.this.customTimeLine.setCurrentFrame(
                                        FrontController.this.customTimeLine.getCurrentFrame() + 1);
                                FrontController.this.customTimeLine.ajustProgress();
                                System.out.println("current frame = " + FrontController.this.customTimeLine.getCurrentFrame());
                            }
                            else if (modeOfPlay.equals("R"))
                            {
                            }
                            System.out.println("access 1 = " + Platform.isAccessibilityActive());

                            Task<Void> graphicalThread = new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                    return null;
                                }
                            };

                            graphicalThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                                @Override
                                public void handle(WorkerStateEvent workerStateEvent) {
                                    FrontController.this.labelTimeleft.setText(
                                            FrontController.this.customTimeLine.getCurrentFrame() + " / " +
                                                    FrontController.this.customTimeLine.getNumberOfFrames());
                                    FrontController.this.customTimeLine.ajustProgress();
                                }
                            });

                            new Thread(graphicalThread).start();

                            // Making the thread sleep to simulate the frame by frame feeling.
                            Thread.sleep(FrontController.this.sleepingTime);
                        }
                    }
                    // Sleep exception.
                    catch (Exception e) {
                        System.out.println("mais quel coonnnn !" + e.getMessage());
                    }

                    // No return in void function.
                    return null;
                }

            };

            // Function on thread succeed.
            sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    // Enabling replay.
                    isPlaying = false;
                    if (viewMode == ViewMode.FrameByFrame) {
                        // Setting the last frame.
                        labelTimeleft.setText(customTimeLine.getCurrentFrame() + " / "
                                + customTimeLine.getNumberOfFrames());

                        // Looking if the tree of draggable objects has to be disable.
                        // If the frame is 0, then the tree isn't disable.
                        if (customTimeLine.getCurrentFrame() == 0) {
                            treeViewOthers.setDisable(false);
                        }
                        // If the frame isn't 0, then the tree is disable.
                        else {
                            treeViewOthers.setDisable(true);
                        }
                    }
                    else
                    {
                        viewMode = ViewMode.FrameByFrame;
                        FrontController.this.toggleViewMode();
                        labelTimeleft.setText(nbMinutes + " : " + nbSecondes);
                    }
                }
            });

            // Starting of the thread.
            new Thread(sleeper).start();
        }
    }

    /**
     * Stop the viewing on Real time view.
     */
    public void stopPlayback() {
        // Stopping the thread.
        threadStop = true;
    }

    /**
     * Pause the viewing.
     */
    public void pausePlayback() {
        // Doing a thing only and only if the thread is playing.
        if (isPlaying) {
            // Stopping thread.
            threadStop = true;

            // The thread was paused.
            wasPaused = true;
        }
    }

    /**
     * This function is built to decrease the sleeping time. It causes the play to play faster.
     */
    public void acceleratePlayback() {
        sleepingTime /= 2;
        System.out.println("sleepingtime = " + sleepingTime);
    }

    /**
     * Skip to the next frame while in Frame by frame mode and
     * fastfast forward in Real time mode.
     */
    public void fastForwardPlayback() {
        // Frame by frame mode :
        if (viewMode.equals(ViewMode.FrameByFrame) && !isPlaying && backEnd.firstDraggable() != null) {
            this.iGlobalCursor++;
            // Checking if there is a new frame to create.
            if (customTimeLine.getCurrentFrame().equals(customTimeLine.getNumberOfFrames())) {
                int maxFrame = backEnd.getMaxFrameByFrame();
                    backEnd.doubleLastEntry(-100, -1, sGlobalmode);

                // Setting the frame.
                customTimeLine.nextFrame();

                // Incremente.
                nbSecondes++;

                // Atteint la prochaine minute?
                if (nbSecondes % 60 == 0)
                {
                    // New minute.
                    nbMinutes++;

                    // Reset secondes.
                    nbSecondes++;
                }

                // Ajout du mode

            }
            // The frame must be change.
            else {
                customTimeLine.setCurrentFrame(customTimeLine.getCurrentFrame() + 1);
                while (true) {
                    if (backEnd.firstDraggable().getlPositions().get(iGlobalCursor).mode.equals("F"))
                        break;
                    this.iGlobalCursor++;
                }
                // Setting new positions if they exist.
                backEnd.readTriple(this.iGlobalCursor);
            }

            // Ajusting text and timeline.
            customTimeLine.ajustProgress();
            labelTimeleft.setText(customTimeLine.getCurrentFrame() + " / " + customTimeLine.getNumberOfFrames());

            // Redraw canvas.
            drawCurrentPlayOnCanvas();

            if (customTimeLine.getCurrentFrame() != 0) {
                treeViewOthers.setDisable(true);
            } else {
                treeViewOthers.setDisable(false);
            }

            //update timestamp
            backEnd.currentPlay.setCurrentTimestamp(
                    new Timestamp(backEnd.currentPlay.getCurrentTimestamp().getTime() + 1));

            // Set the players back to lower saturation.
            backEnd.currentPlay.setPlayersWithLowerSaturation();
            // Erase the ghost of players.
            backEnd.currentPlay.eraseGhosts();
            drawCurrentPlayOnCanvas();
        }
        // Realtime mode :
        else {

        }
    }

    // TODO : Ouvrir un dialog pour que l'utilisateur indique l'endroit de la sauvegarde.
    public void saveSessionUnder() {
        // Shows a dialog to get user's desired save location.
    }

    public void exportPlay() {
        if (backEnd.currentPlay != null)
            this.saveAsPng();
    }

    public void exportBackend() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Visualigue export(*.ser)", "*.ser"));
        File file = fileChooser.showSaveDialog(null);
        if (!file.getName().contains(".")) {
            file = new File(file.getAbsolutePath() + ".ser");
        }


        if (file != null) {
            FileManager fileMngr = FileManager.getInstance();
            fileMngr.saveData(backEnd, file.getAbsolutePath());
        }
    }

    public void importBackend() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("Visualigue exported files (*.ser)", "*.SER");
        fileChooser.getExtensionFilters().addAll(extFilterJPG);
        File file = fileChooser.showOpenDialog(null);

        FileManager fileMngr = FileManager.getInstance();
        //this.backEnd = fileMngr.loadData(file.getAbsolutePath());

        Bridge bridge = Bridge.getInstance().getInstance();
        bridge.importPath = file.getAbsolutePath();
        bridge.hasImportedOnce = true;

        try {
            bridge.main.start(bridge.ps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function is the getter of the canvas' scale.
     *
     * @return The function return a double representing the canvas' scale.
     */
    private double getCanvasScale() {
        return canvasScale.get();
    }

    /**
     * This function is the setter of the canvas' scale.
     *
     * @param canvasScale is a double representing the new canvas's scale.
     */
    private void setCanvasScale(double canvasScale) {
        this.canvasScale.set(canvasScale);
    }

    /**
     * This method determine if the user as the right to modify the scale.
     *
     * @param newScale The new scale to be checked.
     * @return A double representing the right new scale is returned.
     */
    private double rightScale(double newScale) {
        // Return value.
        double rightReturnValue;
        rightReturnValue = newScale;

        // Is the new scale superior to the maximum?
        if (newScale > MAX_SCALE) {
            rightReturnValue = MAX_SCALE;
        }
        // Is the new scale inferior to the minimum?
        else if (newScale < MIN_SCALE) {
            rightReturnValue = MIN_SCALE;
        }

        // Return.
        return rightReturnValue;
    }

    /**
     * This function is moving the canvas to his new position.
     *
     * @param newAbscissa is the new abscissa of the canvas.
     * @param newOrdered  is the new ordered of the canvas.
     */
    private void setCanvasNewPosition(double newAbscissa, double newOrdered) {
        canvasField.setTranslateX(newAbscissa + scrollPaneForCanvas.getWidth() / 1.5);
        canvasField.setTranslateY(newOrdered + scrollPaneForCanvas.getHeight() / 3);
    }

    /**
     * This function will allow the user the zoom via the mouse scroll.
     *
     * @param scrollEvent is the event when the user is zooming.
     */
    private void onScrollEvent(ScrollEvent scrollEvent) {
        // Defining the default delta which will be used each time there is a zoom and unzoom.
        double delta = 1.03;

        // Getting localy the new scale and the old scale.
        double newScale = getCanvasScale();
        double oldScale = newScale;

        // Unzoom
        if (scrollEvent.getDeltaY() < 0) {
            newScale /= delta;
        }
        // Zoom
        else {
            newScale *= delta;
        }

        // Verify is the new scale is ok.
        newScale = rightScale(newScale);

        // Setting the new scale.
        if (newScale != oldScale) {
            setCanvasScale(newScale);
        }

        // Getting the map points.
        double xCenter = canvasField.getWidth() / 2;
        double yCenter = canvasField.getHeight() / 2;
        double xPoint = scrollEvent.getSceneX();
        double yPoint = scrollEvent.getSceneY();

        // Determine the difference in X and Y.
        double dx = Math.abs(xCenter - xPoint);
        double dy = Math.abs(yCenter - yPoint);

        // Determine where is the new center.
        // Case where it is already the center.
        if (dx == 0 && dy == 0) {
            setCanvasNewPosition(canvasField.getTranslateX(), canvasField.getTranslateY());
        }
        // Case where the point is in the third frame compared to canvas field center.
        else if (xPoint <= xCenter && yPoint <= yCenter) {
            setCanvasNewPosition(dx, dy);
        }
        // Case where the point is in the forth frame compared to canvas field center.
        else if (xPoint >= xCenter && yPoint <= yCenter) {
            setCanvasNewPosition(-dx, dy);
        }
        // Case where the point is in the first frame compared to canvas field center.
        else if (xPoint >= xCenter && yPoint >= yCenter) {
            setCanvasNewPosition(-dx, -dy);
        }
        // Case where the point is in the second frame compared to canvas field center.
        else if (xPoint <= xCenter && yPoint >= yCenter) {
            setCanvasNewPosition(dx, -dy);
        }

        // Suppress the event.
        scrollEvent.consume();
    }

    private void modifySport(MouseEvent event, Sport sport) {

        if (event.getButton().equals(MouseButton.SECONDARY)) {
            // Finding the sport in the list.
            for (Sport s : backEnd.lSportList) {
                if (s.getName().equals(sport.getName())) {
                    sport = s;
                }
            }
            // Opening the dialog :
            TeamSizeEditDialog dialog = new TeamSizeEditDialog(String.valueOf(sport.getNumberOfPlayersPerTeam()));
            dialog.showAndWait();
            // Changing the number of player of the sport.
            sport.setNumberOfPlayersPerTeam(Integer.parseInt(dialog.getTeamSizeTextField()));
        }

    }

    /**
     * TODO : Ajouter au DC.
     * Delete a sport from the list.
     *
     * @param sport Sport to delete
     */
    private void deleteSport(Sport sport, CustomTimeLine customTimeLine) {
        backEnd.deleteSport(sport, customTimeLine);
        initPlayList();
        drawCurrentPlayOnCanvas();
    }

    /**
     * TODO : Ajouter au DC.
     * Delete a play from the list.
     *
     * @param play Play to delete
     */
    private void deletePlay(Play play) {
        backEnd.deletePlay(play);
        customTimeLine.reset();
        initPlayList();
        drawCurrentPlayOnCanvas();
    }

    private void undoWrapper() {

        backEnd.undoAction();
        System.out.println("butts");
        backEnd.readTriple(iGlobalCursor);
        drawCurrentPlayOnCanvas();
    }

    private void redoWrapper() {

        backEnd.redoAction();
        backEnd.readTriple(iGlobalCursor);
        drawCurrentPlayOnCanvas();
    }

}
