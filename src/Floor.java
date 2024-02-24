package src;

import lombok.Getter;

import java.io.IOException;
import java.net.*;
import java.util.Calendar;
import java.util.LinkedList;

public class Floor implements Runnable {
    private static int nextFloorNum = 1;
    private static int nextPortNum = 1;
    private final int SCHEDULER_PORT = 64;
    @Getter
    private final int port;
    @Getter
    private final int floorNum;

    private int destFloor;
    @Getter
    DatagramSocket socket;
    DatagramPacket receivePacket;
    static synchronized int getNextFloorNum() {
        return nextFloorNum++;
    }

    static synchronized int getNextPortNum() {
        return nextPortNum++;
    }

    public Floor() {
        this.port = getNextPortNum();
        this.floorNum = getNextFloorNum();
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException("Error creating DatagramSocket", e);
        }
    }

    @Override
    public void run() {

        LinkedList<RequestData> requests = FloorSubsystem.getRequests(this.getFloorNum());

        Calendar currentTime = Calendar.getInstance();
        while (!requests.isEmpty()) {
            RequestData request = requests.poll();
            this.destFloor = request.getRequestFloor();
            while(request.getTime().compareTo(currentTime.getTime()) != 0){
                currentTime = Calendar.getInstance();
            }
            printRequestInfo(request);
            sendRequest(request.getDirection(), this.floorNum, port);
            waitRequest();
        }
    }

    /**
     * Sends a request to the Scheduler via DatagramSocket.
     *
     * @param buttonType The direction of the request (UP or DOWN)
     * @param floorNum   The floor number
     * @param port       The port number
     */
    private void sendRequest(Direction buttonType, int floorNum, int port) {
        DatagramPacket packet = createPacket(buttonType, floorNum, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            socket.close();
            throw new RuntimeException(e);
        }
    }

    /** Creates packet to be sent by socket
     * Packet Format:
     * first byte  : 0 for DOWN, 1 for UP
     * second byte : floor number
     * third byte  : port of sending floor socket
     * @param buttonType The direction of the request (UP or DOWN)
     * @param floorNum   The floor number
     * @param port       The port number
     * @return DatagramPacket to be sent
     */
    public DatagramPacket createPacket(Direction buttonType, int floorNum, int port) {
        byte[] data = new byte[3];
        data[0] = (byte) ((buttonType == Direction.UP) ? 1 : 0);
        data[1] = (byte) floorNum;
        data[2] = (byte) port;
        try {
            return new DatagramPacket(data, data.length, InetAddress.getLocalHost(), SCHEDULER_PORT);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    /** Creates packet to be sent by socket
     * Packet Format:
     * first byte : floor number
     * @param floorNum   The floor number
     * @return DatagramPacket to be sent
     */
    public DatagramPacket createPacket(int floorNum) {
        byte[] data = new byte[2];
        data[0] = (byte) floorNum;
        try {
            return new DatagramPacket(data, data.length, InetAddress.getLocalHost(), SCHEDULER_PORT);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Parses the received packet.
     *
     * @param packet The received packet
     */
    private void parseRequest(DatagramPacket packet) {

        System.out.println("Packet data: "+ packet.getData()[0]);
        switch (packet.getData()[0]) {
            case 0: // open door
                board();

                break;
        }
    }

    /**
     * Presses an elevator button.
     *
     * @param floorNum The floor number
     */
    private void pressElevatorButton(int floorNum) {
        // sends request
        System.out.println("Requesting elevator to move to floor number: "+floorNum);
        DatagramPacket packet = createPacket(floorNum);
        try {
            socket.send(packet);
        } catch (IOException e) {
            socket.close();
            throw new RuntimeException(e);
        }
    }

    /**
     * Boards the elevator.
     */
    private void board() {
        // get dest floor number from RequestData DS
        pressElevatorButton(this.destFloor);
    }

    /**
     * Waits for a request from the Scheduler.
     */
    private void waitRequest(){
        byte[] data = new byte[1];
        receivePacket = new DatagramPacket(data, 1);
        try {
            System.out.println("Waiting until packet received...");
            // Block until a datagram is received via sendReceiveSocket.
            socket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Process the received datagram.
        System.out.println("Floor: Packet received:");
        System.out.println("From host port: " + receivePacket.getPort()+"\n");
        parseRequest(receivePacket);
    }
    /**
     * Prints necessary information from the about the request packet to be sent
     *
     * @param request  RequestData instance containing packet request information
     */
    public void printRequestInfo(RequestData request) {
        System.out.println("Floor: Sending request to Scheduler containing:");
        System.out.println("Direction: "+request.direction);
        System.out.println("Current floor: "+request.getCurrentFloor());
        System.out.println("port: "+port+"\n");
    }

}