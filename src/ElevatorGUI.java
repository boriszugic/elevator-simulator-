package src;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * The {@code ElevatorGUI} class represents the graphical user interface for the elevator simulation.
 * It displays the state of each elevator, including its current floor and operational status
 */
public class ElevatorGUI extends JFrame {
    private final HashMap<Integer, JLabel> elevatorStateLabels;
    private final HashMap<Integer, JButton[]> elevatorButtonPanels;
    private final ElevatorSubsystem elevatorSubsystem;
    private final HashMap<Integer, JLabel> firstRequestTimestampLabels; // Track first request timestamps for each elevator
    private final HashMap<Integer, JLabel> lastCompletedRequestTimestampLabels; // Track last completed request timestamps for each elevator
    private final HashMap<Integer, JLabel> countdownLabels = new HashMap<>();

    public ElevatorGUI(ElevatorSubsystem subsystem) {
        super("Elevator Simulation");
        this.elevatorSubsystem = subsystem;
        elevatorStateLabels = new HashMap<>();
        elevatorButtonPanels = new HashMap<>();
        firstRequestTimestampLabels = new HashMap<>();
        lastCompletedRequestTimestampLabels = new HashMap<>();
        initializeUI();
    }

    /**
     * Initializes the user interface components for the elevator simulation.
     * This includes setting up panels for each elevator, floor buttons, state displays, and timestamp labels.
     */
    private void initializeUI() {
        int numElevators = elevatorSubsystem.getElevators().size();
        int numFloors = elevatorSubsystem.getNumFloors();
        int gridRows = (int) Math.ceil(Math.sqrt(numFloors));
        int gridCols = gridRows;
        if (gridRows * (gridCols - 1) >= numFloors) {
            gridCols--; // Reduce the number of columns if we can fit all buttons in a smaller grid
        }

        setLayout(new GridLayout(0, 5)); // Set only the columns and let rows be determined by the layout manager
        setSize(new Dimension(800, 600)); // Adjust the size based on the requirement or screen resolution
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (int i = 0; i < numElevators; i++) {
            JPanel elevatorPanel = new JPanel(new BorderLayout());
            elevatorPanel.setBorder(BorderFactory.createTitledBorder(
                    "Elevator " + (i + 1)));

            // Elevator state display
            JLabel stateLabel = new JLabel("State: Idle", SwingConstants.CENTER); // Default state
            stateLabel.setOpaque(true);
            stateLabel.setBackground(Color.GREEN);
            elevatorPanel.add(stateLabel, BorderLayout.NORTH);

            // Elevator buttons as floor indicators
            JPanel buttonPanel = new JPanel(new GridLayout(gridRows, gridCols, 5, 5));
            JButton[] buttonPanels = new JButton[numFloors];
            for (int floor = 1; floor <= numFloors; floor++) {
                JButton button = new JButton(Integer.toString(floor));
                button.setBorder(BorderFactory.createRaisedBevelBorder());
                button.setBackground(Color.GRAY); // Default color
                buttonPanel.add(button);
                buttonPanels[floor - 1] = button;
            }

            elevatorPanel.add(buttonPanel, BorderLayout.CENTER);
            add(elevatorPanel);

            elevatorStateLabels.put(i + 1, stateLabel);
            elevatorButtonPanels.put(i + 1, buttonPanels);

            // Timestamp labels for each elevator
            JPanel timestampPanel = new JPanel(new GridLayout(2, 1));
            JLabel firstRequestLabel = new JLabel("First: null");
            JLabel lastCompletedLabel = new JLabel("Last Completed: null");
            timestampPanel.add(firstRequestLabel);
            timestampPanel.add(lastCompletedLabel);
            firstRequestTimestampLabels.put(i, firstRequestLabel);
            lastCompletedRequestTimestampLabels.put(i, lastCompletedLabel);

            elevatorPanel.add(timestampPanel, BorderLayout.SOUTH);

            JLabel countdownLabel = new JLabel("Countdown: -");
            timestampPanel.add(countdownLabel);

            countdownLabels.put(i + 1, countdownLabel);
        }
    }

