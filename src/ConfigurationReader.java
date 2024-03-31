package src;

import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for reading a JSON configuration file and saving variables for use within
 * other classes based on the given configurations.
 */
public class ConfigurationReader {

    @Getter
    public final int numElevators;
    @Getter
    public final int numFloors;

    @Getter
    public final int openDoorTime;
    @Getter
    public final int closeDoorTime;
    @Getter
    public final int movingTime;
    @Getter
    public final int loadingTime;
    @Getter
    public final int doorsBlockedTime;
    @Getter
    public final String inputFile;

    /**
     *
     * @param filePath
     * @throws IOException
     * @throws ParseException
     */

    /**
     * Constructor which takes a filePath and attempts to parse the given
     * file and determine config variables.
     *
     * @param filePath The filepath of the configurations.
     * @throws IOException
     * @throws ParseException
     */
    public ConfigurationReader(String filePath) throws IOException, ParseException {
        // Parse the configuration file
        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader(filePath));

        // Get values of each field
        this.numFloors = ((Long) config.get("floors")).intValue();
        this.numElevators = ((Long) config.get("elevators")).intValue();
        this.inputFile = (String) config.get("input_file");
        this.openDoorTime = ((Long) config.get("door_open_time")).intValue();
        this.closeDoorTime = ((Long) config.get("door_close_time")).intValue();
        this.movingTime = ((Long) config.get("moving_time")).intValue();
        this.loadingTime = ((Long) config.get("loading_time")).intValue();
        this.doorsBlockedTime = ((Long) config.get("doors_blocked_time")).intValue();
    }

//    public static void main(String[] args) {
//        try {
//            launchProcess("./compile.bat");
//            TimeUnit.SECONDS.sleep(5);
//
//            // Parse the configuration file
//            JSONParser parser = new JSONParser();
//            JSONObject config = (JSONObject) parser.parse(new FileReader("config.json"));
//
//            // Get values of each field
//            int numOfFloors = ((Long) config.get("floors")).intValue();
//            int numOfElevators = ((Long) config.get("elevators")).intValue();
//            String input_file = (String) config.get("input_file");
//
//            // Launch the scheduler, elevators, and floors
//            launchScheduler();
//            launchElevators(numOfElevators, numOfFloors);
//            launchFloors(input_file, numOfFloors);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Launches the floor subsystem with the specified input file and number of floors.
//     *
//     * @param inputFile   The input file for the floor subsystem
//     * @param numOfFloors The number of floors in the building
//     * @throws Exception if an error occurs while launching the process
//     */
//    private static void launchFloors(String inputFile, int numOfFloors) throws Exception {
//        launchProcess("java", "src/FloorSubsystem", inputFile, String.valueOf(numOfFloors));
//    }
//
//    /**
//     * Launches the elevator subsystem with the specified number of elevators and number of floors.
//     *
//     * @param numOfElevators The number of elevators in the building
//     * @param numOfFloors    The number of floors in the building
//     * @throws Exception if an error occurs while launching the process
//     */
//    private static void launchElevators(int numOfElevators, int numOfFloors) throws Exception {
//        launchProcess("java", "src/Elevator", String.valueOf(numOfElevators), String.valueOf(numOfFloors));
//    }
//
//    /**
//     * Launches the scheduler subsystem.
//     *
//     * @throws Exception if an error occurs while launching the process
//     */
//    private static void launchScheduler() throws Exception {
//        launchProcess("java", "src/Scheduler");
//    }
//
//    /**
//     * Launches a process with the specified commands.
//     *
//     * @param commands The commands to execute
//     * @throws Exception if an error occurs while launching the process
//     */
//    private static void launchProcess(String... commands) throws Exception {
//        List<String> commandList = new ArrayList<>();
//        commandList.addAll(Arrays.asList(commands));
//        ProcessBuilder builder = new ProcessBuilder(commandList);
//        builder.directory(new File(System.getProperty("user.dir")));
//        builder.inheritIO(); // Redirects the output to the current console
//        Process process = builder.start();
//    }
}