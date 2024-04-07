package src;

import lombok.Getter;

import java.io.IOException;
import java.net.*;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * Class representing a floor which implements a Thread and socket
 * for each floor, with the corresponding id/numbers and which checks
 * for inputs for a given file.
 */
public class Floor implements Runnable {
    private static int nextFloorNum = 1;
    private static int nextId= 1;
    private final Logger logger;
    private final int SCHEDULER_PORT = 64;
    @Getter
    private final int id;
    @Getter
    private final int floorNum;
    private int destFloor;
    @Getter
    DatagramSocket socket;
    DatagramPacket receivePacket;

    /**
     * Returns the next utilized floor number for each floor.
     * @return Next floor number.
     */
    static synchronized int getNextFloorNum() {
        return nextFloorNum++;
    }

    /**
     * Returns the next utilized ID number for each floor.
     * @return Next ID number.
     */
    static synchronized int getNextId() {
        return nextId++;
    }

    /**
     * Constructor for floor instance which creates a new ID and floor number depending on
     * the previous amount allocated, and a socket/logger for communication/debugging.
     */
    public Floor() {
        this.id = getNextId();
        this.logger = new Logger(System.getProperty("user.home") + "/floor" + this.id + ".log");
        this.floorNum = getNextFloorNum();
        try {
            this.socket = new DatagramSocket(id);
        } catch (SocketException e) {
            throw new RuntimeException("Error creating DatagramSocket", e);
        }
    }

    /**
     * Constructor for floor instance necessary for testing, omits creation
     * of UDP socket.
     *
     * @param test Parameter for differentiating constructor
     */
    public Floor(String test){
        this.id = getNextId();
        this.logger = new Logger(System.getProperty("user.home") + "/floor" + this.id + ".log");
        this.floorNum = getNextFloorNum();
    }

    /**
     * Interface Runnable method which assigns a linked list for requests and
     * checks for input requests from the subsystem while there are requests present.
     *
     * It then checks this information compared to the current time and sends the request
     * when it is the given input time.
     */
    @Override
    public void run() {
        LinkedList<RequestData> requests = FloorSubsystem.getRequests(this.getFloorNum());
        logger.debug("All requests: \n" + requests);

        Calendar currentTime = Calendar.getInstance();
        while (!requests.isEmpty()) {
            RequestData request = requests.poll();
            this.destFloor = request.getRequestFloor();
            //Check current time compared to input time
            Date requestTime = request.getTime();
            long time;
            do{
                time = System.currentTimeMillis();

                //Check if current time has reached or passed the time of the request
                if(time >= requestTime.getTime()){
                    //Process the request
                    processRequest(request);

                    //Exit the loop once the request is processed
                    break;
                }
                try {
                    //Sleep for short duration before checking again
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } while (time < requestTime.getTime());
        }
    }
    /**
     * Prints information about the request, sends the request to the elevator control system,
     * and optionally waits for the request to be completed.
     *
     * @param request The request data containing information about the request, such as direction, floor number,
     *                request ID, and any error status.
     */
    private void processRequest(RequestData request) {
        printRequestInfo(request);
        sendRequest(request.getDirection(), this.floorNum, id, request.getError());
        if(request.getError() != 2){
            waitRequest();
        }
    }
    /**
     * Sends a request to the Scheduler via DatagramSocket.
     *
     * @param buttonType The direction of the request (UP or DOWN)
     * @param floorNum   The floor number
     * @param port       The port number
     * @param error The error code to be sent
     */
    private void sendRequest(Direction buttonType, int floorNum, int port, int error) {
        DatagramPacket packet = createPacket(buttonType, floorNum, port, error);
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
     * @param error The error code to be sent
     * @return DatagramPacket to be sent
     */
    public DatagramPacket createPacket(Direction buttonType, int floorNum, int port, int error) {
        byte[] data = new byte[4];
        data[0] = (byte) ((buttonType == Direction.UP) ? 1 : 0);
        data[1] = (byte) floorNum;
        data[2] = (byte) port;
        data[3] = (byte) error;
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
        data[1] = (byte) this.floorNum;
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
        logger.debug("Packet data: " + packet.getData()[0]);
        if (packet.getData()[0] != 0) {
            board();
        }
    }

    /**
     * Testing method utilized for sending example packets from
     * scheduler to floor.
     *
     * @param packet The packet to be parsed.
     * @return "Boarding" if packet is correct, "Error in packet" otherwise
     */
    public String testParseRequest(DatagramPacket packet){
        if(packet.getData()[0] != 0){
            return "Boarding";
        }
        return "Error in packet";
    }

    /**
     * Presses an elevator button.
     *
     * @param floorNum The floor number
     */
    private void pressElevatorButton(int floorNum) {
        // sends request
        logger.debug("Pressed elevator button: " + floorNum);
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
            logger.debug("Waiting until packet is received...");
            socket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        parseRequest(receivePacket);
    }

    /**
     * Prints necessary information from the about the request packet to be sent
     *
     * @param request  RequestData instance containing packet request information
     */
    public void printRequestInfo(RequestData request) {
        logger.debug("---------- SEND ----------");
        logger.debug(request.toString());
        logger.debug("--------------------------");
    }
}