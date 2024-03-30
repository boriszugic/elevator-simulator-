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
    private int error;

    /**
     * Constructs a RequestData object with the specified parameters.
     *
     * @param time          The time at which the request was made
     * @param currentFloor  The current floor of the request being made
     * @param direction     The direction in which the elevator is requested to move (Up or Down)
     * @param requestFloor  The destination floor
     */
    public RequestData(Date time, int currentFloor, Direction direction, int requestFloor, int error) {
        this.time = time;
        this.currentFloor = currentFloor;
        this.direction = direction;
        this.requestFloor = requestFloor;
        this.error = error;
    }

    /**
     * Overrides default toString() method and returns a String representation
     * of the contained request data.
     *
     * @return String representation of request data
     */
    @Override
    public String toString() {
        return " | " + time + " | " + currentFloor + " | " + direction + " | " + requestFloor + " | " + error + " | ";
    }
}