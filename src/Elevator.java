package src;

import lombok.Getter;
import lombok.Setter;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class Elevator implements Runnable {

    // Constants
    private static final int MAX_NUM_OF_PASSENGERS = 10;
    private static final int SCHEDULER_PORT = 64;
    private final Logger logger;

    // Static variables
    private static int nextPortNum = 66;
    private static int nextId = 1;

    @Getter
    private final int port;

    // Instance variables
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
    private ElevatorStateEnum state;

    private ArrayList<Integer> requested = new ArrayList<>();

    private ElevatorSubsystem subsystem;

    static synchronized int getNextPortNum() {
        return nextPortNum++;
    }

    public Elevator(ElevatorSubsystem subsystem, int ID) {
        this.port = nextPortNum;
        getNextPortNum();
        this.subsystem = subsystem;
        this.id = ID;
        motor = new Motor(this);
        door = new Door();
        display = new Display(this);
        currentFloor = 1;
        display.display(String.valueOf(currentFloor));
        destinationFloor = 0;
        numOfPassengers = 0;
        state = ElevatorStateEnum.IDLE; //elevator initialized to idle.

        for (int i = 1; i <= subsystem.getNumFloors(); i++){
            buttons.add(new ElevatorButton(i));
            lamps.add(new ElevatorLamp(i));
        }
        this.logger = new Logger(System.getProperty("user.home") + "/elevator" + ID + ".log");
    }

    public synchronized void pause(){
        try{
            this.wait();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            if(requested.isEmpty()){
                pause();
            }else{
                int floorNum = requested.getFirst();
                state = (this.currentFloor >= floorNum) ? ElevatorStateEnum.MOVING_DOWN : ElevatorStateEnum.MOVING_UP;
                move(floorNum);
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
        if(currentFloor!=floorNum){requested.add(floorNum);}

        /**Request sorting algorithm --incomplete **/
//        requested.sort(Comparator.comparingInt(floor -> {
//            int distance = Math.abs(floor - currentFloor);
//            boolean isDirectionUp = floor > currentFloor;
//            return isDirectionUp ? distance : -distance;
//        }));
        notifyAll();
    }

    /**
     * Moves to Floor #floorNum
     *
     * @param floorNum floor number to move to
     */
    public void move(int floorNum){
        System.out.println("[port:"+port+"] requests: "+requested.toString());
        logger.debug("Moving to floor " + floorNum + " from currentFloor " + currentFloor);

        motor.move(floorNum);
        door.open();
        state = ElevatorStateEnum.LOADING_UNLOADING;
        requested.removeFirst();

    }

    /**
     * Sends an update packet to the Scheduler indicating that the doors are open.
     * This method creates a packet with the specified update type and sends it through the socket.
     */
    private void sendUpdate() {

        subsystem.sendSchedulerPacket(createPacket(UpdateType.OPEN_DOORS));
            System.out.println("Doors opened. boarding...");

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