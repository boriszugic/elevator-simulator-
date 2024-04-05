package src;

import lombok.Setter;

/**
 * Utility class which binds a GUI to an elevator to display port number
 * and update based on floor location.
 */
public class Display {
    Elevator elevator;
    @Setter
    ElevatorGUI gui;

    /**
     * Default constructor for display class which assigns a new GUI
     * to an elevator.
     * @param e The elevator which requires a GUI.
     */
    public Display(Elevator e){
        elevator = e;
    }

    /**
     * Invokes the {@code updateElevatorDisplay} method on the associated GUI to reflect the current status
     * of the elevator in the graphical user interface. This method ensures the elevator's current state,
     * including its operational state, current floor, and request timestamps, are accurately displayed.
     * It fetches the elevator's state, current floor, and ID, passing these as parameters to update the GUI accordingly.
     * This method is typically called when there's a change in the elevator's state or position that needs to be
     * visually represented to the user.
     */
    public void display() {
        gui.updateElevatorDisplay(elevator.getId(), elevator.getState(), elevator.getCurrentFloor());
    }

    /**
     * Initiates a countdown from 10 to 0 for the associated elevator, displaying this countdown within the GUI.
     * This method is intended to visually represent time-sensitive operations of the elevator, such as door closing
     * or waiting times, enhancing the simulation's realism and user experience.
     * The countdown is displayed in the GUI specific to the elevator identified by its ID. Once initiated,
     * the countdown decreases by one every second until it reaches 0, at which point it automatically stops.
     */
    public void countdown(){
       gui.startElevatorCountdown(elevator.getId());
    }
}