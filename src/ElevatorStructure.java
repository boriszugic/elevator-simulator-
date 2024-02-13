package src;

public class ElevatorStructure {
    private int id;
    private ElevatorState state;
    private int currFloor;
    private int port;
    private int destPort;

    public ElevatorStructure(int id, ElevatorState state, int currFloor, int port){
        this.id = id;
        this.state = state;
        this.currFloor = currFloor;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public ElevatorState getState() {
        return state;
    }

    public int getCurrFloor() {
        return currFloor;
    }

    public void setCurrFloor(int currFloor) {
        this.currFloor = currFloor;
    }

    public int getPort() {
        return port;
    }

    public int getDestPort() {
        return destPort;
    }

    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }

    @Override
    public String toString() {
        return "ElevatorStructure{" +
                "id=" + id +
                ", state=" + state +
                ", currFloor=" + currFloor +
                ", port=" + port +
                ", destPort=" + destPort +
                '}';
    }
}
