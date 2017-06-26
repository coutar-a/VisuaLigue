package sample;


import javafx.collections.FXCollections;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Charles on 10/10/2016.
 */
public class GameObjective extends DraggableObject implements Serializable {

//    public String sImagePath;

    public GameObjective(String sName, int iPosX, int iPosY, Image image, String sImagePath, Timestamp t) {
        super(sName, iPosX, iPosY, image, sImagePath, 0, t);
        this.sImagePath = sImagePath;
    }

    public GameObjective(String sName, int iPosX, int iPosY, Image image, String sImagePath, Timestamp t, double dScale) {
        super(sName, iPosX, iPosY, image, sImagePath, 0, t);
        this.sImagePath = sImagePath;
        scale = dScale;
    }

    // TODO
    public  GameObjective(GameObjective other) {
        super(other.getsName(), other.getiPosX(), other.getiPosY(), other.getImage(), other.getsImagePath(), other.getObjectId(), other.getTimestamp());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        if (this.sImagePath != null && !this.sImagePath.equals(""))
            image = new Image(this.sImagePath);
    }
}
