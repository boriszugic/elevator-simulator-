package src;

import java.util.concurrent.TimeUnit;

/**
 * Class representing the motor of an elevator which takes as input
 * a configuration file with the given speed of movement between floors.
 */
public class Motor {
    //The elevator associated with the motor
    private Elevator elevator;
    //The configuration file associated with the speed of the motor
    private ConfigurationReader config;

    /**
     * Constructs a Motor object with the specified elevator and
     * configuration file.
     *
     * @param e The elevator associated with this motor
     * @param config The configuration file to be used
     */
    public Motor(Elevator e, ConfigurationReader config) {
        elevator = e;
        this.config = config;
    }

    /**
     * Moves the elevator to the specified floor number. Returns without
     * movement if the requested floor is invalid or the current floor is the
     * requested floor. Utilizes a timeout for movement based on the movement
     * time found in the configuration file.
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