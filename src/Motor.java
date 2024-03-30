package src;

import java.util.concurrent.TimeUnit;

/**
 * Represents the motor of an elevator.
 */
public class Motor {
    private Elevator elevator;
    private ConfigurationReader config;

    /**
     * Constructs a Motor object with the specified elevator.
     *
     * @param e The elevator associated with this motor
     */
    public Motor(Elevator e, ConfigurationReader config) {
        elevator = e;
        this.config = config;
    }

    /**
     * Moves the elevator to the specified floor number.
     *
     * @param floorNum The target floor number
     */
    public void move(int floorNum) {
        if (config.getNumFloors() < floorNum){
            return;
        }
        if (elevator.getCurrentFloor() == floorNum){
            //elevator.getDisplay().display(elevator.getCurrentFloor());
            return;
        }

        while (elevator.getCurrentFloor() != floorNum) {
            try {
                TimeUnit.MILLISECONDS.sleep(config.getMovingTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            elevator.setCurrentFloor(elevator.getCurrentFloor() +
                    (floorNum - elevator.getCurrentFloor() > 0 ? 1 : -1));
            elevator.getDisplay().display(String.valueOf(elevator.getCurrentFloor()));
        }
        //elevator.getState().Arrival();
    }
}