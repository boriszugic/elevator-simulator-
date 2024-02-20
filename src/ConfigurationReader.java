package src;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for reading a JSON configuration file and launching processes based on the configuration.
 */
public class ConfigurationReader {

    public static void main(String[] args) {
        try {
            launchProcess("./compile.bat");
            TimeUnit.SECONDS.sleep(5);

            // Parse the configuration file
            JSONParser parser = new JSONParser();
            JSONObject config = (JSONObject) parser.parse(new FileReader("config.json"));

            // Get values of each field
            int numOfFloors = ((Long) config.get("floors")).intValue();
            int numOfElevators = ((Long) config.get("elevators")).intValue();
            String input_file = (String) config.get("input_file");

            // Launch the scheduler, elevators, and floors
            launchScheduler();
            launchElevators(numOfElevators, numOfFloors);
            launchFloors(input_file, numOfFloors);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches the floor subsystem with the specified input file and number of floors.
     *
     * @param inputFile   The input file for the floor subsystem
     * @param numOfFloors The number of floors in the building
     * @throws Exception if an error occurs while launching the process
     */
    private static void launchFloors(String inputFile, int numOfFloors) throws Exception {
        launchProcess("java", "src/FloorSubsystem", inputFile, String.valueOf(numOfFloors));
    }

    /**
     * Launches the elevator subsystem with the specified number of elevators and number of floors.
     *
     * @param numOfElevators The number of elevators in the building
     * @param numOfFloors    The number of floors in the building
     * @throws Exception if an error occurs while launching the process
     */
    private static void launchElevators(int numOfElevators, int numOfFloors) throws Exception {
        launchProcess("java", "src/Elevator", String.valueOf(numOfElevators), String.valueOf(numOfFloors));
    }

    /**
     * Launches the scheduler subsystem.
     *
     * @throws Exception if an error occurs while launching the process
     */
    private static void launchScheduler() throws Exception {
        launchProcess("java", "src/Scheduler");
    }

    /**
     * Launches a process with the specified commands.
     *
     * @param commands The commands to execute
     * @throws Exception if an error occurs while launching the process
     */
    private static void launchProcess(String... commands) throws Exception {
        List<String> commandList = new ArrayList<>();
        commandList.addAll(Arrays.asList(commands));
        ProcessBuilder builder = new ProcessBuilder(commandList);
        builder.directory(new File(System.getProperty("user.dir")));
        builder.inheritIO(); // Redirects the output to the current console
        Process process = builder.start();
    }
}