package test;

import org.junit.Before;
import org.junit.Test;
import src.Floor;
import src.FloorSubsystem;
import java.net.DatagramPacket;
import java.text.ParseException;
import static org.junit.Assert.*;

/**
 * This class is the test class for testing the functionality
 * of the scheduler class and its methods.
 */
public class FloorTests {
    private static Floor floor;
    private static final int numFloors = 3;

    /**
     * Initializes the testing environment with a default parameter
     * of 3 floors.
     */
    @Before
    public void setUp(){
        floor = new Floor("test");
    }

    /**
     * This method tests that the floor class can create a new
     * packet to transfer information.
     */
    @Test
    public void testCreatePacket(){
        DatagramPacket data = floor.createPacket(numFloors);
        assertNotNull(data);
        assertEquals((byte) numFloors, data.getData()[0]);
    }

    /**
     * This method tests that the floor class can parse the
     * given packet and process the information.
     */
    @Test
    public void testParse() throws ParseException {
        String[] test = {"23:16:10.0", "1", "Up", "2", "0"};
        FloorSubsystem.addDataArray(3);
        FloorSubsystem.processInput(test);
        assertNotNull(FloorSubsystem.getDataArray());
    }
}