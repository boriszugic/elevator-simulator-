package src;

import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

/**
 * Class implementing ElevatorState interface which represents the
 * Idle state of an elevator.
 */

class IdleState implements ElevatorState {

    private ElevatorStateMachine state;

    public IdleState(ElevatorStateMachine state){this.state = state;}
    /**
     * Method representing the elevator receiving a request in the
     * idle state.
     *
     * @param context Current context of the state machine.
     */
    @Override
    public void requestReceived(ElevatorStateMachine context, String direction) {
        if (direction.equals("UP")) {
            context.setState(new Moving_up(state));
        } else {
            context.setState(new Moving_down(state));
        }
    }

    /**
     * Method representing the elevator receiving an arrival notification
     * in the idle state.
     *
     * @param context Current context of the state machine.
     */
    public void Arrival(ElevatorStateMachine context) {
    }

    /**
     * Method representing the elevator receives a notification to move
     * to the next request.
     *
     * @param context Current context of the state machine.
     */

    public void nextRequest(ElevatorStateMachine context, String direction){
        if(direction.equals("UP")){
            context.setState(new Moving_up(state));
        }
        else{
            context.setState(new Moving_down(state));
        }
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
 * Fault state of an elevator.
 */
class FaultState implements ElevatorState {

    private ElevatorStateMachine state;

    public FaultState(ElevatorStateMachine state){this.state = state;}
    /**
     * Method representing the elevator receiving a request in the
     * fault state.
     *
     * @param context Current context of the state machine.
     */
    @Override
    public void requestReceived(ElevatorStateMachine context, String direction) {}

    /**
     * Method representing the elevator receiving an arrival notification
     * in the fault state.
     *
     * @param context Current context of the state machine.
     */
    public void Arrival(ElevatorStateMachine context) {}

    /**
     * Method representing the elevator receives a notification to move
     * to the next request.
     *
     * @param context Current context of the state machine.
     */

    public void nextRequest(ElevatorStateMachine context, String direction){}

    /**
     * Overrides default toString() with a string
     * representing the current state.
     *
     * @return String representation of state "Fault"
     */
    @Override
    public String toString(){
        return "Fault";
    }
}

/**
 * Class implementing ElevatorState interface which represents the
 * Timeout state of an elevator.
 */
class Timeout implements ElevatorState {

    private ElevatorStateMachine state;

    public Timeout(ElevatorStateMachine state){this.state = state;}
    /**
     * Method representing the elevator receiving a request in the
     * fault state.
     *
     * @param context Current context of the state machine.
     */
    @Override
    public void requestReceived(ElevatorStateMachine context, String direction) {}

    /**
     * Method representing the elevator receiving an arrival notification
     * in the fault state.
     *
     * @param context Current context of the state machine.
     */
    public void Arrival(ElevatorStateMachine context) {}

    /**
     * Method representing the elevator receives a notification to move
     * to the next request.
     *
     * @param context Current context of the state machine.
     */

    public void nextRequest(ElevatorStateMachine context, String direction){}

    /**
     * Overrides default toString() with a string
     * representing the current state.
     *
     * @return String representation of state "Fault"
     */
    @Override
    public String toString(){
        return "Timeout";
    }
}

/**
 * Class implementing ElevatorState interface which represents the
 * Moving state of an elevator.
 */
class Moving_up implements ElevatorState{
    private ElevatorStateMachine state;
    public Moving_up(ElevatorStateMachine context){
        this.state = state;
    }
    /**
     * Method representing the elevator receiving a request in the
     * moving state.
     *
     * @param context Current context of the state machine.
     */
    @Override
    public void requestReceived(ElevatorStateMachine context, String direction){}

    /**
     * Method representing the elevator receiving an arrival notification
     * in the moving state.
     *
     * @param context Current context of the state machine.
     */
    public void Arrival(ElevatorStateMachine context){
        context.setState(new UnloadingLoading(state));
    }

    /**
     * Method representing the elevator receives a notification to move
     * to the next request.
     *
     * @param context Current context of the state machine.
     */

    public void nextRequest(ElevatorStateMachine context, String direction){
        if(direction.equals("UP")){
            context.setState(new Moving_up(state));
        }
        else{
            context.setState(new Moving_down(state));
        }
    }

    /**
     * Overrides default toString() with a string
     * representing the current state.
     *
     * @return String representation of state "Moving"
     */
    @Override
    public String toString(){
        return "Moving_up";
    }
}
class Moving_down implements ElevatorState{
    private ElevatorStateMachine state;
    public Moving_down(ElevatorStateMachine context){
        this.state = state;
    }
    /**
     * Method representing the elevator receiving a request in the
     * moving state.
     *
     * @param context Current context of the state machine.
     */
    @Override
    public void requestReceived(ElevatorStateMachine context, String direction){}

    /**
     * Method representing the elevator receiving an arrival notification
     * in the moving state.
     *
     * @param context Current context of the state machine.
     */
    public void Arrival(ElevatorStateMachine context){
        context.setState(new UnloadingLoading(state));
    }

    /**
     * Method representing the elevator receives a notification to move
     * to the next request.
     *
     * @param context Current context of the state machine.
     */
    public void nextRequest(ElevatorStateMachine context, String direction){
        if(direction.equals("UP")){
            context.setState(new Moving_up(state));
        }
        else{
            context.setState(new Moving_down(state));
        }
    }

    /**
     * Overrides default toString() with a string
     * representing the current state.
     *
     * @return String representation of state "Moving"
     */
    @Override
    public String toString(){
        return "Moving_down";
    }
}

/**
 * Class implementing ElevatorState interface which represents the
 * Unloading/Loading state of an elevator.
 */
class UnloadingLoading implements ElevatorState{
    private ElevatorStateMachine machine;
    public UnloadingLoading(ElevatorStateMachine state){this.machine = machine;}

    /**
     * Method representing the elevator receiving a request in the
     * unloading/loading state.
     *
     * @param context Current context of the state machine.
     */
    @Override
    public void requestReceived(ElevatorStateMachine context, String direction){}

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
    public void nextRequest(ElevatorStateMachine context, String direction){
        if(direction.equals("UP")){
            context.setState(new Moving_up(machine));
        }
        else{
            context.setState(new Moving_down(machine));
        }
    }

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
        states.put("IdleState", new IdleState(this));
        states.put("Moving_up", new Moving_up(this));
        states.put("Moving_down", new Moving_down(this));
        states.put("UnloadingLoading", new UnloadingLoading(this));
        states.put("Fault", new FaultState(this));
        state = states.get("IdleState");
    }

    /**
     * Method representing when a floor button is pressed and a request
     * for an elevator is received.
     */
    public void requestReceived(ElevatorStateMachine context, String direction) {
        state.requestReceived(this, direction);
    }

    /**
     * Method representing when an elevator is given a notification
     * that it has arrived at a destination.
     */
    public void Arrival(ElevatorStateMachine context) {
        state.Arrival(this);
    }

    /**
     * Method representing when an elevator has completed a request and transitions
     * to a new state for the next request.
     */
    public void nextRequest(ElevatorStateMachine context, String direction) {
        state.nextRequest(this, direction);
    }

}
