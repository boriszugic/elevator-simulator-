package test;

import org.junit.Before;
import org.junit.Test;
import src.Floor;
import src.FloorSubsystem;
import src.Scheduler;
import src.Elevator;

import static org.junit.Assert.*;

/**
 * This test classes tests the communication between the three
 * subsystems of the elevator.
 */
public class UDPTests {

    @Before
    public void setUp(){
        String[] args1 = null;
        String[] args2 = new String[]{"1 2"};
        String[] args3 = new String[]{"testinput.txt"};
        Scheduler.main(args1);
//        Elevator.main(args2);
        FloorSubsystem.main(args3);
    }
    @Test
    public void test(){
        //TBD
    }
}
