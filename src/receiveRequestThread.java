package src;

import java.io.IOException;

public class receiveRequestThread implements Runnable{
    private ElevatorSubsystem subsystem;
    public receiveRequestThread(ElevatorSubsystem subsystem){
        this.subsystem = subsystem;
    }
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
