package src;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Utility class representing a singleton instance of a floor with basic parameters
 * necessary for scheduling calculations.
 */
@ToString
public class FloorStructure {
    @Getter
    @Setter
    private Direction buttonType;
    @Getter
    private int floorNum;
    @Getter
    private int port;
    @Getter
    @Setter
    private int elevatorPort;

    /**
     * Default constructor which assigns the given variables to the FloorStructure.
     *
     * @param floorNum Floor number of the given floor.
     * @param port Port number of the given floor.
     */
    public FloorStructure(int floorNum, int port) {
        this.floorNum = floorNum;
        this.port = port;
    }
}