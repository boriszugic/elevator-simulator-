package src;

import lombok.Getter;

import java.io.IOException;
import java.net.*;

public class Floor implements Runnable {

    private static int nextPort = 0;
    private static int nextFloorNum = 0;
    private final int OUT_PORT = 23;
    private final int IN_PORT;
    @Getter
    private final int FLOOR_NUM;

    DatagramSocket socket;

    static synchronized int getNextPort() {
        return nextPort++;
    }

    static synchronized int getNextFloorNum() {
        return nextFloorNum++;
    }

    public Floor() {
        this.IN_PORT = getNextPort();
        this.FLOOR_NUM = getNextFloorNum();
        try {
            this.socket = new DatagramSocket(IN_PORT);
        } catch (SocketException e) {
            throw new RuntimeException("Error creating DatagramSocket", e);
        }
    }

    public static void main(String[] args) {
        //Floor floor = new Floor();
        //System.out.println("Port #" + floor.IN_PORT);
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
    private DatagramPacket createPacket(ButtonType buttonType, int floorNum, int port) {

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
}