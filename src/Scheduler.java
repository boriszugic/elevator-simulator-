package src;

import lombok.Getter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

@Getter
/** Singleton class
 *
 */
public class Scheduler implements Runnable{

    private final int port = 23;
    DatagramSocket socket;
    List<Floor> floors;
    List<Elevator> elevators;

    private Scheduler() {
        try {
            this.socket = new DatagramSocket(port);
            this.elevators = new ArrayList<>();
            this.floors = new ArrayList<>();
        } catch (SocketException e) {
            throw new RuntimeException("Error creating DatagramSocket", e);
        }
    }

    @Getter
    private static final Scheduler instance = new Scheduler();

    public void addElevator(Elevator elevator) {
        elevators.add(elevator);
    }

    public void addFloor(Floor floor) {
        floors.add(floor);
    }

    public static void main(String[] args){
        //Scheduler scheduler = Scheduler.getInstance();
        //new Thread(scheduler).start();
    }

    @Override
    public void run() {

        while (true) {
            waitRequest();
        }
    }

    private void waitRequest() {
        DatagramPacket receivedPacket = new DatagramPacket(new byte[3], 3);
        try {
            socket.receive(receivedPacket);
            System.out.println("Request received.");
            parseRequest(receivedPacket);
        } catch (IOException e) {
            socket.close();
            throw new RuntimeException(e);
        }
    }

    /** Parse the request from floor (MOST LIKELY TO BE SYNCHRONIZED)
     *
     * @param packet
     */
    private void parseRequest(DatagramPacket packet){
        byte[] data = packet.getData();
        // error checking
        if (isValid(data)){
            Elevator elevator = chooseElevator(data[0], data[1]);
            sendRequest(createPacket(data[1], elevator.getPort()));
        }
    }

    private boolean isValid(byte[] data) {
        int floorNum = data[1];
        return floorNum >= 0 && floorNum <= floors.size();
    }

    /**
     *
     * @param direction
     * @param floorNum
     * @return chosenElevator
     */
    private Elevator chooseElevator(int direction, int floorNum){
        return elevators.getFirst();
    }

    /** Create packet with elevator-needed information
     * Format:
     * first byte : floorNum
     * second byte : if needed
     */
    private DatagramPacket createPacket(int floorNum, int port){
        try
        {
            return new DatagramPacket(new byte[]{(byte) floorNum, 0}, 2,
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
}
