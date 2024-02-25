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
import java.util.concurrent.TimeUnit;

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
        if (args.length < 1) {
            System.err.println("Usage: java FloorSubsystem <input_file> <num_of_floors>");
            System.exit(1);
        }

        for (int i = 0; i < Integer.parseInt(args[1]); i++){
            dataArray.add(new LinkedList<>());
        }

        String inputFile = args[0];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 4) {
                    processInput(parts);
                } else {
                    System.err.println("Invalid input format: " + line);
                }
            }

            // store info of each floor in scheduler
            for (int i = 0; i < Integer.parseInt(args[1]); i++){
                Floor floor = new Floor();
                saveFloorInScheduler(floor);
                floors.add(floor);
            }

            saveFloorInScheduler(null);

            TimeUnit.SECONDS.sleep(3);

            // start floor threads
            for (Floor floor : floors){
                new Thread(floor).start();
            }

            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            e.printStackTrace();
        } catch (ParseException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Process input line and add request data to the data array.
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
        dataArray.get(floorNum - 1).add(new RequestData(currentDate.getTime(), floorNum,
                (parts[2].equals("Up")) ? Direction.UP : Direction.DOWN, destFloorNum));
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
                                       (byte) floor.getPort()},
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
}