package sample;

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Charles on 10/10/2016.
 */
public class Play implements Serializable {

    /* - Attributes - */
    private String sTitle;
    private Sport sSport;
    private String sDescription;
    private int iIdItem = 0;

    private ArrayList<Player> lPlayers;
    private ArrayList<GameObjective> lGameObjectives;
    private ArrayList<Obstacle> lObstacles;
    private transient ArrayList<Player> lGhosts = new ArrayList<>();
    private Timestamp timestampZero;
    private Timestamp currentTimestamp;
    private transient Image playThumbnail;
    private String sImagePathPlayThumbnail;

    // Ajouter les attributs ici...

    /* - Constructor - */
    public Play(String p_title, Sport p_sport, String p_description, Timestamp preStartTimestamp) {
        // Initializing the attributes.
        sTitle = p_title;
        sSport = p_sport;
        sDescription = p_description;
        lPlayers = new ArrayList<>();
        lGameObjectives = new ArrayList<>();
        lObstacles = new ArrayList<>();
        lGhosts = new ArrayList<>();
        playThumbnail = sSport.getThumbnail();

        timestampZero = preStartTimestamp;
        currentTimestamp = preStartTimestamp;
    }

    /* - Methods - */

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        if (this.sImagePathPlayThumbnail != null && !this.sImagePathPlayThumbnail.equals(""))
            this.playThumbnail = new Image("file:///" + this.sImagePathPlayThumbnail);
    }

    public int getNewId() {
        this.iIdItem++;
        return this.iIdItem - 1;
    }

    public Player getPlayer(int playerID) {
        for (Player player : lPlayers) {
            if (player.getObjectId() == playerID) return player;
        }
        return null;
    }

    // Ajouter les méthodes ici...
    public String getsTitle() {
        return sTitle;
    }

    public ArrayList<Player> getlPlayers() {
        return lPlayers;
    }

    public void setlPlayers(ArrayList<Player> p_players) {
        lPlayers = p_players;
    }

    public void setPlayer(int id, int xPos, int yPos, Timestamp time) {

    }

    public void setObstacle(int id, int xPos, int yPos, Timestamp time) {

    }

    public void addObstacle(int iPosX, int iPosY) {

    }

    public Sport getsSport() {
        return sSport;
    }

    public int getlPlayersSize() {
        return lPlayers.size();
    }

    public int getlObstaclesSize() {
        return lObstacles.size();
    }

    public int getlGameObjectivesSize() {
        return lGameObjectives.size();
    }

    public ArrayList<Player> getlGhosts() {
        return lGhosts;
    }

    public void setlGhosts(ArrayList<Player> lGhosts) {
        this.lGhosts = lGhosts;
    }

    /**
     * TODO : Ajouter au DC.
     * Add a ghost to the ghost list.
     *
     * @param player Ghost of the player to add.
     */
    public void addGhost(double mouseX, double mouseY, Player player) {

        Player newGhost = new Player(player.getPlayerName(), (int) mouseX, (int) mouseY, player.getImage(), "", player.getObjectId(), null);
        newGhost.setRotation(player.getRotation());

        boolean ghostFound = false;
        for (Player ghost : lGhosts) {

            if (ghost.getObjectId() == newGhost.getObjectId()) {
                ghostFound = true;
            }
        }
        if (!ghostFound) {
            lGhosts.add(newGhost);
        }


    }

    /**
     * TODO : Ajouter au DC.
     * Erase the ghosts from this play.
     */
    public void eraseGhosts() {
        if (lGhosts != null) {
            lGhosts.clear();
        }
    }

    public String getsDescription() {
        return sDescription;
    }

    public ArrayList<Obstacle> getlObstacles() {
        return lObstacles;
    }

    public ArrayList<GameObjective> getlGameObjectives() {
        return lGameObjectives;
    }

    public Timestamp getTimestampZero() {
        return timestampZero;
    }

    public void setTimestampZero(Timestamp timestampZero) {
        this.timestampZero = timestampZero;
    }

    public Timestamp getCurrentTimestamp() {
        return currentTimestamp;
    }

    public void setCurrentTimestamp(Timestamp currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }

    public Image getPlayThumbnail() {
        return playThumbnail;
    }

    public void setPlayThumbnail(Image playThumbnail, String sImagePath) {
        this.playThumbnail = playThumbnail;
        this.sImagePathPlayThumbnail = sImagePath;
    }

    public boolean getNewPlayerApproval(String colorPath) {

        int nb_players = 0;

        for (Player player : lPlayers) {
            if (player.getsImagePath() == colorPath)
                nb_players++;
        }
        return (nb_players < sSport.getNumberOfPlayersPerTeam());
    }

    /**
     * TODO : Ajouter au DC.
     * Ajust the boolean attribute for when a player
     * has been dragged / has not been dragged.
     */
    public void setPlayersWithLowerSaturation() {
        for (Player player : getlPlayers()) {
            player.setHasBeenDragged(false);
        }
    }
}

