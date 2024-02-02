package src;

import lombok.Getter;

import java.net.DatagramSocket;
import java.util.List;

@Getter
public class Elevator implements Runnable{


    DatagramSocket socket;
    Motor motor;
    Door door;
    List<ElevatorButton> buttons;
    int currentFloor, destinationFloor, numOfFloors, numOfPassengers;


    public Elevator(){
        motor = new Motor();
        door = new Door();
        currentFloor = 0;
        destinationFloor = 0;
        numOfPassengers = 0;

        for (int i = 0; i < numOfFloors; i++){
            buttons.add(new ElevatorButton(i));
        }
    }

    public static void main(String[] args){
        //Elevator elevator = new Elevator();
        //new Thread(elevator).start();
    }

    @Override
    public void run() {

    }


    /** Parses received data and updates attributes accordingly
     *
     */
    private void parseRequest(){

    }

    /** Moves to Floor #floorNum
     *
     * @param floorNum
     */
    private void move(int floorNum){

    }

    /** Sends update to Floor through Scheduler that it has arrived at destFloor
     *
     */
    private void sendUpdate(){

    }

    public int getPort() {
        return socket.getLocalPort();
    }
}