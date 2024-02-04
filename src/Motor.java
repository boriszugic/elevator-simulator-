package src;

import java.util.concurrent.TimeUnit;

public class Motor {

    Elevator elevator;

    public Motor(Elevator e){
        elevator = e;
    }
    public void move(int floorNum) {

        while(elevator.currentFloor != floorNum){
            elevator.getDisplay().display(elevator.currentFloor);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            elevator.currentFloor += (floorNum - elevator.currentFloor > 0) ? 1 : -1;
        }
    }
}
