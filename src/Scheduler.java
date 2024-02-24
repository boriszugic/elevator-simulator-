package src;

import lombok.Getter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
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
        //scheduler.run();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(scheduler).start();

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
            //System.out.println("Request received.");
            return receivedPacket;
        } catch (IOException e) {
            socket.close();
            throw new RuntimeException(e);
        }
    }

    /** Parse the request from floor
     *
     * @param packet The packet to be parsed
     */
    private DatagramPacket parseRequest(DatagramPacket packet){
        byte[] data = packet.getData();
        System.out.println("Packet length: "+packet.getLength());
        System.out.println("Packet data length: "+packet.getData().length);
        if (packet.getLength() == 3){ //Elevator packet response
            if (isValid(data)){ //Elevator packet request to retrieve elevator
                System.out.println("elseif");
                // Process the received datagram.
                printPacketInfo(packet,"Floor");
                ElevatorStructure elevator = chooseElevator((data[0] == 0 ? Direction.DOWN : Direction.UP),
                        data[1]);
                return createElevatorPacket(data[1], elevator.getPort());
            }
        }
        if (packet.getLength() == 2) {
            if(packet.getData()[1] == 0){
                //Elevator packet request when boarded
                System.out.println("if packet.getLength() == 2");
                // Process the received datagram.
                printPacketInfo(packet,"Floor");
                int floorNum = packet.getData()[0];
                if(floorNum <= floors.size()){return createElevatorPacket(floorNum, 65);}
            }else{
                System.out.println("if packet.getLength() == 4");
                // Process the received datagram.
                byte[] updateData = new byte[]{packet.getData()[0]};
                int port = packet.getData()[1];
                printPacketInfo(packet,"Elevator");
                return createFloorPacket(updateData,port);
            }
        }
        // error checking
        throw new RuntimeException("Invalid request.");
    }

    private boolean isValid(byte[] data) {

        if (data.length != 3) {
            return false;
        }
        if (!(data[0] == 0 || data[0] == 1)){
            System.out.println("False");
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
     * Formatted to be sent to Elevator subsystem
     * The packet contains the floor number and direction.
     *
     * @param floorNum  The floor number
     * @return The created DatagramPacket
     */
    private DatagramPacket createElevatorPacket(int floorNum, int port){
        try {
            return new DatagramPacket(new byte[]{(byte) floorNum}, 1,
                                      InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Creates a DatagramPacket with the necessary information for the scheduler.
     * Formatted to be sent to Floor subsystem
     * The packet contains the Elevator UPDATETYPE.
     *
     * @param data  The byte array of data received from Elevator
     * @return The created DatagramPacket
     */
    private DatagramPacket createFloorPacket(byte[] data,int port){
        try {
            return new DatagramPacket(data, 1,
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
            //Information prints
            System.out.println("Scheduler: Sending packet:");
            System.out.println("To host: "+ packet.getAddress());
            System.out.println("Destination host port: "+packet.getPort());
            int len = packet.getData().length;
            System.out.println("Length: "+len);
            System.out.print("Containing: ");
            System.out.println(new String(packet.getData(),0,len)+"\n");
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printPacketInfo(DatagramPacket packet, String sender) {
        System.out.println("Scheduler: Packet received from "+sender+": ");
        System.out.println("From host: " + packet.getAddress());
        System.out.println("Host port: " + packet.getPort()+"\n");
        System.out.println("Data: "+ Arrays.toString(packet.getData()));
    }

}