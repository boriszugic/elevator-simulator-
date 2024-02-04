package src;

import lombok.Getter;

import java.io.IOException;
import java.net.*;

public class Floor implements Runnable {

    private static int nextPort = 1;
    private static int nextFloorNum = 0;
    private final int OUT_PORT = 23;
    /**
     * -- GETTER --
     *  Default getter for port parameter.
     *
     * @return The current port value
     */
    @Getter
    private final int port;
    /**
     * -- GETTER --
     *  Default getter for the floor number.
     *
     * @return The floor number of this occurrence.
     */
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
        while (true) {
            sendRequest(ButtonType.UP, 1, OUT_PORT);
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
    private void sendRequest(ButtonType buttonType, int floorNum, int port) {

        DatagramPacket packet = createPacket(buttonType, floorNum, port);

        try
        {
            socket.send(packet);
            System.out.println("Request to send elevator sent.");
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

        data[0] = (byte) ((buttonType == ButtonType.UP) ? 1 : 0);
        data[1] = (byte) floorNum;

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
    private void parseRequest(DatagramPacket packet) {

        switch(packet.getData()[0]){
            case 0: // open door
                board();
                break;
            case 1: // close door

                break;
            case 2: // overload
                break;
        }

    }

    private void board() {
        try
        {
            socket.send(new DatagramPacket(new byte[]{0, 0}, 2,
                        InetAddress.getLocalHost(), OUT_PORT));
            System.out.println("Request to close doors sent.");
        }
        catch (IOException e)
        {
            socket.close();
            throw new RuntimeException(e);
        }
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

}