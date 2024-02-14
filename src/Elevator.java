package src;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Elevator implements Runnable {

    private static final int MAX_NUM_OF_PASSENGERS = 10;
<<<<<<< HEAD
    private static final int SCHEDULER_PORT = 64;
    private static int nextPortNum = 65;
=======
    private static final int SCHEDULER_PORT = 51;
    private static int nextPortNum = 3100;
>>>>>>> da79e512b9cdf6d5489795534a6ee88e4dd98489
    private static int nextId = 1;
    private final int id;
    private final int port;
    private int floorPort;
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
    ElevatorState state;

    static synchronized int getNextPortNum() {
        return nextPortNum++;
    }

    private int getNextId() {
        return nextId++;
    }
    public Elevator(int numFloors) {
        this.port = getNextPortNum();
        this.numOfFloors = numFloors;
        this.id = getNextId();
        motor = new Motor(this);
        door = new Door();
        display = new Display();
        currentFloor = 0;
        destinationFloor = 0;
        numOfPassengers = 0;
        state = ElevatorState.IDLE;

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

    public static void main(String[] args) {
        for (int i = 0; i < Integer.parseInt(args[0]); i++){
            Elevator elevator = new Elevator(Integer.parseInt(args[1]));
            // store info of each elevator in scheduler
            sendElevatorInformationToScheduler(elevator);
            //new Thread(elevator).start();
        }

        sendElevatorInformationToScheduler(null);

    }

    /**
     * Sends elevator information to the Scheduler.
     *
     * Format
     * 1st byte: 0 if Idle, 1 if Moving
     * 2nd byte: current floor
     * 3rd byte: receiving socket port
     *
     * @param elevator The Elevator object containing the information to be sent.
     */
    private static void sendElevatorInformationToScheduler(Elevator elevator) {
        try {
            // end of initialization stage
            if (elevator == null) {
                new DatagramSocket().send(new DatagramPacket(new byte[]{1}, 1,
                                          InetAddress.getLocalHost(), SCHEDULER_PORT));
            } else {
                // Create and send DatagramPacket containing elevator information
                elevator.getSocket().send(new DatagramPacket(new byte[]{
                                                             (byte) elevator.getId(),
                                                             (byte) elevator.getCurrentFloor(),
                                                             (byte) elevator.getPort()},
                                                       3, InetAddress.getLocalHost(), SCHEDULER_PORT));
            }
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    private int getId() {
        return id;
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

    /**
     * Parses received data and updates attributes accordingly
     */
    private void parseRequest(DatagramPacket packet){
        int floorNum = packet.getData()[0];
        move(floorNum);
    }

    /**
     * Moves to Floor #floorNum
     *
     * @param floorNum
     */
    public void move(int floorNum){
        motor.move(floorNum);
        door.open();
        display.display(currentFloor);
        sendUpdate();
    }

    /**
     * Sends update to Floor through Scheduler that it has arrived at destFloor
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
            return new DatagramPacket(new byte[]{(byte) updateType.ordinal()}, 1,
                                      InetAddress.getLocalHost(), SCHEDULER_PORT);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return port;
    }

    private boolean isOverloaded(){
        return numOfPassengers > MAX_NUM_OF_PASSENGERS ? true : false;
    }

    public int getFloorPort() {
        return floorPort;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public Motor getMotor() {
        return motor;
    }

    public Door getDoor() {
        return door;
    }

    public Display getDisplay() {
        return display;
    }

    public List<ElevatorButton> getButtons() {
        return buttons;
    }

    public List<ElevatorLamp> getLamps() {
        return lamps;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public int getNumOfFloors() {
        return numOfFloors;
    }

    public int getNumOfPassengers() {
        return numOfPassengers;
    }

    private ElevatorState getState() {
        return state;
    }
}