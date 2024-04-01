package src;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Utility class representing a singleton instance of an elevator with basic parameters
 * necessary for scheduling calculations.
 */
@ToString
public class ElevatorStructure {
    @Getter
    private int id;
    @Getter
    @Setter
    private ElevatorStateMachine state;
    @Getter
    @Setter
    private int currFloor;
    @Getter
    private int port;

    /**
     * Default constructor which assigns the given variables to the ElevatorStructure
     *
     * @param id ID of the given elevator
     * @param state State of the given elevator
     * @param currFloor Current floor of the given elevator
     * @param port Port of the given elevator
     */
    public ElevatorStructure(int id, ElevatorStateMachine state, int currFloor, int port){
        this.id = id;
        this.state =  state;
        this.currFloor = currFloor;
        this.port = port;
    }
}