package test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import src.Elevator;
import java.util.ArrayList;


/**
 * This class is the test class for testing the functionality
 * of the elevator class.
 */
public class ElevatorTests {

    ArrayList<Elevator> elevators = new ArrayList<>();
    private static final int numFloors = 3;

    /**
     * Initialize the testing environment with two elevators.
     */
    /*
    @Before
    public void setUp(){
        for (int i = 1; i <= numFloors; i++){
            Elevator elevator = new Elevator(numFloors);
            elevators.add(elevator);
            new Thread(elevator).start();
        }
    }

    /**
     * This method tests that the elevators are initialized in the proper state.

    @Test
    public void testInit(){
        for (int i = 0; i < numFloors; i++){
            assertEquals(elevators.get(i).getCurrentFloor(), 1);
            assertEquals(elevators.get(i).getNumOfPassengers(), 1);
            assertEquals(elevators.get(i).getDestinationFloor(), 1);
        }
    }
    */
    /**
     * The method tests that an elevator is capable of changing the
     * stated floor via the move method.
     */
    /**
    @Test
    public void testMove(){
        assertEquals(elevators.get(0).move(2), true);
        assertEquals(elevators.get(0).getCurrentFloor(), 2);
        assertEquals(elevators.get(0).move(3), true);
        assertEquals(elevators.get(0).getCurrentFloor(), 3);
        assertEquals(elevators.get(0).move(4), false);
        assertEquals(elevators.get(0).getCurrentFloor(), 3);
        assertEquals(elevators.get(0).move(3), false);
        assertEquals(elevators.get(0).getCurrentFloor(), 3);
    }
*/
    /**
     * This method tests whether the elevators can process information
     * and subsequently change their attributes.
     */
    @Test
    public void testParse(){
        //TBD
    }
}
