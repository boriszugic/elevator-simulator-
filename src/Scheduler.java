package src;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/** Singleton class
 *
 */
public class Scheduler implements Runnable{
    private final int port = 64;
    DatagramSocket socket;
    List<FloorStructure> floors;
    List<ElevatorStructure> elevators;

    private Scheduler() {
        try {
            this.socket = new DatagramSocket(port);
            this.elevators = new ArrayList<>();
            this.floors = new ArrayList<>();
        } catch (SocketException e) {
            throw new RuntimeException("Error creating DatagramSocket", e);
        }
    }

    private static final Scheduler instance = new Scheduler();

    public static void main(String[] args){
        Scheduler scheduler = Scheduler.getInstance();
        initializeFloorsAndElevators();
        for (FloorStructure floor : scheduler.getFloors()){
            System.out.println(floor.toString());
        }
        for (ElevatorStructure elevator : scheduler.getElevators()){
            System.out.println(elevator.toString());
        }
        /*
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(scheduler).start();
        */
    }

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

    public void addElevator(ElevatorStructure elevator) {
        elevators.add(elevator);
    }

    public void addFloor(FloorStructure floor) {
        floors.add(floor);
    }

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
            ElevatorStructure elevator = chooseElevator(data[0], data[1]);
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
     *
     * @param direction
     * @param floorNum
     * @return chosenElevator
     */
    private ElevatorStructure chooseElevator(int direction, int floorNum){
       return elevators.get(0);
    }

    /** Create packet with scheduler-needed information
     * Format:
     * first byte : floor number
     * second byte : direction
     */
    private DatagramPacket createPacket(int floorNum, int direction){
        try
        {
            return new DatagramPacket(new byte[]{(byte) floorNum, (byte) direction}, 2,
                                      InetAddress.getLocalHost(), port);
        }
        catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** Send packet
     *
     */
    private void sendRequest(DatagramPacket packet){
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return port;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public List<FloorStructure> getFloors() {
        return floors;
    }

    public List<ElevatorStructure> getElevators() {
        return elevators;
    }
}