package sample;

import java.awt.*;

/**
 * Created by Ambroise on 08/12/2016.
 */

enum operationType {
    creation, movement
}
public class OperationRecord {

    public DraggableObject object;
    public double x;
    public double y;
    public operationType operationType;

    public OperationRecord(DraggableObject object,
                           double originX,
                           double originY,
                           operationType operation) {

        this.object = object;
        x = originX;
        y = originY;
        operationType = operation;
    }
}
