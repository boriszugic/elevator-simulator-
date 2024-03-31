package src;

import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

class Idle implements SchedulerState{
    /**
     * Method representing receiving a request while in the idle state.
     *
     * @param context Current context of the scheduler.
     */
    @Override
    public void requestReceived(SchedulerStateMachine context){
        context.setState(new Scheduling());
    }

    /**
     * Overrides default toString() with a string
     * representation of the idle state.
     *
     * @return String representation of the state "Idle"
     */
    @Override
    public String toString(){
        return "Idle";
    }
}

class Scheduling implements SchedulerState{
    /**
     * Method representing receiving a request while in the scheduling state.
     *
     * @param context Current context of the scheduler.
     */
    @Override
    public void requestReceived(SchedulerStateMachine context){
        context.setState(new Idle());
    }

    /**
     * Overrides default toString() with a string
     * representation of the scheduling state.
     *
     * @return String representation of the state "Scheduling"
     */
    @Override
    public String toString(){
        return "Scheduling";
    }
}

/**
 * Class representing a state machine which simulates the transition
 * between scheduler states based on the given inputs.
 */
public class SchedulerStateMachine {
    //Map representing possible states
    private Map<String, SchedulerState> states;
    //Current state of the machine
    @Setter
    @Getter
    private SchedulerState state;

    /**
     * Constructor which initializes the states and places them in a new
     * HashMap before setting the default state to idle.
     */
    public SchedulerStateMachine() {
        states = new HashMap<>();
        states.put("Idle", new Idle());
        states.put("Scheduling", new Scheduling());
        state = states.get("Idle");
    }

    /**
     * Method representing when a request has been received by the scheduler
     * and transitions to the next state.
     */
    public void requestReceived() {
        state.requestReceived(this);
    }
}