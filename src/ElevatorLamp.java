package src;

public class ElevatorLamp {

    private final int id;
    private boolean isOn;

    public ElevatorLamp(int id){
        this.id = id;
        isOn = false;
    }

    public void turnOn(){
        isOn = true;
    }

    public void turnOff(){
        isOn = false;
    }
}
