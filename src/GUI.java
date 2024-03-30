package src;

import javax.swing.*;

/**
 * User interface class which takes as input an elevator ID and port
 * before displaying them in a JFrame which updates based on the
 * given message.
 */
public class GUI {
    private JFrame frame; // Make frame an instance variable
    private JLabel label;

    /**
     * Constructor which calls methods to create and display a basic GUI
     * with elevator ID and port.
     *
     * @param id The given elevator id
     * @param port The given elevator port
     */
    public GUI(int id, int port) {
        createAndShowGUI(id, port);
    }

    /**
     * Method utilized to create a new JFrame and title it with
     * the corresponding elevator information.
     *
     * @param id The given elevator ID
     * @param port The given elevator port
     */
    private void createAndShowGUI(int id, int port) {
        frame = new JFrame("Elevator " + id + " port " + port);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        label = new JLabel(""); // Initialize the label with default text
        frame.getContentPane().add(label); // Add label to frame
        frame.setSize(200, 200);
    }

    /**
     * Method utilized to update the label contained within the JFrame with
     * the current floor of the elevator.
     *
     * @param message The elevator floor to be displayed
     */
    public void display(String message) {
        label.setText(message); // Update the text of the existing label
        frame.pack();
        frame.setVisible(true);
    }
}