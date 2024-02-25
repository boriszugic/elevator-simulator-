package src;

public interface ElevatorState {
    void requestReceived(ElevatorStateMachine context);

    void Arrival(ElevatorStateMachine context);

    void displayState();

    void nextRequest(ElevatorStateMachine context);
    
    String toString();
}
