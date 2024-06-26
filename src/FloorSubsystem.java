package src;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Class representing a Floor Subsystem which attempts to read an input file to
 * determine each input before storing them in a ArrayList and initializing each
 * Floor instance based on the given configurations.
 */
@Getter
public class FloorSubsystem {
    @Getter
    static ArrayList<LinkedList<RequestData>> dataArray = new ArrayList<>();
    static ArrayList<Floor> floors = new ArrayList<>();

    /**
     * Main method to read input, initialize floors, and start threads.
     * @param args Command line arguments: <input_file> <num_of_floors>
     */
    public static void main(String[] args) {
        ConfigurationReader config;
        try {
            config = new ConfigurationReader("./config.json");
        } catch (IOException | org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }

        String inputFile = config.getInputFile();

        for (int i = 1; i <= config.getNumFloors(); i++){
            dataArray.add(new LinkedList<>());
        }

        try {
            //Attempts to parse given input file and assign each input to appropriate floor
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 5) {
                    processInput(parts);
                } else {
                    System.err.println("Invalid input format: " + line);
                }
            }
            // store info of each floor in scheduler
            for (int i = 0; i < config.getNumFloors(); i++){
                Floor floor = new Floor();
                saveFloorInScheduler(floor);
                floors.add(floor);
            }

            //Inform scheduler that floors have been initialized
            saveFloorInScheduler(null);

            // wait until Scheduler is done initializing
            DatagramPacket receivedPacket = new DatagramPacket(new byte[3], 3);
            DatagramSocket socket = new DatagramSocket(150);
            socket.receive(receivedPacket);
            socket.close();

            // start floor threads
            for (Floor floor : floors){
                new Thread(floor).start();
            }

            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Process input line and add request data to the data array.
     *
     * @param parts Input line split into parts
     * @throws ParseException If input parsing fails
     */
    public static void processInput(String[] parts) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        calendar.setTime(sdf.parse(parts[0]));

        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        currentDate.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        currentDate.set(Calendar.SECOND, calendar.get(Calendar.SECOND));

        int floorNum = Integer.parseInt(parts[1]);
        int destFloorNum = Integer.parseInt(parts[3]);
        int error = Integer.parseInt(parts[4]);
        dataArray.get(floorNum - 1).add(new RequestData(currentDate.getTime(), floorNum,
                (parts[2].equals("Up")) ? Direction.UP : Direction.DOWN, destFloorNum, error));
    }

    /**
     * Save floor information in scheduler.
     * @param floor Floor object containing floor information
     */
    private static void saveFloorInScheduler(Floor floor) {
        try {
            // end of initialization stage
            if (floor == null) {
                new DatagramSocket().send(new DatagramPacket(new byte[]{0}, 1,
                                          InetAddress.getLocalHost(), 64));
            } else {
                // Create and send DatagramPacket containing floor information
                floor.getSocket().send(new DatagramPacket(new byte[]{
                                       (byte) floor.getFloorNum(),
                                       (byte) floor.getId()},
                                 2, InetAddress.getLocalHost(), 64));
            }
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the requests for a specific floor.
     * @param floorNum Floor number
     * @return LinkedList containing requests for the specified floor
     */
    public static LinkedList<RequestData> getRequests(int floorNum) {
        return dataArray.get(floorNum - 1);
    }

    /**
     * Testing method utilized for establishing data array
     * without input file.
     *
     * @param floors Number of floors to add
     */
    public static void addDataArray(int floors){
        for (int i = 1; i <= floors; i++){
            dataArray.add(new LinkedList<>());
        }
    }
}