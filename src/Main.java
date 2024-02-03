package src;

import java.util.Scanner;

/** Starting point of our application
 * Class to configure the number of floors and elevators and run the application
 *
 *
 *
 */
public class Main {

    private static final int DEFAULT_NUM_OF_ELEVATORS = 1,
                             DEFAULT_NUM_OF_FLOORS    = 3;

    public static void main(String[] args){
        int numOfElevators = DEFAULT_NUM_OF_ELEVATORS,
            numOfFloors    = DEFAULT_NUM_OF_FLOORS;

        Scanner input = new Scanner(System.in);

        System.out.println("Number of elevators: ");
        try {
            numOfElevators = input.nextInt();
        }catch(NumberFormatException e){

        }

        System.out.println("Number of floors: ");
        try {
            numOfFloors = input.nextInt();
        }catch(NumberFormatException e){

        }

        Scheduler scheduler = Scheduler.getInstance();

        for (int i = 0; i < numOfElevators; i++){
            Elevator elevator = new Elevator(numOfFloors);
            scheduler.addElevator(elevator);
            new Thread(elevator).start();
        }

        for (int i = 0; i < numOfFloors; i++){
            Floor floor = new Floor();
            scheduler.addFloor(floor);
            new Thread(floor).start();
        }

        new Thread(scheduler).start();
    }
}
