package src;

import lombok.Getter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Elevator implements Runnable{

    private static final int MAX_NUM_OF_PASSENGERS = 10;
    private static int nextPort = 0;
    @Getter
    private final int port;

    DatagramSocket socket;
    Motor motor;
    Door door;
    Display display;
    List<ElevatorButton> buttons = new ArrayList<>();
    List<ElevatorLamp> lamps = new ArrayList<>();
    int currentFloor;
    int destinationFloor;
    int numOfFloors;
    int numOfPassengers;
    boolean isAvailable;

    static synchronized int getNextPort() {
        return nextPort++;
    }

    public Elevator(int numFloors){
        port = getNextPort();
        motor = new Motor(this);
        door = new Door();
        display = new Display();
        currentFloor = 0;
        destinationFloor = 0;
        numOfPassengers = 0;
        this.numOfFloors = numFloors;
        isAvailable = true;

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

        while (true) {
            DatagramPacket receivedPacket = new DatagramPacket(new byte[3], 3);
            try {
                socket.receive(receivedPacket);
                parseRequest(receivedPacket);
            } catch (IOException e) {
                socket.close();
                throw new RuntimeException(e);
            }
        }
    }

    /** Parses received data and updates attributes accordingly
     *
     */
    private void parseRequest(DatagramPacket packet){
        int floorNum = packet.getData()[0];
        move(floorNum);
    }

    /** Moves to Floor #floorNum
     *
     * @param floorNum
     */
    public void move(int floorNum){
        motor.move(floorNum);
        door.open();
        display.display(currentFloor);
        sendUpdate();
    }

    /** Sends update to Floor through Scheduler that it has arrived at destFloor
     *
     */
    private void sendUpdate(){
        try {
            socket.send(createPacket(UpdateType.OPEN_DOORS));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DatagramPacket createPacket(UpdateType updateType){
        try {
            return new DatagramPacket(new byte[]{(byte) updateType.ordinal(), 0}, 2,
                                      InetAddress.getLocalHost(), 1);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return socket.getLocalPort();
    }

    private boolean isOverloaded(){
        return numOfPassengers > MAX_NUM_OF_PASSENGERS ? true : false;
    }
}