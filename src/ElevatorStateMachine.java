package src;
import java.util.HashMap;
import java.util.Map;

interface ElevatorState {
    void requestReceived(ElevatorStateMachine context);

    void Arrival(ElevatorStateMachine context);

    void displayState();

    void nextRequest(ElevatorStateMachine context);
}

    class idleState implements ElevatorState{
    @Override
        public void requestReceived(ElevatorStateMachine context){
        System.out.println("Elevator Request Received!");
        context.setState("Moving");
    }
        public void displayState(){
        System.out.println("Elevator is currently IDLE.");
        }
        public void Arrival(ElevatorStateMachine context){
        // not a possible sequence just for formality.
        System.out.println("Arrived.");
        }
        public void nextRequest(ElevatorStateMachine context){
            System.out.println("Request completed, moving on");
           context.setState("Moving");
        }
    }

    class Moving implements ElevatorState{
        @Override
        public void requestReceived(ElevatorStateMachine context){
            System.out.println("Elevator Request Received!");
        }
        public void displayState(){
            System.out.println("Elevator is currently MOVING.");
        }
        public void Arrival(ElevatorStateMachine context){
            System.out.println("Opening Doors...");
            System.out.println("Doors are open");
            System.out.println("Arrived.");
            context.setState("Unloading / Loading");
        }
        public void nextRequest(ElevatorStateMachine context){
            System.out.println("Request Skipped, moving on");
            context.setState("Moving");
        }
    }

    class UnloadingLoading implements ElevatorState{
        @Override
        public void requestReceived(ElevatorStateMachine context){
            System.out.println("Elevator Request Received!");
            System.out.println("Doors are closing.");
            System.out.println("Doors are closed.");
            System.out.println("Request completed, moving on");
            context.setState("Moving");
        }
        public void displayState(){
            System.out.println("Elevator is currently STOPPED with the doors OPEN");
        }
        public void Arrival(ElevatorStateMachine context){
            // not a possible sequence just for formality.
            System.out.println("Arrived.");
        }
        public void nextRequest(ElevatorStateMachine context){

        }
    }



class ElevatorStateMachine {
    private Map<String, ElevatorState> states;
    private ElevatorState currentState;

    public ElevatorStateMachine(){
        states = new HashMap<>();
        states.put("IdleState", new idleState());
        states.put("Moving", new Moving());
        states.put("Unloading / Loading", new UnloadingLoading());
        currentState = states.get("IdleState");
    }
    public void setState(String stateName){
        this.currentState = states.get(stateName);

    }
    // when a floor button is pressed and a request for elevator is received.
    public void requestReceived(){
       // System.out.println("State Before :" );
        currentState.displayState();
        currentState.requestReceived(this);
        //System.out.println("State After :" );
        currentState.displayState();
    }

    // for when an elevator is in motion.
    public void Arrival(){
        System.out.println("Elevator has now arrived");
        currentState.Arrival(this);

    }


    // for when the elevator has completed a request and must transition states.
    public void nextRequest(){
        System.out.println("Request completed, moving on");
        currentState.nextRequest(this);
    }


}

//public enum ElevatorState {
 //   IDLE,
 //   MOVING
//}
