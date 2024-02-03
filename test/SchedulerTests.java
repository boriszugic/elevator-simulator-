package test;

import org.junit.Before;
import org.junit.Test;
import src.*;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * This class is the test class for testing the functionality
 * of the scheduler class.
 */
public class SchedulerTests {
    Scheduler scheduler;
    private final int numFloors = 3;
    ArrayList<Object> testparam = new ArrayList<>();
    /**
     * Initialize the testing environment with the default
     * parameters for a scheduler.
     */
    @Before
    public void setUp(){
        scheduler = new Scheduler();
    }

    /**
     * This method tests whether the default parameters of the
     * scheduler are initialized in the proper state.
     */
    @Test
    public void testInit(){
        assertEquals(scheduler.getElevators(), testparam);
        assertEquals(scheduler.getFloors(), testparam);
    }

    /**
     * This method tests whether the scheduler class can successfully
     * add and access elevators.
     */
    @Test
    public void addElevators(){
        Elevator testElevator = new Elevator(numFloors);
        scheduler.addElevator(testElevator);
        testparam.add(testElevator);
        assertEquals(scheduler.getElevators(), testparam);
    }

    /**
     * This method tests whether the scheduler class can successfully
     * add and access floors.
     */
    @Test
    public void addFloors(){
        Floor testFloor = new Floor();
        scheduler.addFloor(testFloor);
        testparam.add(testFloor);
        assertEquals(scheduler.getFloors(), testparam);
    }

    /**
     * This method testes whether the Scheduler class is capable
     * of creating a packet to send to another class.
     */
    @Test
    public void testCreatePacket(){
        //Unnecessary for current iteration, scheduler does not create new packets.
    }

    /**
     * This method tests whether the Scheduler class is capable
     * of parsing a packet and retrieving necessary information.
     */
    @Test
    public void testParse(){
        //Unnecessary for current iteration, scheduler does not parse packets.
    }
}
