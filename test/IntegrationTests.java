package test;

import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import src.*;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for testing the integration of each subsystem with respect
 * to UDP processing and DatagramPackets.
 */
public class IntegrationTests {
    //Establish config file utilized for testing
    private static ConfigurationReader config;

    static {
        try {
            config = new ConfigurationReader("./testconfig.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    //Individual systems utilized for testing
    private Scheduler scheduler;
    private Elevator elevator;
    private Floor floor;
    ElevatorStateMachine stateMachine = new ElevatorStateMachine();
    private static ElevatorSubsystem subsystem = new ElevatorSubsystem(config);

    /**
     * Initialize the testing environment with a scheduler, usable floor, and
     * usable elevator.
     */
    @Before
    public void setUp(){
        scheduler = new Scheduler("test");
        floor = new Floor("test");
        scheduler.addElevator(new ElevatorStructure(1, stateMachine, 1, 1));
        scheduler.addFloor(new FloorStructure(1, 1));
        scheduler.addFloor(new FloorStructure(2, 2));
        elevator = subsystem.getElevators().get(1);
    }

    /**
     * This method tests how an elevator parses a request which is
     * constructed in the scheduler.
     */
    @Test
    public void testSchedulerToElevator() {
        int floor = config.getNumFloors();
        DatagramPacket data1 = scheduler.createElevatorPacket(floor, 1, 0);
        assertEquals(3, data1.getLength());
        assertNotNull(data1);
        elevator.parseRequest(data1);
        assertEquals((int) elevator.getRequested().peek(), floor);
    }

    /**
     * This method tests how the scheduler parses an elevator request
     * made within an elevator instance.
     */
    @Test
    public void testElevatorToScheduler(){
        elevator.setPort(1);
        DatagramPacket data1 = elevator.createPacket(UpdateType.OPEN_DOORS);
        assertNotNull(data1);
        assertEquals(3, data1.getLength());
        DatagramPacket data2 = scheduler.parseRequest(data1);
        assertEquals(1, data2.getLength());
    }

    /**
     * This method tests how the scheduler parses a floor requests created
     * in a Floor instance.
     */
    @Test
    public void testFloorToScheduler(){
        DatagramPacket data = floor.createPacket(1);
        assertNotNull(data);
        assertEquals((byte) 1, data.getData()[0]);
        DatagramPacket data2 = scheduler.parseRequest(data);
        assertNotNull(data2);
        assertEquals(3, data2.getLength());
    }

    /**
     * This method tests how a floor instance parses a request
     * from the scheduler.
     *
     * @throws UnknownHostException if local host name cannot be resolved as an address
     */
    @Test
    public void testSchedulerToFloor() throws UnknownHostException {
        byte[] updateData = new byte[]{(byte) 1};
        DatagramPacket data = scheduler.createFloorPacket(updateData, 1);
        assertNotNull(data);
        assertEquals(1, data.getLength());
        String temp = floor.testParseRequest(data);
        assertEquals("Boarding", temp);
        updateData[0] = 0;
        DatagramPacket data2 = new DatagramPacket(updateData, updateData.length, InetAddress.getLocalHost(), 1);
        temp = floor.testParseRequest(data2);
        assertEquals("Error in packet", temp);
    }
}
