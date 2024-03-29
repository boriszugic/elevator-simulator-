package src;

import lombok.Getter;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Singleton class representing the Scheduler.
 */
public class Scheduler implements Runnable{

    private static final Logger logger = new Logger(System.getProperty("user.home") + "/scheduler.log");
    @Getter
    private final int port = 64;

    private final int elevator_port = 65;
    @Getter
    private final DatagramSocket socket;
    @Getter
    private HashMap<Integer, FloorStructure> floors;
    @Getter
    private HashMap<Integer, ElevatorStructure> elevators;
    @Getter
    private SchedulerStateEnum state;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private Scheduler() {
        state = SchedulerStateEnum.IDLE; //Initialize scheduler in IDLE state
        try {
            this.socket = new DatagramSocket(port);
            this.elevators = new HashMap<>();
            this.floors = new HashMap<>();
        } catch (SocketException e) {
            logger.error("Error creating DatagramSocket");
            throw new RuntimeException("Error creating DatagramSocket", e);
        }
    }

    /** Singleton instance */
    private static final Scheduler instance = new Scheduler();

    public static void main(String[] args){
        Scheduler scheduler = Scheduler.getInstance();
        initializeFloorsAndElevators();
        printFloorAndElevatorInfo(scheduler);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(scheduler).start();

    }

