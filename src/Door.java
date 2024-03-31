package src;

import java.util.concurrent.TimeUnit;

/**
 * Class representing an elevator door which opens and closes
 * based on the configuration settings given.
 */
public class Door {
    private boolean isOpen;
    private ConfigurationReader config;

    /**
     * Default constructor for Door class which takes the configuration settings
     * and sets the door to closed.
     * @param config
     */
    public Door(ConfigurationReader config){
        isOpen = false;
        this.config = config;
    }

    /**
     * Opens the door after taking a period of time determined by the
     * configurations.
     */
    public void open(){
        try {
            TimeUnit.MILLISECONDS.sleep(config.getOpenDoorTime());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        isOpen = true;
    }

    /**
     * Closes the door after taking a period of time determined by the
     * configurations.
     */
    public void close(){
        try {
            TimeUnit.MILLISECONDS.sleep(config.getCloseDoorTime());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        isOpen = false;
    }
}
