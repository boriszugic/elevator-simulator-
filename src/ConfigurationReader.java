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
}