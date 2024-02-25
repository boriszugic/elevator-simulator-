package test;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import src.Floor;
import src.FloorSubsystem;
import src.RequestData;

import java.net.DatagramPacket;
import java.text.ParseException;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * This class is the test class for testing the functionality
 * of the scheduler class and its methods.
 */
public class FloorTests {
    private static Floor floor;
    private static final int numFloors = 3;
    private static FloorSubsystem subFloor;

    /**
     * Initializes the testing environment with a default parameter
     * of 3 floors.
     */

    @Before
    public void setUp(){
        floor = new Floor();
        String[] args = {"testinput.txt", "2"};
        FloorSubsystem.main(args);
    }

    /**
     * This method tests that the floor class can create a new
     * packet to transfer information.
     */
    @Test
    public void testCreatePacket(){
        DatagramPacket data = floor.createPacket(numFloors);
        assertNotNull(data);
    }

    /**
     * This method tests that the floor class can parse the
     * given packet and process the information.
     */
    @Test
    public void testParse() throws ParseException {
        String[] test = {"23:16:10.0", "1", "Up", "2"};
        FloorSubsystem.processInput(test);
        assertNotNull(FloorSubsystem.getDataArray());
    }
}
