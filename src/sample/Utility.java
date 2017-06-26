package sample;

import java.awt.*;

/**
 * Created by Charles on 14/12/2016.
 */
public final class Utility {

    private Utility() {};

    public static float getAngle(Point.Double target, Point.Double otherTarget) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - otherTarget.y, target.x - otherTarget.x));

        if(angle < 0){
            angle += 360;
        }
        return angle;
    }


}
