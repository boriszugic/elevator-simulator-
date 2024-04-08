package test;

import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import src.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * This class is the test class for testing the functionality
 * of the scheduler class.
 */
public class SchedulerTests {
    Scheduler scheduler;
    static ConfigurationReader config;
    static {
        try {
            config = new ConfigurationReader("./testconfig.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    private final int numFloors = 3;
    ElevatorStateMachine stateMachine = new ElevatorStateMachine();
    /**
     * Initialize the testing environment with the default
     * parameters for a scheduler.
     */

    @Before
    public void setUp(){
        scheduler = new Scheduler("test");
        for (int i = 1; i <= config.numElevators; i++){
            scheduler.addElevator(new ElevatorStructure(i, stateMachine, 1, 1));
        }
        for (int i = 1; i <= config.numFloors; i++){
            scheduler.addFloor(new FloorStructure(i, i));
        }
    }


    /**
     * This method tests whether the default parameters of the
     * scheduler are initialized in the proper state.
     */
    @Test
    public void testInit(){
        assertNotEquals(null, scheduler.getElevators());
        assertNotEquals(null, scheduler.getFloors());
    }

    @Test
    public void testCreatePacket(){
        DatagramPacket data1 = scheduler.createElevatorPacket(numFloors, 1, 0);
        assertNotNull(data1);
        assertEquals((byte) 1, data1.getData()[1]);
        byte[] temp = new byte[]{0};
        DatagramPacket data2 = scheduler.createFloorPacket(temp, 2);
        assertNotNull(data2);
    }

    /**
     * This method tests whether the Scheduler class is capable
     * of parsing a packet and retrieving necessary information.
     */
    @Test
    public void testParse() throws UnknownHostException {
        byte[] testdata = new byte[3];
        testdata[0] = (byte) 1;
        DatagramPacket testpacket = new DatagramPacket(testdata, testdata.length, InetAddress.getLocalHost(), 64);
        DatagramPacket sample = scheduler.parseRequest(testpacket);
        assertNotNull(sample);
        assertEquals(1, sample.getLength());
        testdata = new byte[4];
        testdata[1] = (byte) 1;
        testdata[2] = (byte) 1;
        testpacket = new DatagramPacket(testdata, testdata.length, InetAddress.getLocalHost(), 64);
        sample = scheduler.parseRequest(testpacket);
        assertNotNull(sample);
        assertEquals(3, sample.getLength());
    }

    /**
     * This method tests whether the scheduler can determine the
     * validity of a packet's contents.
     */
    @Test
    public void testValid(){
        byte[] test = new byte[4];
        assertTrue(scheduler.isValid(test));
    }
}
