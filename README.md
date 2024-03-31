# SYSC3303 Project Iteration 4 - Documentation
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
between each other to parse a data input from the floor system. 

This iteration has implemented UDP elements for effective communication between the three subsystems. State machines have
been included in the code for both the Elevator and Scheduler which simulate the attached UML State Machine diagrams.

# Using the program
1) Utilzing the IntelliJ IDE, execute the main() method contained within the ConfigurationReader.java class.
Alternatively, each subsystem (Elevator, Floor, Scheduler) can be executed on a separate computer utilizing
the same IP address and can perform UDP communication between each system. There is currently an input.txt
file which can be modified to test as desired. 

# Issues/Limitations
1. Can run a maximum of 64 floors and 63 elevators.

# Responsibilities

- Iteration 1:
    - UML Class and Sequence Diagrams:
      - Cole McPherson: UML Class Diagram
      - Johnny Nguyen: Sequence Diagram
    - Coding:
      - Boris Zugic: Elevator, Floor, Main, ButtonType, Door, Motor, Scheduler
      - Cole McPherson:
      - Johnny Nguyen:
      - Nitin Alagu:
      - Evan Baldwin: Elevator movement, documentation
    - Unit Testing:
      - Evan Baldwin: Elevator, Scheduler, Floor Tests
      - Boris Zugic: Elevator, Floor Tests

- Iteration 2:
    - UML Class and Sequence Diagrams:
      - Nitin Alagu: Updated UML Class Diagram
      - Cole McPherson: Updated Sequence, State Diagram
    - Coding:
      - Boris Zugic: UDP Communication (Elevator/Scheduler/Floor), Display
      - Cole McPherson: Elevator State Machine (ElevatorState, ElevatorStateMachine, etc)
      - Johnny Nguyen: Scheduler 
      - Nitin Alagu:
      - Evan Baldwin: 
    - Unit Testing:
      - Evan Baldwin: Updated UDP, Elevator, Scheduler, Floor Tests
    
- Iteration 3:
    - UML Class and Sequence Diagrams:
      - Cole McPherson: Elevator/Scheduler State Diagrams
      - Evan Baldwin: Updated Elevator/Scheduler State Diagrams, UML Class Diagram
    - Coding:
      - Boris Zugic: GUI, Logger, Input file
      - Cole McPherson: 
      - Johnny Nguyen: Scheduler, UDP Communication
      - Nitin Alagu:
      - Evan Baldwin: Scheduler State Machine (SchedulerState, StateStateMachine, etc)
    - Unit Testing:
      - No major changes

- Iteration 4:
    - UML Class and Sequence Diagrams:
      - 
    - Coding:
      - Boris Zugic: Elevator Selection Algorithm
      - Cole McPherson: UDP Communication, Schedule/Scheduler Request Communication with Elevators
      - Johnny Nguyen: Scheduler Communication/Logic, Parsing
      - Nitin Alagu:
      - Evan Baldwin: Elevator threading/subsystem, UDP Communication, Error injection/elevator response
    - Unit Testing:
      - Evan Baldwin: Updated Elevator tests, new Scheduler tests

