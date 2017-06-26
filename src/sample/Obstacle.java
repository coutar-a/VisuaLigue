package sample;

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Charles on 11/10/2016.
 */



public class Obstacle extends DraggableObject implements Serializable {

//    public String sImagePath;

    public Obstacle(String sName, int iPosX, int iPosY, Image image, String sImagePath, Timestamp t) {
        super(sName, iPosX, iPosY, image, sImagePath, 0, t);
        this.sImagePath = sImagePath;
    }

    public Obstacle(String sName, int iPosX, int iPosY, Image image, String sImagePath, Timestamp t, double dScale) {
        super(sName, iPosX, iPosY, image, sImagePath, 0, t);
        this.sImagePath = sImagePath;
        scale = dScale;
    }

    public Obstacle(Obstacle other) {
        super(other.getsName(), other.getiPosX(), other.getiPosY(), other.getImage(), other.getsImagePath(), other.getObjectId(), other.getTimestamp());
    }

    public Obstacle(String sName, Image image, String sObstacleImagePath, Timestamp t) {
        super(sName, 0, 0, image, sObstacleImagePath, 0, t);
        this.sImagePath = sObstacleImagePath;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.sImagePath != null)
            image = new Image(this.sImagePath);
    }
}
