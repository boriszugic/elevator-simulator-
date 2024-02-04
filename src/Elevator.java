package src;

import lombok.Getter;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Elevator implements Runnable{

    private static int nextPort = 0;
    @Getter
    private final int port;

    DatagramSocket socket;
    Motor motor;
    Door door;
    List<ElevatorButton> buttons = new ArrayList<>();
    List<ElevatorLamp> lamps = new ArrayList<>();
    int currentFloor;
    int destinationFloor;
    int numOfFloors;
    int numOfPassengers;

    static synchronized int getNextPort() {
        return nextPort++;
    }

    public Elevator(int numFloors){
        port = getNextPort();
        motor = new Motor();
        door = new Door();
        currentFloor = 0;
        destinationFloor = 0;
        numOfPassengers = 0;
        this.numOfFloors = numFloors;

        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException("Error creating DatagramSocket", e);
        }

        for (int i = 0; i < numOfFloors; i++){
            buttons.add(new ElevatorButton(i));
            lamps.add(new ElevatorLamp(i));
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
    public Boolean move(int floorNum){
        if(floorNum >=0 && floorNum != currentFloor && floorNum <=numOfFloors){
            currentFloor = floorNum;
            return true;
        }
        return false;
    }

    /** Sends update to Floor through Scheduler that it has arrived at destFloor
     *
     */
    private void sendUpdate(){

    }

    public int getPort() {
        return socket.getLocalPort();
    }

    /**
     * Default getter for current floor.
     * @return currentFloor Floor of the elevator.
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Default getter for destination floor.
     * @return destinationFloor Desired floor of the elevator.
     */
    public int getDestinationFloor() {
        return destinationFloor;
    }

    /**
     * Default getter for number of passengers.
     * @return numOfPassengers Current number of passengers on elevator.
     */
    public int getNumOfPassengers() {
        return numOfPassengers;
    }

}