package src;

import java.util.Date;

public class RequestData {
    Direction move;
    private int currentFloor;
    private Date time;
    private int requestFloor;

    public RequestData(Date time, int currentFloor, Direction direction, int requestFloor) {
        this.time = time;
        this.currentFloor = currentFloor;
        this.move = direction;
        this.requestFloor = requestFloor;
    }

    /**
     * get the LocalDateTime value
     * @return time
     */
    public Date getTime() {
        return this.time;
    }

    /**
     * get the int value
     * representing floor number
     * @return floorNum
     */
    public int getCurrentFloor() {
        return this.currentFloor;
    }

    /**
     * get the int value
     * representing the floor number
     * pressed
     * @return floorButton
     */
    public int getRequestedFloor() {
        return this.requestFloor;
    }

    /**
     * get the Direction value
     * representing the state of the
     * elevator (UP/DOWN) button
     * @return button
     */
    public Direction getDirection() {
        return this.move;
    }
}