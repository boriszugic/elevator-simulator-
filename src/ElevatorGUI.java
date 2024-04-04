package src;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ElevatorGUI extends JFrame {
    private final HashMap<Integer, JLabel> elevatorStateLabels;
    private final HashMap<Integer, JButton[]> elevatorButtonPanels;
    private final ElevatorSubsystem elevatorSubsystem;
    private final HashMap<Integer, JLabel> firstRequestTimestampLabels; // Track first request timestamps for each elevator
    private final HashMap<Integer, JLabel> lastCompletedRequestTimestampLabels; // Track last completed request timestamps for each elevator

    public ElevatorGUI(ElevatorSubsystem subsystem) {
        super("Elevator Simulation");
        this.elevatorSubsystem = subsystem;
        elevatorStateLabels = new HashMap<>();
        elevatorButtonPanels = new HashMap<>();
        firstRequestTimestampLabels = new HashMap<>();
        lastCompletedRequestTimestampLabels = new HashMap<>();
        initializeUI();
    }

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
            JLabel firstRequestLabel = new JLabel("First Request: null");
            JLabel lastCompletedLabel = new JLabel("Last Completed Request: null");
            timestampPanel.add(firstRequestLabel);
            timestampPanel.add(lastCompletedLabel);
            firstRequestTimestampLabels.put(i, firstRequestLabel);
            lastCompletedRequestTimestampLabels.put(i, lastCompletedLabel);

            elevatorPanel.add(timestampPanel, BorderLayout.SOUTH);
        }
    }

    public void updateElevatorDisplay(int elevatorId, ElevatorStateMachine state, int floor) {
        SwingUtilities.invokeLater(() -> {
            JLabel stateLabel = elevatorStateLabels.get(elevatorId);
            JButton[] buttonPanels = elevatorButtonPanels.get(elevatorId);

            if (stateLabel != null) {
                stateLabel.setText("State: " + state.getState().toString());
                Color stateColor = switch (state.getState().toString()) {
                    case "Idle" -> Color.GREEN;
                    case "Moving_down", "Moving_up" -> Color.ORANGE;
                    case "Unloading/Loading" -> Color.cyan;
                    case "Fault" -> Color.RED;
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar firstRequestTimestamp = elevatorSubsystem.getElevators().
                    get(elevatorId).
                    getFirstRequestTimestamp();
            Calendar lastCompletedRequestTimestamp = elevatorSubsystem.getElevators().
                    get(elevatorId).
                    getLastCompletedRequestTimestamp();

            JLabel firstRequestLabel = firstRequestTimestampLabels.get(elevatorId - 1);
            JLabel lastCompletedLabel = lastCompletedRequestTimestampLabels.get(elevatorId - 1);

            if (firstRequestTimestamp != null) {
                firstRequestLabel.setText("First Request: " + sdf.format(firstRequestTimestamp.getTime()));
            }
            if (lastCompletedRequestTimestamp != null) {
                lastCompletedLabel.setText("Last Completed Request: " + sdf.format(lastCompletedRequestTimestamp.getTime()));
            }
        });
    }
}