package src;

import java.io.IOException;

/**
 * Utility class implementing Runnable interface utilized for
 * receiving requests from the subsystem socket and ensuring requests
 * are not missed.
 */
public class receiveRequestThread implements Runnable{
    //Associated elevator subsystem
    private ElevatorSubsystem subsystem;

    /**
     * Constructor which initializes private subsystem to given variable subsystem
     *
     * @param subsystem The given subsystem
     */
    public receiveRequestThread(ElevatorSubsystem subsystem){
        this.subsystem = subsystem;
    }

    /**
     * Implements Runnable method run(), loops infinitely and attempts to receive
     * requests from the given socket of the subsystem.
     */
    @Override
    public void run(){
        while(true){
            try{
                subsystem.receiveRequest();
            } catch(IOException e){
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
