package test;

import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import src.*;

import java.io.IOException;

/**
 * This class is the test class for testing the functionality
 * of the elevator class and the elevator state machine.
 */
public class ElevatorTests {

    private static Elevator elevator;
    private static ElevatorStructure stateElevator;
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

    private static ElevatorSubsystem subsystem = new ElevatorSubsystem(config);

    ElevatorStateMachine elevatorStateMachine = new ElevatorStateMachine();


    /**
     * Initialize the testing environment with two elevators.
     */
    @Before
    public void setUp(){
        elevator = subsystem.getElevators().get(1);
        stateElevator = new ElevatorStructure(1, ElevatorStateEnum.IDLE, 1, 0);
    }

    /**
     * This method tests that the elevators are initialized in the proper state.
    */
    @Test
    public void testInit(){
        assertEquals(1, elevator.getCurrentFloor());
        assertEquals(0, elevator.getNumOfPassengers());
        assertEquals(0, elevator.getDestinationFloor());
        assertEquals(1, elevator.getId());
        assertNotEquals(null, elevator.getDisplay());
        assertNotEquals(null, elevator.getButtons());
        assertNotEquals(null, elevator.getLamps());
    }

    /**
     * The method tests that an elevator is capable of changing the
     * stated floor via the move method.
     */
    @Test
    public void testMove(){
        elevator.move(2);
        assertEquals(2, elevator.getCurrentFloor());
        elevator.move(3);
        assertEquals(3, elevator.getCurrentFloor());
        elevator.move(25);
        assertEquals(3, elevator.getCurrentFloor());
    }

    /**
     * This method tests whether the elevators simulated by the state
     * machine can successfully change states from idling to moving
     * and unloading/loading.
     */
    @Test
    public void testStates(){
        assertEquals("Idle", elevatorStateMachine.getState().toString());
        elevatorStateMachine.requestReceived();
        assertEquals("Moving", elevatorStateMachine.getState().toString());
        elevatorStateMachine.Arrival();
        assertEquals("Unloading/Loading", elevatorStateMachine.getState().toString());
    }
}