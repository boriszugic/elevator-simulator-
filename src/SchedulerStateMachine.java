package src;

import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

class Idle implements SchedulerState{
    @Override
    public void requestReceived(SchedulerStateMachine context){
        context.setState(new Scheduling());
    }
    @Override
    public String toString(){
        return "Idle";
    }
}

class Scheduling implements SchedulerState{
    @Override
    public void requestReceived(SchedulerStateMachine context){
        context.setState(new Idle());
    }
    @Override
    public String toString(){
        return "Scheduling";
    }
}

public class SchedulerStateMachine {
    private Map<String, SchedulerState> states;
    @Setter
    @Getter
    private SchedulerState state;

    public SchedulerStateMachine() {
        states = new HashMap<>();
        states.put("Idle", new Idle());
        states.put("Scheduling", new Scheduling());
        state = states.get("Idle");
    }

    // when a floor button is pressed and a request for elevator is received.
    public void requestReceived() {
        state.requestReceived(this);
    }
}