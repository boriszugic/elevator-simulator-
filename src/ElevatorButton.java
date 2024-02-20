package src;

/**
 * Represents a button in the elevator.
 */
public class ElevatorButton {

    private final int id;

    /**
     * Constructs an ElevatorButton object with the specified ID.
     *
     * @param id The ID of the button
     */
    public ElevatorButton(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the button.
     *
     * @return The ID of the button
     */
    public int getId() {
        return this.id;
    }
}