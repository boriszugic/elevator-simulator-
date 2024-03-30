package src;

/**
 * Interface containing ElevatorStateMachine methods to be implemented
 * by individual states.
 */
public interface ElevatorState {
    /**
     * The current state receives a request and responds accordingly.
     *
     * @param context Current context of the state machine.
     */
    void requestReceived(ElevatorStateMachine context);

    /**
     * The current state receives a notification of arrival and responds accordingly.
     *
     * @param context Current context of the state machine.
     */
    void Arrival(ElevatorStateMachine context);

    /**
     * Displays the current state.
     */
    void displayState();

    /**
     * The current state receives a notification to move to the next request.
     *
     * @param context Current context of the state machine.
     */
    void nextRequest(ElevatorStateMachine context);

    /**
     * Overwrites toString method in each state class to give string representation
     * of the current state.
     *
     * @return String representation of state.
     */
    String toString();
}
