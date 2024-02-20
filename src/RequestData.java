package src;

import lombok.Getter;
import java.util.Date;

/**
 * Represents data related to elevator requests.
 */
@Getter
public class RequestData {
    Direction direction;
    private int currentFloor;
    private Date time;
    private int requestFloor;

    /**
     * Constructs a RequestData object with the specified parameters.
     *
     * @param time          The time at which the request was made
     * @param currentFloor  The current floor of the elevator
     * @param direction     The direction in which the elevator is requested to move (Up or Down)
     * @param requestFloor  The floor to which the elevator is requested to move
     */
    public RequestData(Date time, int currentFloor, Direction direction, int requestFloor) {
        this.time = time;
        this.currentFloor = currentFloor;
        this.direction = direction;
        this.requestFloor = requestFloor;
    }
}