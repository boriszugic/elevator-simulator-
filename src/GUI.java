package src;

import javax.swing.*;

public class GUI {
    private JFrame frame; // Make frame an instance variable
    private JLabel label;
    // Constructor to set up the GUI
    public GUI(int id, int port) {
        createAndShowGUI(id, port);
    }

    // Method to create and show the GUI
    private void createAndShowGUI(int id, int port) {
        frame = new JFrame("Elevator " + id + " port " + port);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        label = new JLabel(""); // Initialize the label with default text
        frame.getContentPane().add(label); // Add label to frame
        frame.setSize(200, 200);
    }

    // Instance method to display a message
    public void display(String message) {
        label.setText(message); // Update the text of the existing label
        frame.pack();
        frame.setVisible(true);
    }
}