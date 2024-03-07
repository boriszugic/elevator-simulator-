package src;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ElevatorStructure {
    @Getter
    private int id;
    @Getter
    @Setter
    private ElevatorStateEnum state;
    @Getter
    @Setter
    private int currFloor;
    @Getter
    private int port;

    public ElevatorStructure(int id, ElevatorStateEnum state, int currFloor, int port){
        this.id = id;
        this.state = state;
        this.currFloor = currFloor;
        this.port = port;
    }
}