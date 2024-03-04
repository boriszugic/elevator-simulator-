package src;

import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

class IdleState implements ElevatorState{
    @Override
    public void requestReceived(ElevatorStateMachine context){
        context.setState(new Moving());
    }
    public void displayState(){}
    public void Arrival(ElevatorStateMachine context){}
    public void nextRequest(ElevatorStateMachine context){
        context.setState(new Moving());
    }

    @Override
    public String toString(){
        return "Idle";
    }
}

class Moving implements ElevatorState{
    @Override
    public void requestReceived(ElevatorStateMachine context){}
    public void displayState(){}
    public void Arrival(ElevatorStateMachine context){
        context.setState(new UnloadingLoading());
    }
    public void nextRequest(ElevatorStateMachine context){
        context.setState(new Moving());
    }
    @Override
    public String toString(){
        return "Moving";
    }
}

class UnloadingLoading implements ElevatorState{
    @Override
    public void requestReceived(ElevatorStateMachine context){
        context.setState(new Moving());
    }
    public void displayState(){}
    public void Arrival(ElevatorStateMachine context){}
    public void nextRequest(ElevatorStateMachine context){}
    @Override
    public String toString(){
        return "Unloading/Loading";
    }
}

public class ElevatorStateMachine {
    private Map<String, ElevatorState> states;
    @Setter
    @Getter
    private ElevatorState state;

    public ElevatorStateMachine() {
        states = new HashMap<>();
        states.put("IdleState", new IdleState());
        states.put("Moving", new Moving());
        states.put("Unloading / Loading", new UnloadingLoading());
        state = states.get("IdleState");
    }

    // when a floor button is pressed and a request for elevator is received.
    public void requestReceived() {
        state.requestReceived(this);
    }

    // for when an elevator is in motion.
    public void Arrival() {
        state.Arrival(this);
    }

    // for when the elevator has completed a request and must transition states.
    public void nextRequest() {
        state.nextRequest(this);
    }
}