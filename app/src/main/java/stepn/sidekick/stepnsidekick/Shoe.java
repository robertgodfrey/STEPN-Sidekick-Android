package stepn.sidekick.stepnsidekick;

/**
 * Shoe class for iterating through the different types of shoes on the main menu and saving
 * custom speed values.
 *
 * @author Bob Godfrey
 * @version 1.0.6
 */

public class Shoe {
    private float minSpeed, maxSpeed;
    private final int imageSource, numFeet;
    private final String title;

    public Shoe(String title, int imageSource, float minSpeed, float maxSpeed, int numFeet) {
        this.title = title;
        this.imageSource = imageSource;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.numFeet = numFeet;
    }

    public float getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(float minSpeed) {
        this.minSpeed = minSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getImageSource() {
        return imageSource;
    }

    public String getTitle() {
        return title;
    }

    public int getNumFeet() {
        return numFeet;
    }
}
