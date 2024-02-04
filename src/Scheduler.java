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
        Scheduler scheduler = Scheduler.getInstance();
        new Thread(scheduler).start();
    }

    @Override
    public void run() {
        try {
            DatagramPacket receivedPacket = new DatagramPacket(new byte[3], 3);
            socket.receive(receivedPacket);

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
        Elevator elevator = chooseElevator((int) data[0], (int) data[1]);
        sendRequest(createPacket((int) data[1], elevator.getPort()));
    }

    /**
     *
     * @param direction
     * @param floorNum
     * @return chosenElevator
     */
    private Elevator chooseElevator(int direction, int floorNum){

        return elevators.get(0);
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

    /**
     * Default getter for the current list of floors.
     * @return floors The list of associated floors.
     */
    public List<Floor> getFloors() {
        return floors;
    }

    /**
     * Default getter for the current list of elevators.
     * @return elevators The list of associated elevators.
     */
    public List<Elevator> getElevators() {
        return elevators;
    }
}
