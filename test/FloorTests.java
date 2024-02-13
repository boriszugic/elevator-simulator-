package test;

import org.junit.Before;
import org.junit.Test;
import src.Floor;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * This class is the test class for testing the functionality
 * of the scheduler class and its methods.
 */
public class FloorTests {
    ArrayList<Floor> floors = new ArrayList<>();
    private static final int numFloors = 3;

    /**
     * Initializes the testing environment with a default parameter
     * of 3 floors.
     */
    /*
    @Before
    public void setUp(){
        for (int i = 1; i <= numFloors; i++){
            Floor floor = new Floor();
            floors.add(floor);
            new Thread(floor).start();
        }
    }
    */
    /**
     * This method tests that the floors are initialized in the proper state.
     */
    /*
    @Test
    public void testInit(){
        for (int i = 1; i <= numFloors; i++){
            assertEquals(floors.get(i).getport(), i);
            assertEquals(floors.get(i).getfloorNum(), i);
        }
    }
*/
    /**
     * This method tests that the floor class can create a new
     * packet to transfer information.
     */
    @Test
    public void testCreatePacket(){
        //TBD
    }

    /**
     * This method tests that the floor class can parse the
     * given packet and process the information.
     */
    @Test
    public void testParse(){
        //TBD
    }
}
