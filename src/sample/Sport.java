package sample;

import javafx.collections.FXCollections;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Charles on 10/10/2016.
 */
public class Sport implements Serializable {

    /* - Attributes - */
    private String name;
    private transient Image thumbnail;
    private String sImagePath;
    private GameObjective objective;
    private String description;
    private transient List<Player> playerTypes; // TODO : Revoir la structure de donnée ensemble.
    public ArrayList lSerializedPlayers;
    private GameField gameField;
    private int numberOfTeams;
    private int numberOfPlayersPerTeam;
    private boolean deleted = false;
    // Ajouter les attributs ici...

    private void writeObject(ObjectOutputStream os) throws IOException {
         //add behavior here

        lSerializedPlayers = new ArrayList();
        lSerializedPlayers.addAll(playerTypes);
        os.defaultWriteObject(); //serialization of the current serializable fields
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        playerTypes = FXCollections.observableList(this.lSerializedPlayers);
        thumbnail = new Image(sImagePath);
        //os.defaultWriteObject(); //serialization of the current serializable fields
    }

    /* - Constructor - */
    //public Sport(String p_name, Image p_thumbnail, GameObjective p_objective, String p_description, List<Player> p_playerRole, Image p_terrainImage, String sGameFiledImagePath, Integer p_terrainLength, Integer p_terrainWidth, String p_unit) {
    public Sport(String p_name, Image p_thumbnail, GameObjective p_objective, String p_description, List<Player> p_playerRole, int p_numberOfTeam, int p_numberOfPlayersPerTeam, Image p_terrainImage, String sGameFiledImagePath, Integer p_terrainLength, Integer p_terrainWidth, String p_unit) {
        // Initializing the attributes.
        name = p_name;
        thumbnail = p_thumbnail;
        sImagePath = sGameFiledImagePath;
        objective = p_objective;
        description = p_description;
        playerTypes = p_playerRole;
        numberOfTeams = p_numberOfTeam;
        numberOfPlayersPerTeam = p_numberOfPlayersPerTeam;
        gameField = new GameField(p_terrainImage, sImagePath, p_terrainLength, p_terrainWidth, p_unit);
    }

    public Sport(String p_name) {
        name = p_name;
        thumbnail = null;
        sImagePath = null;
        objective = null;
        description = null;
        playerTypes = null;
        numberOfTeams = 0;
        numberOfPlayersPerTeam = 0;
        gameField = null;
    }

    /* - Methods - */
    public String getName() {
        return name;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public GameObjective getObjective() {
        return objective;
    }

    public List<Player> getPlayerTypes() {
        return playerTypes;
    }

    public GameField getGameField() {
        return gameField;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the number of team.
     *
     * @return int : number of teams.
     */
    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    /**
     * Returns the number of players per team.
     *
     * @return int : Max number of players per team.
     */
    public int getNumberOfPlayersPerTeam() {
        return numberOfPlayersPerTeam;
    }
    // Ajouter les méthodes ici...


    public void setNumberOfPlayersPerTeam(int numberOfPlayersPerTeam) {
        this.numberOfPlayersPerTeam = numberOfPlayersPerTeam;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }
}
