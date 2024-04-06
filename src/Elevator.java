package src;

import lombok.Getter;
import lombok.Setter;

import java.net.*;
import java.util.*;

/**
 * Elevator class which implements a single Thread representing
 * an elevator, with state and corresponding elevator subsystem.
 */
public class Elevator implements Runnable {
    private static final int MAX_NUM_OF_PASSENGERS = 10;
    private static final int SCHEDULER_PORT = 64;
    private final Logger logger;
    private static int nextPortNum = 66;
    private static int nextId = 1;
    @Getter
    private final int port;
    @Getter
    private final int id;
    @Getter
    private Motor motor;
    @Getter
    private Door door;
    @Getter
    private Display display;
    @Getter
    private List<ElevatorButton> buttons = new ArrayList<>();
    @Getter
    private List<ElevatorLamp> lamps = new ArrayList<>();
    @Getter
    @Setter
    private int currentFloor;
    @Getter
    private int destinationFloor;
    @Getter
    private int numOfPassengers;
    @Getter
    private Calendar firstRequestTimestamp;
    @Getter
    private Calendar lastCompletedRequestTimestamp;
    @Getter
    private ElevatorStateMachine state;
    private boolean shutdown;

    //Queue of current requests
    PriorityQueue<Integer> requested = new PriorityQueue<>();
    private ArrayList<Integer> passengerDestination = new ArrayList<>();

    //Reference to subsystem utilized for synchronization
    private ElevatorSubsystem subsystem;

    /**
     * Returns the next utilized port number for the elevator.
     * @return Next port number.
     */
    static synchronized int getNextPortNum() {
        return nextPortNum++;
    }

    /**
     * Class constructor which assigns all instance variables
     * and creates buttons/lamps/motor/door.
     *
     * @param subsystem The corresponding elevator subsystem.
     * @param id ID of the elevator to be constructed.
     */
    public Elevator(ElevatorSubsystem subsystem, int id) {
        this.port = nextPortNum;
        getNextPortNum();
        this.subsystem = subsystem;
        this.id = id;
        motor = new Motor(this, subsystem.getConfig());
        door = new Door(subsystem.getConfig());
        display = new Display(this);
        currentFloor = 1;
        destinationFloor = 0;
        numOfPassengers = 0;
        shutdown = false;
        firstRequestTimestamp = null;
        lastCompletedRequestTimestamp = null;
        state = new ElevatorStateMachine();
        state.setState(new IdleState(state));//elevator initialized to idle.

        for (int i = 0; i <= subsystem.getNumFloors(); i++){
            buttons.add(new ElevatorButton(i));
            lamps.add(new ElevatorLamp(i));
        }
        this.logger = new Logger(System.getProperty("user.home") + "/elevator" + id + ".log");
    }

