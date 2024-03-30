package src;

/**
 * Utility class which binds a GUI to an elevator to display port number
 * and update based on floor location.
 */
public class Display {
    //The elevator to be displayed
    Elevator elevator;
    private final GUI gui;

    /**
     * Default constructor for display class which assigns a new GUI
     * to an elevator.
     * @param e The elevator which requires a GUI.
     */
    public Display(Elevator e){
        elevator = e;
        gui = new GUI(e.getId(), e.getPort());
    }

    public void display(String message) {
        gui.display(message);
    }
}