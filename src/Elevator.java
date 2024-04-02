package src;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

/**
 * Elevator class which implements a single Thread representing
 * an elevator, with state and corresponding elevator subsystem.
 */
public class Elevator implements Runnable {

    // Constants
    private static final int MAX_NUM_OF_PASSENGERS = 10;
    private static final int SCHEDULER_PORT = 64;
    //Logger utilized for debugging
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

    //Queue of current requests
    private ArrayList<Integer> requested = new ArrayList<>();
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
     * @param ID ID of the elevator to be constructed.
     */
    public Elevator(ElevatorSubsystem subsystem, int ID) {
        this.port = nextPortNum;
        getNextPortNum();
        this.subsystem = subsystem;
        this.id = ID;
        motor = new Motor(this, subsystem.getConfig());
        door = new Door(subsystem.getConfig());
        display = new Display(this);
        currentFloor = 1;
        display.display(String.valueOf(currentFloor));
        destinationFloor = 0;
        numOfPassengers = 0;
        state = ElevatorStateEnum.IDLE; //elevator initialized to idle.

        for (int i = 0; i <= subsystem.getNumFloors(); i++){
            buttons.add(new ElevatorButton(i));
            lamps.add(new ElevatorLamp(i));
        }
        this.logger = new Logger(System.getProperty("user.home") + "/elevator" + ID + ".log");
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

    /**
     * Interface Runnable method which executes upon start of thread.
     * Runs forever and checks for requests added to the queue, then
     * updated elevator state accordingly.
     */
    @Override
    public void run() {
        while(true){
            if(requested.isEmpty()){
                pause();
            }else {
                int floorNum = requested.removeFirst();
                System.out.println("Elevator MOVING TO " + floorNum);
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
        lamps.get(floorNum - 1).turnOn();
        if((int)packet.getData()[2] != 0){
            switch(packet.getData()[2]){
                case (byte) 1:
                    try{
                        logger.debug("TRANSIENT ERROR DETECTED: PAUSED");
                        Thread.sleep(10000);
                        logger.debug("TRANSIENT ERROR RESOLVED: CONTINUING");
                        break;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                case (byte) 2:
                    try{
                        logger.debug("FATAL ERROR DETECTED: CEASED OPERATION");
                        Thread.sleep(100000000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
            }
        }
        requested.add(floorNum);
        System.out.println("System test" + requested);
        notifyAll();
    }

    /**
     * Moves to Floor #floorNum
     *
     * @param floorNum floor number to move to
     */
    public void move(int floorNum){
        logger.debug("Closing door of elevator:" + id);
        door.close();
        logger.debug("Moving to floor " + floorNum + " from currentFloor " + currentFloor);
        motor.move(floorNum, passengerDestination);
        //requested.removeFirst();
        logger.debug("Opening doors of elevator " + id + " at floor " + currentFloor);
        door.open();
        state = ElevatorStateEnum.LOADING_UNLOADING;
        if(subsystem.getConfig().getNumFloors() > floorNum) {
            lamps.get(floorNum-1).turnOff();
        }
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