package src;

import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

/**
 * Class implementing ElevatorState interface which represents the
 * Idle state of an elevator.
 */
class IdleState implements ElevatorState{
    /**
     * Method representing the elevator receiving a request in the
     * idle state.
     *
     * @param context Current context of the state machine.
     */
    @Override
    public void requestReceived(ElevatorStateMachine context){
        context.setState(new Moving());
    }

    /**
     * Method displaying the current state.
     */
    public void displayState(){
        System.out.println(this);
    }

    /**
     * Method representing the elevator receiving an arrival notification
     * in the idle state.
     *
     * @param context Current context of the state machine.
     */
    public void Arrival(ElevatorStateMachine context){}

    /**
     * Method representing the elevator receives a notification to move
     * to the next request.
     *
     * @param context Current context of the state machine.
     */
    public void nextRequest(ElevatorStateMachine context){
        context.setState(new Moving());
    }

    /**
     * Overrides default toString() with a string
     * representing the current state.
     *
     * @return String representation of state "Idle"
     */
    @Override
    public String toString(){
        return "Idle";
    }
}

/**
 * Class implementing ElevatorState interface which represents the
 * Moving state of an elevator.
 */
class Moving implements ElevatorState{
    /**
     * Method representing the elevator receiving a request in the
     * moving state.
     *
     * @param context Current context of the state machine.
     */
    @Override
    public void requestReceived(ElevatorStateMachine context){}

    /**
     * Method displaying the current state.
     */
    public void displayState(){
        System.out.println(this);
    }

    /**
     * Method representing the elevator receiving an arrival notification
     * in the moving state.
     *
     * @param context Current context of the state machine.
     */
    public void Arrival(ElevatorStateMachine context){
        context.setState(new UnloadingLoading());
    }

    /**
     * Method representing the elevator receives a notification to move
     * to the next request.
     *
     * @param context Current context of the state machine.
     */
    public void nextRequest(ElevatorStateMachine context){
        context.setState(new Moving());
    }

    /**
     * Overrides default toString() with a string
     * representing the current state.
     *
     * @return String representation of state "Moving"
     */
    @Override
    public String toString(){
        return "Moving";
    }
}

/**
 * Class implementing ElevatorState interface which represents the
 * Unloading/Loading state of an elevator.
 */
class UnloadingLoading implements ElevatorState{
    /**
     * Method representing the elevator receiving a request in the
     * unloading/loading state.
     *
     * @param context Current context of the state machine.
     */
    @Override
    public void requestReceived(ElevatorStateMachine context){
        context.setState(new Moving());
    }

    /**
     * Method displaying the current state.
     */
    public void displayState(){
        System.out.println(this);
    }

    /**
     * Method representing the elevator receiving an arrival notification
     * in the unloading/loading state.
     *
     * @param context Current context of the state machine.
     */
    public void Arrival(ElevatorStateMachine context){}

    /**
     * Method representing the elevator receives a notification to move
     * to the next request.
     *
     * @param context Current context of the state machine.
     */
    public void nextRequest(ElevatorStateMachine context){}

    /**
     * Overrides default toString() with a string
     * representing the current state.
     *
     * @return String representation of state "Unloading/Loading"
     */
    @Override
    public String toString(){
        return "Unloading/Loading";
    }
}

/**
 * Class representing a State Machine which simulates the transition
 * between elevator states based on given inputs.
 */
public class ElevatorStateMachine {
    private Map<String, ElevatorState> states;
    @Setter
    @Getter
    private ElevatorState state;

    /**
     * Default constructor which initializes the states
     * and places them in a new hashmap before setting the
     * default state to idle.
     */
    public ElevatorStateMachine() {
        states = new HashMap<>();
        states.put("IdleState", new IdleState());
        states.put("Moving", new Moving());
        states.put("Unloading / Loading", new UnloadingLoading());
        state = states.get("IdleState");
    }

    /**
     * Method representing when a floor button is pressed and a request
     * for an elevator is received.
     */
    public void requestReceived() {
        state.requestReceived(this);
    }

    /**
     * Method representing when an elevator is given a notification
     * that it has arrived at a destination.
     */
    public void Arrival() {
        state.Arrival(this);
    }

    /**
     * Method representing when an elevator has completed a request and transitions
     * to a new state for the next request.
     */
    public void nextRequest() {
        state.nextRequest(this);
    }
}