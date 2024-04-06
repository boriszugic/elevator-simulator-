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
    private SchedulerStateMachine state;
    private ArrayList<DatagramPacket> requestQueue;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private Scheduler() {
        state = new SchedulerStateMachine();
        state.setState(new Idle()); //Initialize scheduler in IDLE state
        try {
            this.socket = new DatagramSocket(port);
            this.elevators = new HashMap<>();
            this.floors = new HashMap<>();
        } catch (SocketException e) {
            logger.error("Error creating DatagramSocket");
            throw new RuntimeException("Error creating DatagramSocket", e);
        }
        requestQueue = new ArrayList<>();
    }

    /**
     * Test constructor which does not initialize UDP elements; to be utilized in
     * SchedulerTests.Java for JUnit4 testing
     */
    public Scheduler(String test) {
        state.setState(new Idle()); //Initialize scheduler in IDLE state
        socket = null;
        this.elevators = new HashMap<>();
        this.floors = new HashMap<>();
        requestQueue = new ArrayList<>();
    }

    /** Singleton instance */
    private static final Scheduler instance = new Scheduler();

    /**
     * Main executable function which initializes all floors/elevators before starting
     * a scheduler thread.
     *
     * @param args Default parameter
     */
    public static void main(String[] args){
        Scheduler scheduler = Scheduler.getInstance();
        initializeFloorsAndElevators();
        printFloorAndElevatorInfo(scheduler);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(scheduler).start();

    }

    /**
     * Initializes all floors and elevators by constantly checking socket and
     * receives information to create simple structures with information necessary
     * to model each elevator and floor instance.
     */
    private static void initializeFloorsAndElevators() {
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
                    ElevatorStateMachine elevatorStateMachine   = new ElevatorStateMachine();
                    Scheduler.getInstance().getElevators().put((int) data[2],new ElevatorStructure(
                            data[0], elevatorStateMachine,
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
            requestQueue.add(waitRequest());
            if(!requestQueue.isEmpty()){
                sendRequest(parseRequest(requestQueue.removeFirst()));
            }
        }
    }

    /**
     * Waits for a request from Floor and Elevator threads.
     */
    private DatagramPacket waitRequest() {
        DatagramPacket receivedPacket = new DatagramPacket(new byte[4], 4);
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
    public DatagramPacket parseRequest(DatagramPacket packet){
        byte[] data = packet.getData();
        state.setState(new Scheduling());
        if (packet.getLength() == 3 && data[2] == 0){ // Elevator response
            return scheduleRequest(packet);
        }
        else if (packet.getLength() == 4 && isValid(data)){ //Elevator packet request to retrieve elevator
            return requestElevator(packet);
        }
        else if (packet.getLength() == 2) {
            return serveRequest(packet);
        }
        // Error checking
        logger.error("Invalid request (Improper format).");
        throw new RuntimeException("Invalid request (Improper format).");
    }

    /** Handles elevator updates requests to floor
     *
     * @param packet The DatagramPacket that is received
     * @return The appropriate DatagramPacket to be sent
     */
    private DatagramPacket scheduleRequest(DatagramPacket packet) {
        byte[] data = packet.getData();
        byte[] updateData = new byte[]{data[0]};
        printPacketReceived(packet,"Elevator");
        elevators.get((int)data[0]).setCurrFloor(data[1]);
        return createFloorPacket(updateData, packet.getData()[1]);
    }

    /** Handles floor requests for an elevator
     *
     * @param packet The DatagramPacket that is received
     * @return The appropriate DatagramPacket to be sent
     */
    private DatagramPacket requestElevator(DatagramPacket packet) {
        byte[] data = packet.getData();
        printPacketReceived(packet,"Floor");
        // Choose elevator
        ElevatorStructure elevator = chooseElevator((data[0] == 0 ?
                                                    Direction.DOWN :
                                                    Direction.UP),
                                                    data[1]);
        // Assign floor the chosen elevator
        int floorNum = data[1];
        floors.get((int) data[1]).setElevatorPort(elevator.getPort());
        return createElevatorPacket(floorNum, elevator.getPort(), data[3]);
    }

    /** Handles destination requests from floor to elevator
     *
     * @param packet The DatagramPacket that is received
     * @return The appropriate DatagramPacket to be sent
     */
    private DatagramPacket serveRequest(DatagramPacket packet) {
        byte[] data = packet.getData();
        //Floor request when passenger pressed elevator button
        printPacketReceived(packet, "Floor");
        int floorNum = data[0];
        // Create a packet to elevator port assigned to floor in the if statement above
        return createElevatorPacket(floorNum,
                                    floors.get((int)data[1]).getElevatorPort(),
                              0);
    }

    /**
     * Validates formatted data for elevator requests
     *
     * @param data
     * @return false if any of the conditions are triggered, true otherwise
     */
    public boolean isValid(byte[] data) {
        if (data.length != 4) { //Checks for sufficient length of data
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
        if(suitableElevator.isPresent()) {
            ElevatorStateMachine stateMachine = suitableElevator.get().getState();
            if (suitableElevator.get().getCurrFloor() - floorNum != 0) {
                ElevatorState newState = suitableElevator.get().getCurrFloor() >
                        floorNum ?
                        new Moving_up(stateMachine) :
                        new Moving_down(stateMachine);
                stateMachine.setState(newState);
                return suitableElevator.get();

            }
        }
        //TODO: timeout fault check
        //TODO: fix edge case with (6 down 1, 6 down 1, 6 down 1) all going to one elevator.
        else{
            int index = 0;
            ElevatorStructure functionalElevator = elevs.getFirst();
            while (functionalElevator.getState().getState().toString().equals("Fault")) {
                elevs.removeFirst();
                functionalElevator = elevs.getFirst();
            } //Make a check for timeout state in the future.
                ElevatorStateMachine stateMachine = functionalElevator.getState();
                ElevatorState newState = functionalElevator.getCurrFloor() >
                        floorNum ? new Moving_up(stateMachine) : new Moving_down(stateMachine);
                stateMachine.setState(newState);

                logger.debug("Chose Elevator " + functionalElevator.getId());
            System.out.println(functionalElevator.getId());
                return functionalElevator;
            }

        logger.debug("Chose Elevator " + suitableElevator.get().getId());
        ElevatorStateMachine stateMachine = suitableElevator.get().getState();
        ElevatorState newState = suitableElevator.get().getCurrFloor() >
                floorNum ? new Moving_up(stateMachine) : new Moving_down(stateMachine);
        stateMachine.setState(newState);
        return suitableElevator.get(); // Return the found elevator
    }

    /**
     * Takes a given ElevatorStructure along with several parameters and determines if
     * an elevator is suitable for use based on those parameters.
     *
     * @param elevator The given instance of an elevator
     * @param direction The direction of the request
     * @param floorNum The floor number of the request
     * @return True if elevator is suitable, false otherwise
     */
    private boolean isElevatorSuitable(ElevatorStructure elevator, Direction direction, int floorNum) {
        switch(elevator.getState().getState().toString()) {
            case "Idle":
                return true; // An idle elevator is always suitable
            case "Moving_up":
                return direction == Direction.UP && elevator.getCurrFloor() <= floorNum;
            case "Moving_down":
                return direction == Direction.DOWN && elevator.getCurrFloor() >= floorNum;
            default:
                return false; // If the elevator is in a state that doesn't allow it to take new requests
        }
    }

    /**
     * Creates a {@code DatagramPacket} formatted to be sent to the Elevator subsystem.
     * The packet contains the floor number, elevator ID, and an error code, encapsulated within
     * the byte array of the packet.
     *
     * @param floorNum The floor number to be sent to the Elevator subsystem.
     * @param id The identifier for the elevator to which this packet is addressed.
     * @param error The error code to be sent, representing any potential issues or status updates.
     * @return The created {@code DatagramPacket} containing the floor number, elevator ID, and error code.
     * @throws RuntimeException If there is an error in creating the packet due to an unknown host.
     */
    public DatagramPacket createElevatorPacket(int floorNum, int id, int error){
        try {
            byte[] data = new byte[3];
            data[0] = (byte) floorNum;
            data[1] = (byte) id;
            data[2] = (byte) error;
            if(error == 2){
                ElevatorStateMachine tempStateMachine = elevators.get(id).getState();
                tempStateMachine.setState(new FaultState(tempStateMachine));
                elevators.get(id).setState(tempStateMachine);
            }
            return new DatagramPacket(data, data.length,
                    InetAddress.getLocalHost(), elevator_port);
        } catch (UnknownHostException e) {
            logger.error("Error creating elevator packet.");
            throw new RuntimeException("Error creating elevator packet.");
        }
    }

    /**
     * Creates a {@code DatagramPacket} formatted to be sent to the Floor subsystem
     * which contains the Elevator {@code UPDATETYPE}. This method constructs the packet
     * using the specified data and destination port.
     *
     * @param data The byte array of data received from the Elevator to be wrapped in the packet.
     * @param port The destination port number where the packet is to be sent.
     * @return The created {@code DatagramPacket} ready to be sent to the Floor subsystem.
     * @throws RuntimeException If there is an error in creating the packet due to an unknown host.
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
        state.setState(new Idle());
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
        logger.debug("Sending packet:");
        logger.debug("Destination host port: " + packet.getPort());
        logger.debug("Containing: " + Arrays.toString(packet.getData()));
    }
}