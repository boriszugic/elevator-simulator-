package src;

import lombok.Getter;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Class representing an Elevator subsystem which initializes and keeps record
 * of each elevator based on the configurations given.
 *
 * Utilizing the receiveRequestThread, each received request shall be redirected
 * to the elevator based on the given ID.
 */
public class ElevatorSubsystem implements Runnable{
    //Constants representing the scheduler and elevator subsystem socket ports
    private static final int SCHEDULER_PORT = 64;
    private static final int PORT = 65;
    //Hashmap containing references to each elevator
    @Getter
    private HashMap<Integer, Elevator> elevators;
    //Request thread utilized for receiving from socket
    private Thread requestThread;
    @Getter
    private static DatagramSocket socket;
    private final Logger logger;
    //Configuration file containing all necessary instantiation variables
    @Getter
    private final ConfigurationReader config;
    @Getter
    private int numFloors;
    @Getter
    ElevatorGUI gui;

    public ElevatorSubsystem(ConfigurationReader config){
        Elevator elevator;
        Thread temp;
        this.config = config;
        this.numFloors = config.getNumFloors();
        elevators = new HashMap<>();
        //Logger debug files can be found in the primary user directly
        this.logger = new Logger(System.getProperty("user.home") + "/elevator_subsystem.log");
        try {
            //Creates socket with port 65 for scheduler communication
            socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            logger.error("Error creating DatagramSocket");
            throw new RuntimeException("Error creating DatagramSocket", e);
        }

        //Create elevators and send info to scheduler based on config values
        for (int i = 1; i <= config.numElevators; i++){
            elevator = new Elevator(this, i);  //Establish new elevator with appropriate ID
            saveElevatorInScheduler(elevator);
            elevators.put(i, elevator);
            temp = new Thread(elevator, "elevator" + i);
            temp.start();
        }
        //Inform scheduler that all elevators are initialized
        saveElevatorInScheduler(null);

        gui = new ElevatorGUI(this);
        for (int i = 1; i <= elevators.size(); i++){
            elevators.get(i).getDisplay().setGui(gui);
            elevators.get(i).getDisplay().display();
        }
        gui.setVisible(true);
    }

    /**
     * Sends elevator information to the Scheduler.
     * Format
     * 1st byte: 0 if Idle, 1 if Moving
     * 2nd byte: current floor
     * 3rd byte: receiving socket port
     *
     * @param elevator The Elevator object containing the information to be sent.
     */
    private static void saveElevatorInScheduler(Elevator elevator) {
        try {
            // end of initialization stage
            if (elevator == null) {
                socket.send(new DatagramPacket(new byte[]{1}, 1,
                            InetAddress.getLocalHost(), SCHEDULER_PORT));
            }else{
                socket.send(new DatagramPacket(new byte[]{
                            (byte) elevator.getId(),
                            (byte) elevator.getCurrentFloor(),
                            (byte) elevator.getPort()},
                            3, InetAddress.getLocalHost(),
                            SCHEDULER_PORT));
            }
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Receives UDP DatagramPacket from scheduler and calls
     * method to send packet to appropriate elevator based on ID.
     *
     * @throws IOException
     */
    public void receiveRequest() throws IOException{
        DatagramPacket receivedPacket = new DatagramPacket(new byte[3], 3);
        try {
            socket.receive(receivedPacket);
        } catch (IOException e) {
            socket.close();
            throw new RuntimeException(e);
        }
        sendElevatorPacket(receivedPacket);
    }

    /**
     * Parses given DatagramPacket for elevator ID and attempts to retrieve elevator
     * with corresponding ID, then instructs elevator to parse request and take
     * corresponding action based on the information.
     *
     * @param received The received DatagramPacket from the scheduler
     */
    public synchronized void sendElevatorPacket(DatagramPacket received){
        int id = received.getData()[1];
        Elevator elevator = elevators.get(id-PORT);
        elevator.parseRequest(received);
    }

    /**
     * Takes packets from individual elevators and utilizes socket
     * to send each packet to scheduler.
     *
     * @param received The DatagramPacket to be sent.
     */
    public synchronized void sendSchedulerPacket(DatagramPacket received){
        try{
            socket.send(received);
        } catch(IOException e){
            socket.close();
            throw new RuntimeException(e);
        }
    }

    /**
     * Implements interface Runnable to start a new request thread
     * which attempts to receive packets from socket forever.
     */
    @Override
    public void run(){
        requestThread = new Thread(new receiveRequestThread(this));
        requestThread.start();
    }

    /**
     * Main executable function which initializes the configuration reader and
     * corresponding subsystem threads.
     *
     * @param args Default main parameter
     * @throws IOException
     * @throws ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {
        ElevatorSubsystem subsystem;
        Thread subsystemThread;
        ConfigurationReader config;

        config = new ConfigurationReader("./config.json");

        subsystem = new ElevatorSubsystem(config);
        subsystemThread = new Thread(subsystem);
        subsystemThread.start();
    }
}