    /**
     * Updates the display for a specific elevator within the GUI. This method is called to reflect changes in the
     * elevator's state, current floor, and to update the timestamps for the first request and the last completed request.
     * @param elevatorId The ID of the elevator to update. This ID should correspond to a valid elevator managed by the {@code ElevatorSubsystem}.
     * @param state      The current state of the elevator as provided by an instance of {@code ElevatorStateMachine}.
     *                   This state determines the text and background color of the state label.
     * @param floor      The current floor of the elevator. This is used to highlight the appropriate floor button.
     *                   Floors are assumed to start at 1 and increment sequentially.
     */
    public void updateElevatorDisplay(int elevatorId, ElevatorStateMachine state, int floor) {
        SwingUtilities.invokeLater(() -> {
            JLabel stateLabel = elevatorStateLabels.get(elevatorId);
            JButton[] buttonPanels = elevatorButtonPanels.get(elevatorId);

            if (stateLabel != null) {
                stateLabel.setText("State: " + state.getState().toString());
                Color stateColor = switch (state.getState().toString()) {
                    case "Idle" -> Color.GREEN;
                    case "Moving_down", "Moving_up" -> Color.PINK;
                    case "Unloading/Loading" -> Color.cyan;
                    case "Fault" -> Color.RED;
                    case "Timeout" -> Color.ORANGE;
                    default -> Color.LIGHT_GRAY;
                };
                stateLabel.setBackground(stateColor);
            }

            if (buttonPanels != null) {
                // Reset all buttons to default color
                for (JButton button : buttonPanels) {
                    button.setBackground(Color.GRAY);
                }
                // Highlight the button for the current floor
                if (floor > 0 && floor <= buttonPanels.length) {
                    buttonPanels[floor - 1].setBackground(Color.YELLOW);
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Calendar firstRequestTimestamp = elevatorSubsystem.getElevators().
                    get(elevatorId).
                    getFirstRequestTimestamp();
            Calendar lastCompletedRequestTimestamp = elevatorSubsystem.getElevators().
                    get(elevatorId).
                    getLastCompletedRequestTimestamp();

            JLabel firstRequestLabel = firstRequestTimestampLabels.get(elevatorId - 1);
            JLabel lastCompletedLabel = lastCompletedRequestTimestampLabels.get(elevatorId - 1);

            if (firstRequestTimestamp != null) {
                firstRequestLabel.setText("First: " + sdf.format(firstRequestTimestamp.getTime()));
            }
            if (lastCompletedRequestTimestamp != null) {
                lastCompletedLabel.setText("Last Completed: " + sdf.format(lastCompletedRequestTimestamp.getTime()));
            }
        });
    }

    /**
     * Starts a countdown from 10 to 0 for a specified elevator. Updates a label in the GUI
     * to display the countdown, which decreases every second. This is used to visually
     * indicate a temporary state or operation, like a door closing countdown.
     *
     * @param elevatorId The ID of the elevator for which the countdown is to be displayed.
     *                   Assumes that the elevator IDs start from 1 and are consecutive.
     */
    public void startElevatorCountdown(int elevatorId) {
        JLabel countdownLabel = countdownLabels.get(elevatorId); // Assuming you've stored the countdown labels in a map
        if (countdownLabel == null) return; // If the label doesn't exist, exit the method

        final int[] countdown = {10}; // Starting countdown value
        Timer timer = new Timer(1000, e -> {
            if (countdown[0] > 0) {
                // Update the countdown label
                countdownLabel.setText("Countdown: " + countdown[0]);
                countdown[0]--;
            } else {
                // Stop the timer and reset the countdown label when finished
                countdownLabel.setText("Countdown: -");
                ((Timer)e.getSource()).stop();
            }
        });
        timer.setInitialDelay(0); // Start counting down immediately
        timer.start();
    }
}