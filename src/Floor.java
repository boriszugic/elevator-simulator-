package src;

import lombok.Getter;

import java.io.IOException;
import java.net.*;

public class Floor implements Runnable {

    private static int nextPort = 1;
    private static int nextFloorNum = 0;
    private final int OUT_PORT = 23;
    private final int port;
    @Getter
    private final int floorNum;

    DatagramSocket socket;

    static synchronized int getNextPort() {
        return nextPort++;
    }

    static synchronized int getNextFloorNum() {
        return nextFloorNum++;
    }

    public Floor() {
        this.port = getNextPort();
        this.floorNum = getNextFloorNum();
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException("Error creating DatagramSocket", e);
        }
    }

    public static void main(String[] args) {
        //Floor floor = new Floor();
        //System.out.println("Port #" + floor.port);
        //new Thread(floor).start();
    }

    @Override
    public void run() {
        // STARTING POINT: floor makes a request, nothing happens until then
        // QUESTION: how should we simulate the making of a request?
        // idea 1: we have random time intervals when the requests are sent (with random floor numbers)
        sendRequest(ButtonType.UP, 3, OUT_PORT);
    }

    /**
     * Sends a request to Scheduler via DatagramSocket
     *
     * @param floorNum
     * @param buttonType
     * @param port
     */
    private void sendRequest(ButtonType buttonType, int floorNum, int port) {

        DatagramPacket packet = createPacket(buttonType, floorNum, port);

        try
        {
            socket.send(packet);
            System.out.println("Request sent.");
        }
        catch (IOException e)
        {
            socket.close();
            throw new RuntimeException(e);
        }
    }

    /** Creates packet to be sent by socket
     * Packet Format:
     * first byte  : 0 for DOWN, 1 for UP
     * second byte : floorNum
     * third byte  : if needed
     * @param floorNum
     * @param buttonType
     * @param port
     */
    public DatagramPacket createPacket(ButtonType buttonType, int floorNum, int port) {

        byte[] data = new byte[3];

        data[1] = (byte) ((buttonType == ButtonType.UP) ? 1 : 0);
        data[2] = (byte) floorNum;

        try
        {
            return new DatagramPacket(data, data.length, InetAddress.getLocalHost(), port);
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
    private void parsePacket(DatagramPacket packet) {

    }

    /**
     * Default getter for port parameter.
     * @return The current port value
     */
    public int getport() {
        return port;
    }

    /**
     * Default getter for the floor number.
     * @return The floor number of this occurrence.
     */
    public int getfloorNum() {
        return floorNum;
    }

}