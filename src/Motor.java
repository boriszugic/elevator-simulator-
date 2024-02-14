package src;

import java.util.concurrent.TimeUnit;

/**
 * Represents the motor of an elevator.
 */
public class Motor {
    private Elevator elevator;

    /**
     * Constructs a Motor object with the specified elevator.
     *
     * @param e The elevator associated with this motor
     */
    public Motor(Elevator e) {
        elevator = e;
    }

    /**
     * Moves the elevator to the specified floor number.
     *
     * @param floorNum The target floor number
     */
    public void move(int floorNum) {
        while (elevator.getCurrentFloor() != floorNum) {
            elevator.getDisplay().display(elevator.getCurrentFloor());
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            elevator.setCurrentFloor(elevator.getCurrentFloor() +
                    (floorNum - elevator.getCurrentFloor() > 0 ? 1 : -1));
        }
    }
}