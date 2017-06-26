package sample;

import javafx.scene.control.ProgressBar;

/**
 * Created by Charles on 15/11/2016. // TODO : Doc
 */
public class CustomTimeLine extends ProgressBar {

    /* - Attributes - */
    private Integer currentFrame;
    private Integer numberOfFrames;

    /* - Constructor - */
    public CustomTimeLine() {

        // Initializing the pointer attributes.
        currentFrame = 0;
        numberOfFrames = 0;
        // Ajusting the progress bar attributes.
        ajustProgress();
    }

    /* - Public methods - */

    /**
     * Return the current frame.
     *
     * @return Integer : Current frame.
     */
    public Integer getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Sets the current frame.
     *
     * @param currentFrame New current frame.
     */
    public void setCurrentFrame(Integer currentFrame) {
        this.currentFrame = currentFrame;
    }

    /**
     * Returns the max number of frames.
     *
     * @return Integer : max number of frames.
     */
    public Integer getNumberOfFrames() {
        return numberOfFrames;
    }

    /**
     * Sets the max number of frames.
     *
     * @param numberOfFrames New max number of frames.
     */
    public void setNumberOfFrames(Integer numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
    }

    /**
     * Advance to the next frame.
     */
    public void nextFrame() {
        if (currentFrame == numberOfFrames) {
            numberOfFrames++;
        }
        currentFrame++;
        ajustProgress();
    }

    /**
     * Go back to the last frame.
     */
    public void lastFrame() {
        if (currentFrame > 0) {
            currentFrame--;
        }
        ajustProgress();
    }

    public void reset() {
        currentFrame = 0;
        numberOfFrames = 0;
        ajustProgress();
    }


    /* - Private methods - */

    /**
     * Adjust the progress bar according to the ratio of currentFrame / numberOfFrames.
     */
    public void ajustProgress() {
        setProgress((double) currentFrame / (double) numberOfFrames);
    }

}
