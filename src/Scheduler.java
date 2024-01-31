package src;

import java.net.DatagramSocket;

public class Scheduler implements Runnable{

    DatagramSocket socket;

    public static void main(String[] args){
        Scheduler scheduler = new Scheduler();
        new Thread(scheduler).start();
    }

    @Override
    public void run() {

    }

    /** Parse the request from floor
     *
     */
    private void parseRequest(){

    }

    /** Choose elevator based on parsed data
     *
     */
    private void chooseElevator(){

    }

    /** Create packet with elevator-needed information
     *
     */
    private void createPacket(){

    }


    /** Send packet
     *
     */
    private void sendRequest(){

    }



}
