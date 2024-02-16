package src;

import lombok.Getter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Singleton class representing the Scheduler.
 */
public class Scheduler implements Runnable{
    @Getter
    private final int port = 64;
    @Getter
    private final DatagramSocket socket;
    @Getter
    private List<FloorStructure> floors;
    @Getter
    private List<ElevatorStructure> elevators;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private Scheduler() {
        try {
            this.socket = new DatagramSocket(port);
            this.elevators = new ArrayList<>();
            this.floors = new ArrayList<>();
        } catch (SocketException e) {
            throw new RuntimeException("Error creating DatagramSocket", e);
        }
    }

    /** Singleton instance */
    private static final Scheduler instance = new Scheduler();

    public static void main(String[] args){
        Scheduler scheduler = Scheduler.getInstance();
        initializeFloorsAndElevators();
        printFloorAndElevatorInfo(scheduler);
        /*
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(scheduler).start();
        */
    }

    /**
     * Initializes floors and elevators
     */
    private static void initializeFloorsAndElevators() {

        boolean isFloorInitDone = false, isElevatorInitDone = false;

        while (!isFloorInitDone || !isElevatorInitDone){
            DatagramPacket packet = Scheduler.getInstance().waitRequest();
            byte[] data = packet.getData();

            switch (packet.getLength()){
                // initialization done
                case 1:
                    if (packet.getData()[0] == 0){
                        isFloorInitDone = true;
                        System.out.println("---- floors initialized ----");
                    }else{
                        isElevatorInitDone = true;
                        System.out.println("---- elevators initialized ----");
                    }
                    break;
                // floor init
                case 2:
                    Scheduler.getInstance().getFloors().add(new FloorStructure(data[0], data[1]));
                    System.out.println("---- ADDED FLOOR ----");
                    break;
                // elevator init
                case 3:
                    Scheduler.getInstance().getElevators().add(new ElevatorStructure(
                                                               data[0], ElevatorState.IDLE,
                                                               data[1], data[2]));
                    System.out.println("---- ADDED ELEVATOR ----");
                    break;
                default:
                    System.out.println("Invalid init message");
                    break;
            }
        }
    }

    /** Prints floor and elevator information */
    private static void printFloorAndElevatorInfo(Scheduler scheduler) {
        for (FloorStructure floor : scheduler.getFloors()) {
            System.out.println(floor.toString());
        }
        for (ElevatorStructure elevator : scheduler.getElevators()) {
            System.out.println(elevator.toString());
        }
    }

    /** Adds an elevator to the scheduler */
    public void addElevator(ElevatorStructure elevator) {
        elevators.add(elevator);
    }

    /** Adds a floor to the scheduler */
    public void addFloor(FloorStructure floor) {
        floors.add(floor);
    }

    /** Returns the singleton instance of the Scheduler */
    private static Scheduler getInstance() {
        return instance;
    }

    @Override
    public void run() {
        while (true) {
            sendRequest(parseRequest(waitRequest()));
        }
    }

    private DatagramPacket waitRequest() {
        DatagramPacket receivedPacket = new DatagramPacket(new byte[3], 3);
        try {
            socket.receive(receivedPacket);
            System.out.println("Request received.");
            return receivedPacket;
        } catch (IOException e) {
            socket.close();
            throw new RuntimeException(e);
        }
    }

    /** Parse the request from floor
     *
     * @param packet
     */
    private DatagramPacket parseRequest(DatagramPacket packet){
        byte[] data = packet.getData();
        // error checking
        if (isValid(data)){
            ElevatorStructure elevator = chooseElevator((data[0] == 0 ? Direction.DOWN : Direction.UP),
                                                        data[1]);
            return createPacket(data[1], elevator.getPort());
        }
        throw new RuntimeException("Invalid request.");
    }

    private boolean isValid(byte[] data) {
        if (data.length != 3) {
            return false;
        }
        if (!(data[0] == 0 || data[0] == 1)){
            return false;
        }
        int floorNum = data[1];
        return floorNum >= 0 && floorNum <= floors.size();
    }

    /**
     * Chooses an elevator to handle the request based on direction and floor number.
     *
     * @param direction The direction of the request
     * @param floorNum  The floor number of the request
     * @return The chosen elevator
     */
    private ElevatorStructure chooseElevator(Direction direction, int floorNum){
        return elevators.getFirst();
    }

    /**
     * Creates a DatagramPacket with the necessary information for the scheduler.
     * The packet contains the floor number and direction.
     *
     * @param floorNum  The floor number
     * @param direction The direction of the request (0 for down, 1 for up)
     * @return The created DatagramPacket
     */
    private DatagramPacket createPacket(int floorNum, int direction){
        try {
            return new DatagramPacket(new byte[]{(byte) floorNum, (byte) direction}, 2,
                                      InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a request packet to the chosen elevator.
     *
     * @param packet The packet to be sent
     */
    private void sendRequest(DatagramPacket packet){
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}