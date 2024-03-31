package src;

/**
 * Interface containing SchedulerStateMachine methods to be implemented
 * by individual states.
 */
public interface SchedulerState {

    /**
     * The current state receives a request to perform scheduling.
     *
     * @param context Current context of the scheduler.
     */
    void requestReceived(SchedulerStateMachine context);

    /**
     * Overwrites toString() method in each state class to give a string
     * representation of the current state.
     *
     * @return String representation of state
     */
    String toString();
}
