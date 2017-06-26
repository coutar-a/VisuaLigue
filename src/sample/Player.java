package sample;

import javafx.collections.FXCollections;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Charles on 11/10/2016.
 */
public class Player extends DraggableObject implements Serializable {

    /* - Attributes - */
    private String playerName = null;
    private double rotation = 0;

    public Player(String sRole, int iPosX, int iPosY, Image imagePlayer, String sImagePlayer, Integer ID, Timestamp t) {
        super(sRole, iPosX, iPosY, imagePlayer, sImagePlayer, ID, t);
    }

    public Player(Player playerToCopy) {
        super(playerToCopy.getsName(), playerToCopy.getiPosX(), playerToCopy.getiPosY(), playerToCopy.getImage(), playerToCopy.getsImagePath(), playerToCopy.getObjectId(), playerToCopy.getTimestamp());
    }

    public Player(String sRole, Image iImagePlayer, String sImagePath, Timestamp t) {
        super(sRole, 0, 0, iImagePlayer, sImagePath, 0, t);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (sImagePath != null && !sImagePath.equals(""))
            image = new Image(sImagePath);
        //os.defaultWriteObject(); //serialization of the current serializable fields
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}
