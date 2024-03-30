package src;

import lombok.Getter;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

public class ElevatorSubsystem implements Runnable{

    private static final int SCHEDULER_PORT = 64;

    private static final int port = 65;
    @Getter
    private HashMap<Integer, Elevator> elevators;
    private Thread requestThread;
    @Getter
    private static DatagramSocket socket;
    private final Logger logger;
    @Getter
    private final ConfigurationReader config;

    @Getter
    private int numFloors;
    public ElevatorSubsystem(ConfigurationReader config){
        Elevator elevator;
        Thread temp;
        this.config = config;
        this.numFloors = config.getNumFloors();
        elevators = new HashMap<>();
        this.logger = new Logger(System.getProperty("user.home") + "/elevator_subsystem.log");

        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            logger.error("Error creating DatagramSocket");
            throw new RuntimeException("Error creating DatagramSocket", e);
        }

        for (int i = 1; i <= config.numElevators; i++){
            elevator = new Elevator(this, i);  //Establish new elevator with appropriate ID
            saveElevatorInScheduler(elevator);
            elevators.put(i, elevator);
            temp = new Thread(elevator, "elevator" + i);
            temp.start();
        }
        saveElevatorInScheduler(null);
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
            } else {
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

    public synchronized void sendElevatorPacket(DatagramPacket received){
        int ID;
        Elevator elevator;

        ID = received.getData()[1];
        elevator = elevators.get(ID-65);
        elevator.parseRequest(received);
    }

    public synchronized void sendSchedulerPacket(DatagramPacket received){
        try{
            socket.send(received);
        } catch(IOException e){
            socket.close();
            throw new RuntimeException(e);
        }
    }
    public void run(){
        requestThread = new Thread(new receiveRequestThread(this));
        requestThread.start();
    }

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
