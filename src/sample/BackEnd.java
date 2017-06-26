package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;

import javafx.scene.image.Image;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Charles on 09/10/2016.
 */

enum tokenType {
    player, objective, obstacle
}
public class BackEnd implements Serializable {

    /* - Attributes - */
    public transient ObservableList<Play> lPlayList;
    public transient ObservableList<Sport> lSportList;
    public transient ObservableList<GameObjective> lGameObjective;
    public transient ObservableList<Obstacle> lObstacles;
    public Play currentPlay;
    public DraggableObject selectedObject;
    public DraggableObject grabbedObject;
    public DraggableObject rotatedObject;
    private HashMap<String, ArrayList> serializedData;
    public Timestamp preStartTimestamp;
    public transient ArrayList<OperationRecord> pastOperations;
    public transient ArrayList<OperationRecord> undoneOperations;
    /* - Constructor - */
    public BackEnd() {
        /* Initializing the attributes : */
        selectedObject = null;
        grabbedObject = null;
        rotatedObject = null;
        this.preStartTimestamp = new Timestamp(System.currentTimeMillis());
        // Initialize the lists.
        lPlayList = loadPlayList();
        lSportList = loadSportsList();
        lGameObjective = loadGameObjectivesList();
        lObstacles = loadObstaclesList();
        pastOperations = new ArrayList<OperationRecord>();
        undoneOperations = new ArrayList<OperationRecord>();
    }

    /*
     * Called when serialization process is active
     * Saves the transient field to serializable member variables.
     */
    public void helpSerializable() {
        serializedData = new HashMap<String, ArrayList>();

        ArrayList lTempPlayList = new ArrayList();
        for (Play p : this.lPlayList) {
            lTempPlayList.add(p);
        }
        serializedData.put("lPlayList", lTempPlayList);

        ArrayList lTempSportList = new ArrayList();
        for (Sport s : this.lSportList) {
            lTempSportList.add(s);
        }
        serializedData.put("lSportList", lTempSportList);


        ArrayList lTempGameObjectivetList = new ArrayList();
        for (GameObjective g : this.lGameObjective) {
            lTempGameObjectivetList.add(g);
        }
        serializedData.put("lGameObjective", lTempGameObjectivetList);


        ArrayList lTempObstacletList = new ArrayList();
        for (Obstacle o : this.lObstacles) {
            lTempObstacletList.add(o);
        }
        serializedData.put("lObstacles", lTempObstacletList);

    }

    /*
     * Called when deserialization process is active
     * Copy the data of the saved members back to the transient field
     */
    public void helpDeserializable() {
        lPlayList = FXCollections.observableArrayList(serializedData.get("lPlayList"));
        lSportList = FXCollections.observableArrayList(serializedData.get("lSportList"));
        lGameObjective = FXCollections.observableArrayList(serializedData.get("lGameObjective"));
        lObstacles = FXCollections.observableArrayList(serializedData.get("lObstacles"));
    }

