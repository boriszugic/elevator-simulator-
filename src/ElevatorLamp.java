package src;

/**
 * Represents a lamp in the elevator.
 */
public class ElevatorLamp {

    private final int id;
    private boolean isOn;

    /**
     * Constructs an ElevatorLamp object with the specified ID.
     *
     * @param id The ID of the lamp
     */
    public ElevatorLamp(int id) {
        this.id = id;
        isOn = false;
    }

    /**
     * Turns on the lamp.
     */
    public void turnOn() {
        isOn = true;
    }

    /**
     * Turns off the lamp.
     */
    public void turnOff() {
        isOn = false;
    }
}