    /**
     * Initializes floors and elevators
     */
    private static void initializeFloorsAndElevators() {
        //ElevatorStateMachine elevatorStateMachine = new ElevatorStateMachine();

        boolean isFloorInitDone = false, isElevatorInitDone = false;

        while (!isFloorInitDone || !isElevatorInitDone){
            DatagramPacket packet = Scheduler.getInstance().waitRequest();
            byte[] data = packet.getData();
            switch (packet.getLength()){
                // initialization done
                case 1:
                    if (packet.getData()[0] == 0){
                        isFloorInitDone = true;
                        logger.debug("---- floors initialized ----");
                    }else{
                        isElevatorInitDone = true;
                        logger.debug("---- elevators initialized ----");
                    }
                    break;
                // floor init
                case 2:
                    Scheduler.getInstance().getFloors().put((int) data[0], new FloorStructure(data[0], data[1]));
                    logger.debug("---- ADDED FLOOR ----");
                    break;
                // elevator init
                case 3:
                    Scheduler.getInstance().getElevators().put((int) data[2],new ElevatorStructure(
                                                               data[0], ElevatorStateEnum.IDLE,
                                                               data[1], data[2]));
                    logger.debug("---- ADDED ELEVATOR ----");
                    break;
                default:
                    logger.debug("Invalid init message");
                    break;
            }
        }
        try {
            Scheduler.getInstance().getSocket().send(new DatagramPacket(new byte[]{1}, 1,
                                                     InetAddress.getLocalHost(), 150));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Prints floor and elevator information */
    private static void printFloorAndElevatorInfo(Scheduler scheduler) {
        for (FloorStructure floor : scheduler.getFloors().values()) {
            logger.debug(floor.toString());
        }
        for (ElevatorStructure elevator : scheduler.getElevators().values()) {
            logger.debug(elevator.toString());
        }
    }

    /** Adds an elevator to the scheduler */
    public void addElevator(ElevatorStructure elevator) {
        elevators.put(elevator.getPort(), elevator);
    }

    /** Adds a floor to the scheduler */
    public void addFloor(FloorStructure floor) {
        floors.put(floor.getFloorNum(), floor);
    }

    /** Returns the singleton instance of the Scheduler */
    private static Scheduler getInstance() {
        return instance;
    }

    @Override
    public void run() {
        while (true) {
            sendRequest(parseRequest(waitRequest()));
        }
    }

    /**
     * Waits for a request from Floor and Elevator threads.
     */
    private DatagramPacket waitRequest() {
        DatagramPacket receivedPacket = new DatagramPacket(new byte[3], 3);
        try {
            socket.receive(receivedPacket);
            return receivedPacket;
        } catch (IOException e) {
            socket.close();
            throw new RuntimeException(e);
        }
    }

    /** Parse the request from floor
     *
     * @param packet The DatagramPacket to be parsed
     * @return The appropriate DatagramPacket to be sent
     */
    private DatagramPacket parseRequest(DatagramPacket packet){
        byte[] data = packet.getData();
        state = SchedulerStateEnum.SCHEDULING;
        if (packet.getLength() == 3 && data[2] == 0){ // Elevator response
            byte[] updateData = new byte[]{data[0]};
            printPacketReceived(packet,"Elevator");
            elevators.get((int)data[0]).setCurrFloor(data[0]);
            return createFloorPacket(updateData, packet.getData()[1]);
        }
        else if (packet.getLength() == 3 && isValid(data)){ //Elevator packet request to retrieve elevator
            printPacketReceived(packet,"Floor");
            // Choose elevator
            ElevatorStructure elevator = chooseElevator((data[0] == 0 ? Direction.DOWN : Direction.UP), data[1]);
            // Assign floor the chosen elevator
            floors.get((int) data[1]).setElevatorPort(elevator.getPort());
            return createElevatorPacket(data[1], elevator.getId());
        }
        else if (packet.getLength() == 2) {
            //Floor request when passenger pressed elevator button
            printPacketReceived(packet, "Floor");
            int floorNum = data[0];
            if (floorNum <= floors.size()) {
                // Create a packet to elevator port assigned to floor in the if statement above
                DatagramPacket returnPacket = createElevatorPacket(floorNum, floors.get((int)data[1]).getElevatorPort());
                elevators.get(floors.get((int)data[1]).getElevatorPort()).setState(ElevatorStateEnum.IDLE);
                return returnPacket;
            }
        }
        // Error checking
        logger.error("Invalid request (Improper format).");
        throw new RuntimeException("Invalid request (Improper format).");
    }

    /**
     * Validates formatted data for elevator requests
     *
     * @param data
     * @return false if any of the conditions are triggered, true otherwise
     */
    public boolean isValid(byte[] data) {

        if (data.length != 3) { //Checks for sufficient length of data
            return false;
        }
        if (!(data[0] == 0 || data[0] == 1)){ //Checks byte 0 for Direction UP or DOWN
            logger.error("Invalid message format");
            return false;
        }
        int floorNum = data[1];
        return floorNum >= 0 && floorNum <= floors.size(); // Checks if floorNum is valid
    }

    /**
     * Chooses an elevator to handle the request based on direction and floor number.
     *
     * @param direction The direction of the request
     * @param floorNum  The floor number of the request
     * @return The chosen elevator
     */
    private ElevatorStructure chooseElevator(Direction direction, int floorNum){
        // Sort the elevators by their distance to the requested floor
        List<ElevatorStructure> elevs = new ArrayList<>(elevators.values());
        elevs.sort(Comparator.comparingInt(e -> Math.abs(e.getCurrFloor() - floorNum)));
        // Filter and find the first elevator that is either idle or moving towards the requested floor
        Optional<ElevatorStructure> suitableElevator = elevs.stream()
                .filter(elevator -> isElevatorSuitable(elevator, direction, floorNum))
                .findFirst();
        // Set the new state of Scheduler's copy (Elevator will set its own)
        if(suitableElevator.get().getCurrFloor() - floorNum != 0) {
            suitableElevator.get().setState((suitableElevator.get().getCurrFloor() > floorNum) ?
                    ElevatorStateEnum.MOVING_DOWN :
                    ElevatorStateEnum.MOVING_UP);
        }
        logger.debug("Chose Elevator " + suitableElevator.get().getId());
        return suitableElevator.get(); // Return the found elevator
    }

    private boolean isElevatorSuitable(ElevatorStructure elevator, Direction direction, int floorNum) {
        switch (elevator.getState()) {
            case IDLE:
                return true; // An idle elevator is always suitable
            case MOVING_UP:
                return direction == Direction.UP && elevator.getCurrFloor() <= floorNum;
            case MOVING_DOWN:
                return direction == Direction.DOWN && elevator.getCurrFloor() >= floorNum;
            default:
                return false; // If the elevator is in a state that doesn't allow it to take new requests
        }
    }

    /**
     * Creates a DatagramPacket with the necessary information for the scheduler.
     * Formatted to be sent to Elevator subsystem
     * The packet contains the floor number and direction.
     * @param floorNum  The floor number
     * @return The created DatagramPacket
     */
    public DatagramPacket createElevatorPacket(int floorNum, int ID){
        try {
            byte[] data = new byte[2];
            data[0] = (byte) floorNum;
            data[1] = (byte) ID;
            return new DatagramPacket(data, data.length,
                                      InetAddress.getLocalHost(), elevator_port);
        } catch (UnknownHostException e) {
            logger.error("Error creating elevator packet.");
            throw new RuntimeException("Error creating elevator packet.");
        }
    }

    /**
     * Creates a DatagramPacket with the necessary information for the scheduler.
     * Formatted to be sent to Floor subsystem
     * The packet contains the Elevator UPDATETYPE.
     *
     * @param data  The byte array of data received from Elevator
     * @return The created DatagramPacket
     */
    public DatagramPacket createFloorPacket(byte[] data,int port){
        try {
            return new DatagramPacket(data, 1,
                    InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            logger.error("Error creating floor packet.");
            throw new RuntimeException("Error creating floor packet.");
        }
    }

    /**
     * Sends a request packet to the chosen elevator.
     *
     * @param packet The packet to be sent
     */
    private void sendRequest(DatagramPacket packet){
        state = SchedulerStateEnum.IDLE;
        try {
            printPacketRequest(packet);
            socket.send(packet);
        } catch (IOException e) {
            logger.error("Caught an exception trying to send packet.");
            throw new RuntimeException("Caught an exception trying to send packet.");
        }
    }

    /**
     * Prints necessary information from the received packet.
     *
     * @param packet  The byte array of data received from Elevator
     * @param sender String name of the receiver
     */
    private void printPacketReceived(DatagramPacket packet, String sender) {
        logger.debug("Packet received from " + sender);
        logger.debug("From host port: " + packet.getPort());
        logger.debug("Containing: "+ Arrays.toString(packet.getData()));
    }

    /**
     * Prints necessary information from the packet request.
     *
     * @param packet  The byte array of data to be printed
     */
    private void printPacketRequest(DatagramPacket packet) {
        //Information prints
        logger.debug("Sending packet:");
        logger.debug("Destination host port: " + packet.getPort());
        logger.debug("Containing: " + Arrays.toString(packet.getData()));
    }
}