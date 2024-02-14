package src;

import java.io.IOException;
import java.net.*;
import java.util.Calendar;
import java.util.LinkedList;

public class Floor implements Runnable {
    private static int nextFloorNum = 0;
    private static int nextPortNum = 0;
    private final int SCHEDULER_PORT = 64;
    private final int port;
    private final int floorNum;

    DatagramSocket socket;

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
            while(request.getTime().compareTo(currentTime.getTime()) != 0){
                //System.out.println(request.getTime().toString() + " : " + currentTime.getTime().toString());
                currentTime = Calendar.getInstance();
            }
            sendRequest(request.getDirection(), this.floorNum, port);
            waitRequest();
        }
    }

    /**
     * Sends a request to Scheduler via DatagramSocket
     *
     * @param floorNum
     * @param buttonType
     * @param port
     */
    private void sendRequest(Direction buttonType, int floorNum, int port) {
        DatagramPacket packet = createPacket(buttonType, floorNum, port);
        try {
            socket.send(packet);
            System.out.println("Request to send elevator sent.");
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
     * @param floorNum
     * @param buttonType
     * @param port
     */
    public DatagramPacket createPacket(Direction buttonType, int floorNum, int port) {
        byte[] data = new byte[3];
        data[0] = (byte) ((buttonType == Direction.UP) ? 1 : 0);
        data[1] = (byte) floorNum;
        data[2] = (byte) port;
        try
        {
            return new DatagramPacket(data, data.length, InetAddress.getLocalHost(), SCHEDULER_PORT);
        }
        catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** Parses received packet
     *
     * @param packet
     */
    private void parseRequest(DatagramPacket packet) {
        switch (packet.getData()[0]) {
            case 0: // open door
                board();
                break;
        }
    }


    /** Presses elevator button
     *
     * @param floorNum
     */
    private void pressElevatorButton(int floorNum) {
        // sends request
    }

    private void board() {
        // get dest floor number from RequestData DS
        pressElevatorButton(1);
    }

    private void waitRequest(){
        try {
            DatagramPacket receivedPacket = new DatagramPacket(new byte[2], 2);
            socket.receive(receivedPacket);

            parseRequest(receivedPacket);
        } catch (IOException e) {
            socket.close();
            throw new RuntimeException(e);
        }
    }


    public int getPort() {
        return port;
    }

    public int getFloorNum() {
        return floorNum;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

}