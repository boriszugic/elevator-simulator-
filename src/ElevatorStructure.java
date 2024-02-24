package src;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ElevatorStructure {
    @Getter
    private int id;
    @Getter
    private ElevatorStateMachine state;
    @Getter
    @Setter
    private int currFloor;
    @Getter
    private int port;
    @Getter
    @Setter
    private int destPort;

    public ElevatorStructure(int id, ElevatorStateMachine state, int currFloor, int port){
        this.id = id;
        this.state = state;
        this.currFloor = currFloor;
        this.port = port;
    }
}