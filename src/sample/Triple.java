package sample;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Charles on 2016-11-04.
 * <p>
 * Description : This is a custom 3 member tuple used in DraggableObject to save positions at a given moment in time
 */
public class Triple  implements Serializable {

    public Timestamp timeStamp;
    public double x;
    public double y;
    public String mode;

    public Triple(double new_x, double new_y, Timestamp new_timeStamp) {
        x = new_x;
        y = new_y;
        timeStamp = new_timeStamp;
        mode = "";
    }

    public Triple(double new_x, double new_y, Timestamp new_timeStamp, String new_mode) {
        x = new_x;
        y = new_y;
        timeStamp = new_timeStamp;
        mode = new_mode;
    }

}
