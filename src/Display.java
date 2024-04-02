package src;

import lombok.Setter;

/**
 * Utility class which binds a GUI to an elevator to display port number
 * and update based on floor location.
 */
public class Display {
    //The elevator to be displayed
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

    public void display() {
        gui.updateElevatorDisplay(elevator.getId(), elevator.getState(), elevator.getCurrentFloor());
    }
}