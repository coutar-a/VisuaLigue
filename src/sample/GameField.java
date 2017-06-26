package sample;

import javafx.collections.FXCollections;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Created by Charles on 2016-10-11.
 */
public class GameField implements Serializable {

    /* - Attributes - */
    private String unit;
    private Integer width;
    private Integer height;
    private transient Image fieldImage;
    private String sImagePath;

    /* - Constructor - */
    public GameField(Image p_fieldImage, String p_imagePath, Integer p_length, Integer p_width, String p_unit) {
        this.fieldImage = p_fieldImage;
        this.sImagePath = p_imagePath;
        this.width = p_length;
        this.height = p_width;
        this.unit = p_unit;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        fieldImage = new Image(sImagePath);
    }

    public Integer[] getSize() {
        return new Integer[] {width, height};

    }

    /* - Methods - */
    public Image getFieldImage() { return fieldImage; }

    public String getUnit() { return unit; }

    // Ajouter les méthodes ici...
}
