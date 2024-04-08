# SYSC3303 Project

# Authors
  - Boris Zugic,    101223924
  - Cole McPherson, 101185260
  - Johnny Nguyen,  101185885
  - Evan Baldwin,   101222276

# Description
This assignment simulates an elevator system with a floor subsystem, a series of elevators, and a scheduler to determine
the operation of the elevators based on the input from floors. This iteration utilizes synchronized threads which communicate
between each other to parse a data input from the floor system. 

This iteration has implemented UDP elements for effective communication between the three subsystems. State machines have
been included in the code for both the Elevator and Scheduler which simulate the attached UML State Machine diagrams.

# Using the program
1) Utilzing the IntelliJ IDE, execute the main() method contained each subsystem (Scheduler, FloorSubsystem, ElevatorSubsystem).
If desired, a configuration can be established that executes the methods in order of scheduler, floor_subsystem, and finally
elevator_subsystem. A configuration file titled under config.json contains settings to modify floor/elevator numbers and timing values.
Alternatively, each subsystem (Elevator, Floor, Scheduler) can be executed on a separate computer utilizing
the same IP address and can perform UDP communication between each system. There is currently an input.txt
file which can be modified to test as desired. 

# Issues/Limitations
1. Can run a maximum of 64 floors and 63 elevators.
2. There is currently a slight delay in scheduler response/output that slows the system performance

# Responsibilities

- Iteration 1:
    - UML Class and Sequence Diagrams:
      - Cole McPherson: UML Class Diagram
      - Johnny Nguyen: Sequence Diagram
    - Coding:
      - Boris Zugic: Elevator, Floor, Main, ButtonType, Door, Motor, Scheduler
      - Cole McPherson: 
      - Johnny Nguyen:
      - Evan Baldwin: Elevator movement, documentation
    - Unit Testing:
      - Evan Baldwin: Elevator, Scheduler, Floor Tests
      - Boris Zugic: Elevator, Floor Tests

- Iteration 2:
    - UML Class and Sequence Diagrams:
      - Cole McPherson: Updated Sequence, State Diagram
    - Coding:
      - Boris Zugic: UDP Communication (Elevator/Scheduler/Floor), Display
      - Cole McPherson: Elevator State Machine (ElevatorState, ElevatorStateMachine, etc)
      - Johnny Nguyen: Scheduler 
      - Evan Baldwin: ElevatorStateMachine, documentation
    - Unit Testing:
      - Evan Baldwin: Updated UDP, Elevator, Scheduler, Floor Tests
    
- Iteration 3:
    - UML Class and Sequence Diagrams:
      - Cole McPherson: Elevator/Scheduler State Diagrams
      - Evan Baldwin: Updated Elevator/Scheduler State Diagrams, UML Class Diagram
    - Coding:
      - Boris Zugic: GUI, Logger, Input file parsing
      - Cole McPherson: 
      - Johnny Nguyen: Scheduler algorithms, UDP Communication
      - Evan Baldwin: Scheduler State Machine (SchedulerState, StateStateMachine, etc)
    - Unit Testing:
      - No major changes

- Iteration 4:
    - UML Class and Sequence Diagrams:
      - Evan Baldwin: Elevator State Diagram, Sequence Diagram
    - Coding:
      - Boris Zugic: Elevator Selection Algorithm
      - Cole McPherson: UDP Communication, Schedule/Scheduler Request Communication with Elevators
      - Johnny Nguyen: Scheduler Communication/Logic, Parsing
      - Evan Baldwin: Elevator threading/subsystem, UDP Communication, Error injection/elevator response, Communication Help
    - Unit Testing:
      - Evan Baldwin: Updated Elevator tests, new Scheduler tests

- Iteration 5:
    - UML Class, Sequence and Timing Diagrams:
      - Evan Baldwin: Timing, Elevator State Diagrams
      - Boris Zugic: Sequence Diagram
      - Cole McPherson: Updated UML Class Diagram
    - Coding:
      - Boris Zugic: GUI, Elevator State Machine
      - Cole McPherson:  Fixed edge cases with Scheduler, Tweaked ElevatorStateMachine, fixed bug with elevator states
      - Johnny Nguyen: Elevator Request Handling, Floor Request Timings w/ Faults
      - Evan Baldwin: Elevator fault handling
    - Unit Testing:
      - Evan Baldwin: Elevator, Floor, Scheduler, Integration Tests
