package sample;

import javafx.scene.image.Image;

import java.awt.geom.Ellipse2D;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Charles on 13/10/2016.
 */


public abstract class DraggableObject implements Serializable {

    protected String sName;
    protected double iPosX = 0;
    protected double iPosY = 0;
    protected transient Image image;
    protected String sImagePath;
    protected List<Triple> lPositions;
    protected int objectId;
    protected double scale;
    protected boolean hasCollision;
    protected boolean hasBeenDragged;
    protected boolean isOverOtherDraggable;

    public  DraggableObject()
    {

        //int new_x, int new_y, Timestamp new_timeStamp) {

    }

    public DraggableObject(String sName, double iPosX, double iPosY, Image image, String sImagePath, int id, Timestamp t) {
        this.sName = sName;
        this.iPosX = iPosX;
        this.iPosY = iPosY;
        this.image = image;
        this.sImagePath = sImagePath;
        this.objectId = id;
        this.lPositions = new ArrayList<Triple>();
        lPositions.add(new Triple(iPosX, iPosY, t, "F"));
        this.hasBeenDragged = false;
        this.isOverOtherDraggable = false;
    }

    public DraggableObject(DraggableObject other) {
        this.sName = other.getsName();
        this.iPosX = other.getiPosX();
        this.iPosY = other.getiPosY();
        this.image = other.getImage();
        this.sImagePath = other.getsImagePath();
        this.objectId = other.getObjectId();
        this.lPositions = other.getlPositions();
        this.lPositions.add(new Triple(iPosX, iPosY, other.getTimestamp(), "F"));
        this.hasBeenDragged = false;
        this.isOverOtherDraggable = false;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public void setiPosX(double iPosX) {
        this.iPosX = iPosX;
    }

    public void setiPosY(double iPosY) {
        this.iPosY = iPosY;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setsImagePath(String sImagePath)
    {
        this.sImagePath = sImagePath;
    }

    public String getsImagePath()
    {
        return (this.sImagePath);
    }

    public void setId (int newId) {
        objectId = newId;
    }

    public void addPosition(double x, double y, Timestamp time, String mode) {
        lPositions.add(new Triple(x, y, time, mode));
        this.setiPosX(x);
        this.setiPosY(y);
    }

    public void addPositionFake(double x, double y, Timestamp time, String mode) {
        lPositions.add(new Triple(x, y, time, mode));
     }

    public String getsName() {

        return sName;
    }

    public double getiPosX() {
        return iPosX;
    }

    public double getiPosY() {
        return iPosY;
    }

    public Image getImage() {
        return image;
    }

    public int getObjectId() {
        return objectId;
    }

    public List<Triple> getlPositions() {
        return lPositions;
    }

    // Returns the 'physical' boundaries of the object drawned on canvas.
    public Ellipse2D getBoundary() {
        return new Ellipse2D.Double(getiPosX() - 15 , getiPosY() - 15, 30, 30);
    }

    public Ellipse2D getRotationBoundary() {
        return new Ellipse2D.Double(getiPosX() - 50 , getiPosY() - 50, 100, 100);
    }

    public Timestamp getTimestamp()
    {
        return lPositions.get(lPositions.size() - 1).timeStamp;
    }

    public String toString() { return sName + " " + iPosX + " " + iPosY + " " + image + " "+ objectId;}

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public boolean isHasCollision() {
        return hasCollision;
    }

    public void setHasCollision(boolean hasCollision) {
        this.hasCollision = hasCollision;
    }

    public boolean getHasBeenDragged() {
        return hasBeenDragged;
    }

    public void setHasBeenDragged(boolean hasBeenDragged) {
        this.hasBeenDragged = hasBeenDragged;
    }

    public boolean isOverOtherDraggableObject() {
        return isOverOtherDraggable;
    }

    public void setOverOtherDraggable(boolean overOtherDraggable) {
        isOverOtherDraggable = overOtherDraggable;
    }

    /**
     * Return true if the object is overlapping another object.
     * @param otherObject Other object to check.
     * @return Boolean : True if overlapping.
     */
    public boolean intersect(DraggableObject otherObject) {
        Ellipse2D.Double boundaries = new Ellipse2D.Double(this.getiPosX() + 15, this.getiPosY() + 15, 30, 30);
        /*System.out.println(boundaries.intersects(otherObject.getiPosX() + 15*otherObject.getScale(), otherObject.getiPosY() + 15*otherObject.getScale(), 30*otherObject.getScale(), 30*otherObject.getScale())
                && this.getObjectId() != otherObject.getObjectId());*/

        return (boundaries.intersects(otherObject.getiPosX() + 15*otherObject.getScale(), otherObject.getiPosY() + 15*otherObject.getScale(), 30*otherObject.getScale(), 30*otherObject.getScale())
        && this.getObjectId() != otherObject.getObjectId());
    }

    public boolean isOverDraggableObject() {
        return isOverOtherDraggable;
    }
}
