package src;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Elevator implements Runnable {

    // Constants
    private static final int MAX_NUM_OF_PASSENGERS = 10;
    private static final int SCHEDULER_PORT = 64;

    // Static variables
    private static int nextPortNum = 65;
    private static int nextId = 1;

    // Instance variables
    @Getter
    private final int id;
    @Getter
    private final int port;
    @Getter
    private DatagramSocket socket;
    @Getter
    private Motor motor;
    @Getter
    private Door door;
    @Getter
    private Display display;
    @Getter
    private List<ElevatorButton> buttons = new ArrayList<>();
    @Getter
    private List<ElevatorLamp> lamps = new ArrayList<>();
    @Getter
    @Setter
    private int currentFloor;
    @Getter
    private int destinationFloor;
    @Getter
    private int numOfFloors;
    @Getter
    private int numOfPassengers;
    @Getter
    private ElevatorState state;

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
        currentFloor = 1;
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

    /**
     * Main method to create Elevator objects and send their information to the scheduler.
     * @param args Command line arguments: <num_of_elevators> <num_of_floors>
     */
    public static void main(String[] args) {
        for (int i = 0; i < Integer.parseInt(args[0]); i++){
            Elevator elevator = new Elevator(Integer.parseInt(args[1]));
            saveElevatorInScheduler(elevator);
            new Thread(elevator).start();
        }
        saveElevatorInScheduler(null);
    }

    /**
     * Sends elevator information to the Scheduler.
     * Format
     * 1st byte: 0 if Idle, 1 if Moving
     * 2nd byte: current floor
     * 3rd byte: receiving socket port
     *
     * @param elevator The Elevator object containing the information to be sent.
     */
    private static void saveElevatorInScheduler(Elevator elevator) {
        try {
            // end of initialization stage
            if (elevator == null) {
                new DatagramSocket().send(new DatagramPacket(new byte[]{1}, 1,
                                          InetAddress.getLocalHost(), SCHEDULER_PORT));
            } else {
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
        // Process the received datagram.
        System.out.println("Elevator: Packet received from Scheduler:");
        System.out.println("From host: " + packet.getAddress());
        System.out.println("Host port: " + packet.getPort());
        int floorNum = packet.getData()[0];
        System.out.println("Moving to floor: "+floorNum+" from currentFloor: "+currentFloor);
        move(floorNum);
    }

    /**
     * Moves to Floor #floorNum
     *
     * @param floorNum floor number to move to
     */
    public void move(int floorNum){
        motor.move(floorNum);
        door.open();
        display.display(currentFloor);
        sendUpdate();
    }

    /**
     * Sends an update packet to the Scheduler indicating that the doors are open.
     * This method creates a packet with the specified update type and sends it through the socket.
     */
    private void sendUpdate() {
        try {
            socket.send(createPacket(UpdateType.OPEN_DOORS));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a DatagramPacket for sending updates to the Scheduler.
     * The packet contains the ordinal value of the provided update type.
     *
     * @param updateType The type of update to include in the packet
     * @return The DatagramPacket to be sent
     */
    public DatagramPacket createPacket(UpdateType updateType) {
        try {
            System.out.println("Elevator: Sending packet:");
            System.out.println("To host: "+ InetAddress.getLocalHost());
            System.out.println("Destination host port: "+SCHEDULER_PORT);
            byte [] data = new byte[2];
            data[0] = (byte) updateType.ordinal();
            data[1] = (byte) currentFloor;
            return new DatagramPacket(data, data.length,
                    InetAddress.getLocalHost(), SCHEDULER_PORT);
            //Information prints

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks whether the elevator is overloaded based on the number of passengers.
     *
     * @return True if the elevator is overloaded, otherwise false
     */
    private boolean isOverloaded() {
        return numOfPassengers > MAX_NUM_OF_PASSENGERS;
    }
}