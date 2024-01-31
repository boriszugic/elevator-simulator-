package src;

import java.net.DatagramSocket;

public class Elevator implements Runnable{

    DatagramSocket socket;

    int currentFloor, destinationFloor, numOfPassengers;

    public static void main(String[] args){
        Elevator elevator = new Elevator();
        new Thread(elevator).start();
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
}