package src;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Floor implements Runnable {

    private final int OUT_PORT = 23;

    // each floor has a different port where it receives information (may need this not sure how it works)
    // static int inPort;

    DatagramSocket socket;

    public static void main(String[] args) {
        Floor floor = new Floor();
        new Thread(floor).start();
    }

    @Override
    public void run() {
        // STARTING POINT: floor makes a request, nothing happens until then
        // QUESTION: how should we simulate the making of a request?
        // idea 1: we have random time intervals when the requests are sent (with random floor numbers)
    }

    /**
     * Sends a request to Scheduler via DatagramSocket
     *
     * @param floorNum
     * @param buttonType
     * @param port
     */
    private void sendRequest(int floorNum, ButtonType buttonType, int port) {
        // sends DatagramPacket returned by the call to createPacket
    }

    /** Creates packet to be sent by socket
     *
     * @param floorNum
     * @param buttonType
     * @param port
     */
    private void createPacket(int floorNum, ButtonType buttonType, int port) {


    }

    /** Parses received packet
     *
     * @param packet
     */
    private void parsePacket(DatagramPacket packet) {


    }

}