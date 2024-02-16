# SYSC3303 Project Iteration 1 - Documentation
Date: February 3rd, 2024

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

This iteration has partially implemented UDP elements from future iterations, and utilizes datagrams/sockets to send and receive
information between threads. This is preparing for the multiple independant elevators which will be implemented in iteration 3. 

# Using the program
1) Utilizing the IntelliJ IDE, execute the main() method contained within Main.java.
There is currently no print output for the system, as this is an early iteration
intended to test the communication between subsystems.

# Issues/Limitations
1. Can run a maximum of 64 floors and 63 elevators.

# Deliverables
  - Code necessary for program execution
    ButtonType.java, Door.java, Elevator.java, Door.java, ElevatorButton.java, ElevatorLamp.java,
    Floor.java, Main.java, Motor.java, Scheduler.java
  Performed by: Boris Zugic, Nitin Alagu, Evan Baldwin

  - JUnit test cases
    ElevatorTests.java, FloorTests.java, MainTests.java, SchedulerTests.java
  Performed by: Evan Baldwin, Boris Zugic

  - UML Class Diagram
    SYSC3303_Iteration1_UML_Class.png, SYSC3303_Iteration1_UML_Class.drawio
  Performed by: Cole McPherson

  - UML Sequence Diagrams

  Performed by: Johnny Nguyen