    /**
     * Delay method utilized to pause elevator when no requests
     * are available or elevator is in an error state.
     */
    public synchronized void pause(){
        try{
            this.wait();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public synchronized  void addRequest(int floorNum) {
        requested.offer(floorNum);
        //sortRequestedFloors(requested);
        notifyAll();
    }
    /**
     * Interface Runnable method which executes upon start of thread.
     * Runs forever and checks for requests added to the queue, then
     * updated elevator state accordingly.
     */
    @Override
    public void run() {
        int i = 0;
        while(!shutdown){

            if(requested.isEmpty()){
                state.setState(new IdleState(state));
                pause();
            }else{
                if (i == 0) {
                    firstRequestTimestamp = Calendar.getInstance();
                    i = 1;
                }

                int floorNum = requested.peek();
                state.setState((this.currentFloor >= floorNum) ?
                        new Moving_down(state) :
                        new Moving_up(state));
                move();
                lastCompletedRequestTimestamp = Calendar.getInstance();
                sendUpdate();
            }
        }
    }

    /**
     * Parses received data and updates attributes accordingly
     */
    public synchronized void parseRequest(DatagramPacket packet){
        printPacketReceived(packet);
        int floorNum = packet.getData()[0];
        if(floorNum != currentFloor){
            lamps.get(floorNum - 1).turnOn();
        }
        if(packet.getData()[2] != 0){
            switch(packet.getData()[2]){
                case (byte) 1:
                    try{
                        ElevatorState currentState = state.getState();
                        logger.debug("TRANSIENT ERROR DETECTED: PAUSED");
                        state.setState(new Timeout(state));
                        this.getDisplay().display();
                        this.getDisplay().countdown();
                        Thread.sleep(10000);
                        state.setState(currentState);
                        this.getDisplay().display();
                        logger.debug("TRANSIENT ERROR RESOLVED: CONTINUING");
                        break;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                case (byte) 2:
                    logger.debug("FATAL ERROR DETECTED: CEASED OPERATION");
                    state.setState(new FaultState(state));
                    this.getDisplay().display();
                    shutdown = true;
                    Thread.currentThread().interrupt();
                    return;
            }
        }
        addRequest(floorNum);
    }

    /**
     * Moves to the first element in the requested ArrayList
     *
     */
    public void move(){
        synchronized (requested){
            int floorNum = requested.peek();
            logger.debug("Closing doors at floor " + currentFloor);
            door.close();
            logger.debug("Moving to floor " + floorNum + " from floor " + currentFloor);
            state.setState((this.currentFloor >= floorNum) ?
                    new Moving_down(state) :
                    new Moving_up(state));
            motor.move(floorNum, passengerDestination);

            logger.debug("Opening doors at floor " + currentFloor);
            door.open();
            if(subsystem.getConfig().getNumFloors() > floorNum) {
                lamps.get(floorNum-1).turnOff();
            }
            logger.debug("Loading/unloading elevator at floor " + currentFloor);
            state.setState(new UnloadingLoading(state));
            this.getDisplay().display();
            //Sleep for necessary elevator loading time
            try {
                Thread.sleep(subsystem.getConfig().getLoadingTime());
            }catch(InterruptedException e){
                throw new RuntimeException(e);
            }

            state.setState(new IdleState(state));
            this.getDisplay().display();
            requested.remove(floorNum);
        }
    }
    /**
     * Sorts a list of requested floors based on their proximity to the current floor.
     * Floors above the current floor are sorted in ascending order of distance,
     * while floors below are sorted in descending order of distance.
     *
     * @param requested The list of floors requested by users.
     */
    public synchronized void sortRequestedFloors(Queue<Integer> requested) {

//        requested.sort(Comparator.comparingInt(floor -> {
//            int distance = Math.abs(floor - currentFloor);
//            boolean isDirectionUp = floor > currentFloor;
//            return isDirectionUp ? distance : -distance;
//        }));
    }
    /**
     * Sends an update packet to the Scheduler indicating that the doors are open.
     * This method creates a packet with the specified update type and sends it through the socket.
     */
    public void sendUpdate() {
        subsystem.sendSchedulerPacket(createPacket(UpdateType.OPEN_DOORS));
    }

    /**
     * Creates a DatagramPacket for sending updates to the Scheduler.
     * Packet Format:
     * first byte  : updateType ordinal number (Direction 1 = UP : 0 = DOWN)
     * second byte : currentFloor
     * @param updateType The type of update to include in the packet
     * @return The DatagramPacket to be sent
     */
    public DatagramPacket createPacket(UpdateType updateType) {
        try {
            printPacketRequest(updateType);
            byte [] data = new byte[3];
            data[0] = (byte) port;
            data[1] = (byte) currentFloor;
            return new DatagramPacket(data, data.length,
                    InetAddress.getLocalHost(), SCHEDULER_PORT);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks whether the elevator is overloaded based on the number of passengers.
     *
     * @return True if the elevator is overloaded, otherwise false
     */
    private boolean isOverloaded() {
        return numOfPassengers > MAX_NUM_OF_PASSENGERS;
    }

    /**
     * Prints necessary information from the received packet.
     *
     * @param packet  The byte array of data received from Elevator
     */
    private void printPacketReceived(DatagramPacket packet){
        logger.debug("RECEIVED: " + Arrays.toString(packet.getData()));
    }

    /**
     * Prints necessary information from the received packet.
     *
     * @param updateType  The elevator updateType
     */
    private void printPacketRequest(UpdateType updateType){
        logger.debug("SENDING: " + updateType);
    }
}