# SYSC3303 Project Iteration 3 - Documentation
Date: March 17th, 2024

# Authors
  - Boris Zugic,    101223924
  - Cole McPherson, 101185260
  - Johnny Nguyen,  101185885
  - Nitin Alagu,    101223924
  - Evan Baldwin,   101222276

# Description
This assignment simulates an elevator system with a floor subsystem, a series of elevators, and a scheduler to determine
the operation of the elevators based on the input from floors. This iteration utilizes synchronized threads which communicate
between each other to parse a data input from the floor system. The current design does not utilize the scheduler to determine
the usage of elevators, but rather the floors and elevators communicate through the scheduler. 

This iteration has implemented UDP elements for effective communication between the three subsystems. State machines have
been included in the code for both the Elevator and Scheduler which simulate the attached UML State Machine diagrams.

# Using the program
1) Utilzing the IntelliJ IDE, execute the main() method contained within the ConfigurationReader.java class.
Alternatively, each subsystem (Elevator, Floor, Scheduler) can be executed on a separate computer utilizing
the same IP address and can perform UDP communication between each system. There is currently an input.txt
file which can be modified to test as desired. 

# Issues/Limitations
1. Can run a maximum of 64 floors and 63 elevators.
2. There is currently a small bug involving the scheduler handling simultaneous requests from multiple floors
which will be fixed in the next iteration. 

# Deliverables
  - Code necessary for program execution
    ButtonType.java, Door.java, Elevator.java, Door.java, ElevatorButton.java, ElevatorLamp.java,
    Floor.java, Main.java, Motor.java, Scheduler.java, ElevatorStateMachine.java, ElevatorStructure.java
  Performed by: Boris Zugic, Nitin Alagu, Evan Baldwin, Cole McPherson, Johnny Nguyen.

  - JUnit test cases
    ElevatorTests.java, FloorTests.java, MainTests.java, SchedulerTests.java
  Performed by: Evan Baldwin, Boris Zugic

  - UML Class Diagram + State Machine Diagrams
    ElevatorStateMachineIteration3.drawio , SchedulerStateMachineIteration3.drawio,  SchedulerStateMachineIteration3.png, StateMachineDiagramElevatorIteration3.png, SYSC3303_Iteration2_UML_Class.drawio, 
  Performed by: Cole McPherson, Nitin Alagu, Evan Baldwin

  - UML Sequence Diagrams
  Performed by: Johnny Nguyen, Boris Zugic
