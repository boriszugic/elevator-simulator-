package test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import src.Elevator;
import src.ElevatorStateEnum;
import src.ElevatorStructure;
import src.ElevatorStateMachine;

/**
 * This class is the test class for testing the functionality
 * of the elevator class and the elevator state machine.
 */
public class ElevatorTests {

    private static Elevator elevator;
    private static ElevatorStructure stateElevator;
    private static int numFloors = 3;

    ElevatorStateMachine elevatorStateMachine = new ElevatorStateMachine();

    /**
     * Initialize the testing environment with two elevators.
     */
    @Before
    public void setUp(){
//        elevator = new Elevator(numFloors);
        stateElevator = new ElevatorStructure(1, ElevatorStateEnum.IDLE, 1, 0);
    }

    /**
     * This method tests that the elevators are initialized in the proper state.
    */
    @Test
    public void testInit(){
        assertEquals(elevator.getCurrentFloor(), 1);
        assertEquals(elevator.getNumOfPassengers(), 0);
        assertEquals(elevator.getDestinationFloor(), 0);
        assertEquals(elevator.getId(), 1);
        assertNotEquals(elevator.getDisplay(), null);
        assertNotEquals(elevator.getButtons(), null);
        assertNotEquals(elevator.getLamps(), null);
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
        elevator.move(4);
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
        //elevatorStateMachine.setState(new IdleState());
        assertEquals("Idle", elevatorStateMachine.getState().toString());
    }
}