    //Automatically called by JVM when deserialisation is happening
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            in.defaultReadObject();
            currentPlay = null;
        }
        catch(IOException i)
        {
            System.out.println(i.getMessage());
        }
        catch(ClassNotFoundException c)
        {
            System.out.println(c.getMessage());
        }
    }

    /* Prepare the play list to receive new plays
     * added by the user. */
    public ObservableList<Play> loadPlayList() {
        return FXCollections.observableArrayList();
    }

    /* Fills the sport list on application start.
     * The sports are then availaible when creating new plays. */
    public ObservableList<Sport> loadSportsList() {
        ObservableList<Sport> list = FXCollections.observableArrayList();
        // Creating Basic Hockey Sport.
        ArrayList<Player> roleList = new ArrayList<Player>();
        roleList.add(new Player("Guardien", new Image("drawable/blue_circle.png"), "drawable/blue_circle.png", (this.currentPlay == null) ? null : this.currentPlay.getTimestampZero()));
        roleList.add(new Player("Défenseur", new Image("drawable/blue_circle.png"), "drawable/blue_circle.png", (this.currentPlay == null) ? null : this.currentPlay.getTimestampZero()));
        roleList.add(new Player("Aillier", new Image("drawable/blue_circle.png"), "drawable/blue_circle.png", (this.currentPlay == null) ? null : this.currentPlay.getTimestampZero()));
        roleList.add(new Player("Centre", new Image("drawable/blue_circle.png"), "drawable/blue_circle.png", (this.currentPlay == null) ? null : this.currentPlay.getTimestampZero()));
        Sport sport1 = new Sport("Hockey", new Image("drawable/hockey_small.png"),
                new GameObjective("Rondelle", 0, 0, new Image("drawable/hockey_puck.png"), "drawable/hockey_puck.png", (this.currentPlay == null) ? this.preStartTimestamp : this.currentPlay.getTimestampZero(), 1),
                "description", roleList, 2, 7, new Image("drawable/hockey.png"), "drawable/hockey.png", 61, 30, "m");
        // Adding sports to the list.
        list.add(sport1);
        return list;
    }

    /* Fills the game Objective list on application start.
     * The game objectives are then availaible when creating new sports. */
    public ObservableList<GameObjective> loadGameObjectivesList() {
        ObservableList<GameObjective> list = FXCollections.observableArrayList();
        // Adding a few pre-created game objectives.
        list.add(new GameObjective("Ballon (Basket)", 0, 0, new Image("drawable/basketball_ball.png"), "drawable/basketball_ball.png", (this.currentPlay == null) ? this.preStartTimestamp : this.currentPlay.getTimestampZero(), 1));
        list.add(new GameObjective("Rondelle", 0, 0, new Image("drawable/hockey_puck.png"), "drawable/hockey_puck.png", (this.currentPlay == null) ? this.preStartTimestamp : this.currentPlay.getTimestampZero(), 1));
        list.add(new GameObjective("Ballon (Soccer)", 0, 0, new Image("drawable/soccer_ball.png"), "drawable/soccer_ball.png", (this.currentPlay == null) ? this.preStartTimestamp : this.currentPlay.getTimestampZero(), 1));

        return list;
    }

    /* Fills the obstacles list on application start.
     * The obstacles are then availaible when creating new plays
     * for any sport. */
    public ObservableList<Obstacle> loadObstaclesList() {
        ObservableList<Obstacle> list = FXCollections.observableArrayList();
        // Adding a pre-created obstacle.
        list.add(new Obstacle("Cône", 0, 0, new Image("drawable/cone.png"), "drawable/cone.png", (this.currentPlay == null) ? this.preStartTimestamp : this.currentPlay.getTimestampZero(), 1));
        return list;
    }

    /**
     * Create a new 'Play' and adds it to the list of existing plays.
     *
     * @param title       String : Title of the play.
     * @param sport       Sport : Sport associated with the play.
     * @param description String : Short description of the play.
     */
    public void createNewPlay(String title, Sport sport, String description) {
        lPlayList.add(new Play(title, sport, description, this.preStartTimestamp));
    }

    /**
     * Create a new 'Sport' and add it to the list of existing sports.
     *
     * @param name            String : Name of the sport.
     * @param objective       GameObjective : Objective of the game (i.e: Ball, Puck, etc.)
     * @param description     String : Short description of the new sport.
     * @param playerTypes     List<Player> : List of available positions for the sport.
     * @param gameFieldImage  Image : Image to represent the game field.
     * @param gameFieldLength Integer : Length of the game field.
     * @param gameFieldWidth  Integer : Width of the game field.
     * @param gameFieldUnits  String : Type of units to measure the game field with. (i.e : "meters", "yards", etc.)
     */
    public void createNewSport(String name,
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

        lSportList.add(new Sport(name,
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
                gameFieldUnits));
    }

    public void createNewGameObjective(String name, Image image, String imagePath, double scale) {
        System.out.println("Added new game objective to BackEnd");
        lGameObjective.addAll(new GameObjective(name, 0, 0, image, imagePath, preStartTimestamp, scale));
    }

    // Create a new obstacle and add it to the list of obstacles.
    public void createNewObstacle(String obstacleName, Image obstacleImage, String sObstacleImagePath, double scale) {
        lObstacles.addAll(new Obstacle(obstacleName, 0, 0, obstacleImage, sObstacleImagePath, null, scale));
        // TODO Faite refresh la listview après l'ajout
    }


    public void doubleLastEntry(int id, int size, String mode)
    {
        //player
        for (int i = 0; i < currentPlay.getlPlayersSize(); i++) {
            if ((size == -1 || currentPlay.getlPlayers().get(i).getlPositions().size() < size) && currentPlay.getlPlayers().get(i).getObjectId() != id) {

                double x = currentPlay.getlPlayers().get(i).getlPositions().get(currentPlay.getlPlayers().get(i).getlPositions().size() - 1).x;
                double y = currentPlay.getlPlayers().get(i).getlPositions().get(currentPlay.getlPlayers().get(i).getlPositions().size() - 1).y;
                Timestamp timestamp = currentPlay.getlPlayers().get(i).getlPositions().get(currentPlay.getlPlayers().get(i).getlPositions().size() - 1).timeStamp;
                String sMode = mode;

                currentPlay.getlPlayers().get(i).addPosition(x, y, timestamp, sMode);
            }
        }

        //obstacle
        for (int i = 0; i < currentPlay.getlObstaclesSize(); i++) {
            if ((size == -1 || currentPlay.getlObstacles().get(i).getlPositions().size() < size) &&  currentPlay.getlObstacles().get(i).getObjectId() != id) {

                double x = currentPlay.getlObstacles().get(i).getlPositions().get(currentPlay.getlObstacles().get(i).getlPositions().size() - 1).x;
                double y = currentPlay.getlObstacles().get(i).getlPositions().get(currentPlay.getlObstacles().get(i).getlPositions().size() - 1).y;
                Timestamp timestamp = currentPlay.getlObstacles().get(i).getlPositions().get(currentPlay.getlObstacles().get(i).getlPositions().size() - 1).timeStamp;
                String sMode = mode;

                currentPlay.getlObstacles().get(i).addPosition(x, y, timestamp, sMode);

            }
        }

        //gameobjective
        for (int i = 0; i < currentPlay.getlGameObjectivesSize(); i++) {
            if ( (size == -1 || currentPlay.getlGameObjectives().get(i).getlPositions().size() < size) && currentPlay.getlGameObjectives().get(i).getObjectId() != id) {
                double x = currentPlay.getlGameObjectives().get(i).getlPositions().get(currentPlay.getlGameObjectives().get(i).getlPositions().size() - 1).x;
                double y = currentPlay.getlGameObjectives().get(i).getlPositions().get(currentPlay.getlGameObjectives().get(i).getlPositions().size() - 1).y;
                Timestamp timestamp = currentPlay.getlGameObjectives().get(i).getlPositions().get(currentPlay.getlGameObjectives().get(i).getlPositions().size() - 1).timeStamp;
                String sMode = mode;

                currentPlay.getlGameObjectives().get(i).addPosition(x, y, timestamp, sMode);
            }
        }
    }

    public void updatePlayer(int id, double iPosX, double iPosY, String sMode, int tripleIndex) {

        for (int i = 0; i < currentPlay.getlPlayersSize(); i++) {
            if (currentPlay.getlPlayers().get(i).getObjectId() == id) {
                try
                {
                    currentPlay.getlPlayers().get(i).getlPositions().get(tripleIndex).x = iPosX;
                    currentPlay.getlPlayers().get(i).getlPositions().get(tripleIndex).y = iPosY;
                    currentPlay.getlPlayers().get(i).getlPositions().get(tripleIndex).mode = sMode;
                    doubleLastEntry(id, currentPlay.getlPlayers().get(i).getlPositions().size(), sMode);
                    break;
                }
                catch(Exception e)
                {
                    currentPlay.getlPlayers().get(i).addPosition(iPosX, iPosY, this.currentPlay.getCurrentTimestamp(), sMode);
                    doubleLastEntry(id, currentPlay.getlPlayers().get(i).getlPositions().size(), sMode);
                    break;
                }
            }
        }
    }

    public void updateObstacle(int id, double iPosX, double iPosY, String sMode, int tripleIndex) {

        for (int i = 0; i < currentPlay.getlObstaclesSize(); i++) {
            if (currentPlay.getlObstacles().get(i).getObjectId() == id) {

                try
                {
                    currentPlay.getlObstacles().get(i).getlPositions().get(tripleIndex).x = iPosX;
                    currentPlay.getlObstacles().get(i).getlPositions().get(tripleIndex).y = iPosY;
                    currentPlay.getlObstacles().get(i).getlPositions().get(tripleIndex).mode = sMode;
                    doubleLastEntry(id, currentPlay.getlObstacles().get(i).getlPositions().size(), sMode);
                    break;
                }
                catch(Exception e)
                {
                    currentPlay.getlObstacles().get(i).addPosition(iPosX, iPosY, this.currentPlay.getCurrentTimestamp(), sMode);
                    doubleLastEntry(id, currentPlay.getlObstacles().get(i).getlPositions().size(), sMode);
                    break;
                }
            }
        }
    }

    public void updateGameObjective(int id, double iPosX, double iPosY, String sMode, int tripleIndex) {

        for (int i = 0; i < currentPlay.getlGameObjectivesSize(); i++) {
            if (currentPlay.getlGameObjectives().get(i).getObjectId() == id) {
                try
                {
                    currentPlay.getlGameObjectives().get(i).getlPositions().get(tripleIndex).x = iPosX;
                    currentPlay.getlGameObjectives().get(i).getlPositions().get(tripleIndex).y = iPosY;
                    currentPlay.getlGameObjectives().get(i).getlPositions().get(tripleIndex).mode = sMode;
                    doubleLastEntry(id, currentPlay.getlGameObjectives().get(i).getlPositions().size(), sMode);
                    break;
                }
                catch(Exception e)
                {
                    currentPlay.getlGameObjectives().get(i).addPosition(iPosX, iPosY, this.currentPlay.getCurrentTimestamp(), sMode);
                    doubleLastEntry(id, currentPlay.getlGameObjectives().get(i).getlPositions().size(), sMode);
                    break;
                }

            }
        }
    }

    public String readTriple(int i)
    {
        String mode = "";

        // Setting the i-th frame players' position.
        for (int j = 0; j < currentPlay.getlPlayersSize(); j++) {
            // Getting the player to move.
            Player playerToMove = currentPlay.getlPlayers().get(j);

            double newX = playerToMove.getlPositions().get(i).x;
            double newY = playerToMove.getlPositions().get(i).y;

            if (newX == -1 && newY == -1)
            {
                newX = playerToMove.getlPositions().get(i - 1).x ;
                newY = playerToMove.getlPositions().get(i - 1).y;

                System.out.println("x de merde before = " + playerToMove.getlPositions().get(i).x);

                playerToMove.getlPositions().get(i).x = newX;
                playerToMove.getlPositions().get(i).y = newY;

                System.out.println("x de merde after = " + playerToMove.getlPositions().get(i).x);
            }

            // Updating the positions.
            playerToMove.setiPosX(newX);
            playerToMove.setiPosY(newY);

            // Return.
            mode = playerToMove.getlPositions().get(i).mode;
        }

        // Setting the i-th frame obstacles' position.
        for (int j = 0; j < currentPlay.getlObstaclesSize(); j++) {
            // Getting the obstacle.
            Obstacle obstacleToMove = currentPlay.getlObstacles().get(j);

            // Looking if there is a position for the i-th frame.

            // Getting new positions.
            double newX = obstacleToMove.getlPositions().get(i).x;
            double newY = obstacleToMove.getlPositions().get(i).y;

            if (newX == -1 && newY == -1)
            {
                newX = obstacleToMove.getlPositions().get(i - 1).x ;
                newY = obstacleToMove.getlPositions().get(i - 1).y;

                System.out.println("x de merde before = " + obstacleToMove.getlPositions().get(i).x);

                obstacleToMove.getlPositions().get(i).x = newX;
                obstacleToMove.getlPositions().get(i).y = newY;

                System.out.println("x de merde after = " + obstacleToMove.getlPositions().get(i).x);
            }

            // Setting the new postions.
            obstacleToMove.setiPosX(newX);
            obstacleToMove.setiPosY(newY);

            // Return.
            mode = obstacleToMove.getlPositions().get(i).mode;
        }


        System.out.println("salperire = " + currentPlay.getlGameObjectivesSize());

        // Setting the i-th frame game objectives' position.
        for (int j = 0; j < currentPlay.getlGameObjectivesSize(); j++) {
            // Getting the obstacle.
            GameObjective gameObjectiveToMove = currentPlay.getlGameObjectives().get(j);

            // Getting new positions.
            double newX = gameObjectiveToMove.getlPositions().get(i).x;
            double newY = gameObjectiveToMove.getlPositions().get(i).y;
            System.out.println("x de merde before = " + gameObjectiveToMove.getlPositions().get(i).x);

            if (newX == -1 && newY == -1)
            {
                newX = gameObjectiveToMove.getlPositions().get(i - 1).x ;
                newY = gameObjectiveToMove.getlPositions().get(i - 1).y;



                gameObjectiveToMove.getlPositions().get(i).x = newX;
                gameObjectiveToMove.getlPositions().get(i).y = newY;

                System.out.println("x de merde after = " + gameObjectiveToMove.getlPositions().get(i).x);
            }

            // Setting the new postions.
            gameObjectiveToMove.setiPosX(newX);
            gameObjectiveToMove.setiPosY(newY);

            // Return.
            mode = gameObjectiveToMove.getlPositions().get(i).mode;
        }

        // Return.
        return mode;
    }

    //retourne un joueur
    public String setItemsNewPos(long i) {
        // Return variable.
        String mode = "";

        // Getting the timestamp of the i-th frame.
        long iThFrameTimestamp = currentPlay.getTimestampZero().getTime() + i;

        // Setting the i-th frame players' position.
        for (int j = 0; j < currentPlay.getlPlayersSize(); j++) {
            // Getting the player to move.
            Player playerToMove = currentPlay.getlPlayers().get(j);

            // Checking if the player had a different position in the i-th frame.
            for (int k = 0; k <= i && k < playerToMove.getlPositions().size(); k++) {
                // Getting the timestamp of his k-th position.
                long kThPositionTimestamp = playerToMove.getlPositions().get(k).timeStamp.getTime();

                // Case where there was a movement for the player at this time.
                if (kThPositionTimestamp == iThFrameTimestamp) {
                    // Getting new positions.
                    double newX = playerToMove.getlPositions().get(k).x;
                    double newY = playerToMove.getlPositions().get(k).y;

                    // Updating the positions.
                    playerToMove.setiPosX(newX);
                    playerToMove.setiPosY(newY);

                    // Return.
                    mode = playerToMove.getlPositions().get(k).mode;
                }
            }

        }

        // Setting the i-th frame obstacles' position.
        for (int j = 0; j < currentPlay.getlObstaclesSize(); j++) {
            // Getting the obstacle.
            Obstacle obstacleToMove = currentPlay.getlObstacles().get(j);

            // Looking if there is a position for the i-th frame.
            for (int k = 0; k <= i && k < obstacleToMove.getlPositions().size(); k++) {
                // Getting the timestamp of his k-th position.
                long kThPositionTimestamp = obstacleToMove.getlPositions().get(k).timeStamp.getTime();

                // Case where there was a movement for the obstacle at this time.
                if (kThPositionTimestamp == iThFrameTimestamp) {
                    // Getting new positions.
                    double newX = obstacleToMove.getlPositions().get(k).x;
                    double newY = obstacleToMove.getlPositions().get(k).y;

                    // Setting the new postions.
                    obstacleToMove.setiPosX(newX);
                    obstacleToMove.setiPosY(newY);

                    // Return.
                    mode = obstacleToMove.getlPositions().get(k).mode;
                }
            }
        }

        // Setting the i-th frame game objectives' position.
        for (int j = 0; j < currentPlay.getlGameObjectivesSize(); j++) {
            // Getting the obstacle.
            GameObjective gameObjectiveToMove = currentPlay.getlGameObjectives().get(j);

            // Looking if there is a position for the i-th frame.
            for (int k = 0; k <= i && k < gameObjectiveToMove.getlPositions().size(); k++) {
                // Getting the timestamp of his k-th position.
                long kThPositionTimestamp = gameObjectiveToMove.getlPositions().get(k).timeStamp.getTime();

                // Case where there was a movment for the obstacle at this time.
                if (kThPositionTimestamp == iThFrameTimestamp) {
                    // Getting new positions.
                    double newX = gameObjectiveToMove.getlPositions().get(k).x;
                    double newY = gameObjectiveToMove.getlPositions().get(k).y;

                    // Setting the new postions.
                    gameObjectiveToMove.setiPosX(newX);
                    gameObjectiveToMove.setiPosY(newY);

                    // Return.
                    mode = gameObjectiveToMove.getlPositions().get(k).mode;
                }
            }
        }

        // Return.
        return mode;
    }

    public void saveSession() {

        // Saves the session (Plays, sports, etc.).
        FileManager fileMngr = FileManager.getInstance();
        fileMngr.saveData(this);
        fileMngr.saveThumbnails(this); // TODO : Finir la sauvegarde
    }

    /**
     * Saves and exits the application properly.
     */
    public void saveAndExit() {
        Bridge bridge = Bridge.getInstance();

        if (bridge.hasImportedOnce == true)
            bridge.fc.askConfirmationOverideActualSave();
        else {
            this.saveSession();
            Platform.exit(); // Exits the application.
        }
    }

    // TODO : Refait la dernière action de l'utilisateur. Utiliser 'Serializable'.
    public void redoAction() {

        if (undoneOperations.size() < 1)
            return;

        OperationRecord operation = undoneOperations.remove(undoneOperations.size() - 1);

        String mode = operation.object.getlPositions().get(operation.object.getlPositions().size() - 1).mode;


        if (operation.object instanceof Player) {

            System.out.println("current x : " + operation.x + ", y : " + operation.y);

            System.out.println("x : " + operation.x + ", y : " + operation.y);
            updatePlayer(operation.object.getObjectId(), operation.x, operation.y, mode, operation.object.getlPositions().size() - 1);
            System.out.println(operation.object.getiPosX() + ", " + operation.object.getiPosY());
            System.out.println("updating player");
            pastOperations.add(operation);

        } else if (operation.object instanceof GameObjective) {

            updateGameObjective(operation.object.getObjectId(), operation.x, operation.y, mode, operation.object.getlPositions().size() - 1);
            System.out.println("updating objective");
            pastOperations.add(operation);

        } else if (operation.object instanceof Obstacle){

            updateObstacle(operation.object.getObjectId(), operation.x, operation.y, mode, operation.object.getlPositions().size() - 1);
            System.out.println("updating obstacle");
            pastOperations.add(operation);
        }
    }

    // TODO : Annule la dernière action de l'utilisateur. Utiliser 'Serializable'.
    public void undoAction() {


        System.out.println("-------------------------------------------------------------------------------");

        if (pastOperations.size() <= 1)
            return;
        System.out.println("Undo list before : " + pastOperations.size());
        OperationRecord operation = pastOperations.remove(pastOperations.size() - 1);
        System.out.println("undo list after : " + pastOperations.size());

        ArrayList<OperationRecord> objectOps = new ArrayList<>();

        for (OperationRecord opRecord :
             pastOperations) {
            if (opRecord.object.getObjectId() == operation.object.getObjectId()) {
                objectOps.add(opRecord);
            }
        }

        if (objectOps.size() == 0)
            return;

        String mode = operation.object.getlPositions().get(operation.object.getlPositions().size() - 1).mode;


        if (operation.object instanceof Player) {

            System.out.println("current x : " + operation.x + ", y : " + operation.y);

            System.out.println("x : " + objectOps.get(objectOps.size() - 1).x + ", y : " + objectOps.get(objectOps.size() - 1).y);
            updatePlayer(operation.object.getObjectId(), objectOps.get(objectOps.size() - 1).x, objectOps.get(objectOps.size() - 1).y, mode, operation.object.getlPositions().size() - 1);
            System.out.println(operation.object.getiPosX() + ", " + operation.object.getiPosY());
            System.out.println("updating player");
            undoneOperations.add(operation);

        } else if (operation.object instanceof GameObjective) {

            updateGameObjective(operation.object.getObjectId(), objectOps.get(objectOps.size() - 1).x, objectOps.get(objectOps.size() - 1).y, mode, operation.object.getlPositions().size() - 1);
            System.out.println("updating objective");
            undoneOperations.add(operation);

        } else if (operation.object instanceof Obstacle){

            updateObstacle(operation.object.getObjectId(), objectOps.get(objectOps.size() - 1).x, objectOps.get(objectOps.size() - 1).y, mode, operation.object.getlPositions().size() - 1);
            System.out.println("updating obstacle");
            undoneOperations.add(operation);
        }
    }

    // TODO : Fait un zoom avant (utiliser le centre du canvas).
    public void zoomIn() {

    }

    // TODO : Fait un zoom arrière (utiliser le centre du canvas).
    public void zoomOut() {

    }

    // TODO : Ouvre un 'browser' avec la documention du projet. (à voir)
    public void showDocumentation() {

    }

    // TODO : Ouvre un 'browser' avec de l'aide sur l'utilisation de l'application.
    public void showHelp() {

    }

    // Convert pixels to real-life units.
    public Integer[] convertPxtoRLUnits(double x, double y, Canvas canvas) {
        Integer[] fieldSize = currentPlay.getsSport().getGameField().getSize();
        return new Integer[]{(int) (x * fieldSize[0] / (int) canvas.getWidth()), (int) (y * fieldSize[1] / (int) canvas.getHeight())};
    }

    //TODO : implement all following methods

    public void addPlayer(int iPosX, int iPosY) {

    }

    public boolean isClickOnEntity(int x, int y) {
        return true;
    }

    public void setCurrentPlay(Play CurrentPlay) {

    }

    /**
     * This function determine which object has the most number of positions.
     *
     * @return The max number of positions.
     */
    public int getMaxFrameByFrame() {
        // Return variable.
        int frames;
        frames = 0;

        if (currentPlay.getlPlayers().size() == 0)
            return frames;

        // Checking players.
        for (Triple t: currentPlay.getlPlayers().get(0).getlPositions()) {
            if (t.mode.equals("F"))
                frames++;
        }
        return frames - 1;
    }

    /**
     * TODO : Ajouter au DC.
     * Remove a sport from the data.
     *
     * @param sport Name of the sport to remove.
     */
    public void deleteSport(Sport sport, CustomTimeLine customTimeLine) {
        // Setting currentPlay to null if the sport deleted was the one of current play.
        if (currentPlay != null) {
            if (currentPlay.getsSport().getName().equals(sport.getName())) {
                currentPlay = null;
                customTimeLine.reset();

            }
        }
        // Removing the sport.
        for (int i = 0; i < lSportList.size(); ++i) {
            if (lSportList.get(i).getName().equals(sport.getName())) {
                lSportList.remove(i);
            }
        }
        // Removing plays from the sport.
        for (Play play : lPlayList) {
            if (play.getsSport().getName().equals(sport.getName())) {
                lPlayList.remove(play);
            }
        }
    }

    /**
     * TODO : Ajouter au DC.
     * Remove a play from the data.
     *
     * @param play Play to remove.
     */
    public void deletePlay(Play play) {
        // Setting currentPlay to null if the play deleted was the currentPlay
        if (currentPlay.equals(play)) {
            currentPlay = null;
        }
        // Removing the play.
        lPlayList.remove(play);
    }

    public DraggableObject firstDraggable()
    {
        if (currentPlay.getlPlayers().size() != 0 && currentPlay.getlPlayers().get(0).getObjectId() == 0)
            return currentPlay.getlPlayers().get(0);
        else if (currentPlay.getlGameObjectives().size() != 0 && currentPlay.getlGameObjectives().get(0).getObjectId() == 0)
            return currentPlay.getlGameObjectives().get(0);
        else if (currentPlay.getlObstacles().size() != 0 && currentPlay.getlObstacles().get(0).getObjectId() == 0)
            return currentPlay.getlObstacles().get(0);
        return null;
    }

    public  int getNbOfFrameForI(int breakPoint)
    {
        int i = 1;
        int nbOfFrame = 0;
        DraggableObject first = firstDraggable();

        while (i != breakPoint)
        {
            if (first.getlPositions().get(i).mode.equals("F"))
            {
                nbOfFrame++;
            }
            i++;
        }
        return nbOfFrame;
    }
}


