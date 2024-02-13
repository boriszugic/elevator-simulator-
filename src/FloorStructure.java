package src;

public class FloorStructure {
    private Direction buttonType;
    private int floorNum;
    private int port;
    private int destPort;

    public FloorStructure(int floorNum, int port) {
        this.floorNum = floorNum;
        this.port = port;
    }

    public Direction getButtonType() {
        return buttonType;
    }

    public void setButtonType(Direction buttonType) {
        this.buttonType = buttonType;
    }

    public int getFloorNum() {
        return floorNum;
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
        return "FloorStructure{" +
                "buttonType=" + buttonType +
                ", floorNum=" + floorNum +
                ", port=" + port +
                ", destPort=" + destPort +
                '}';
    }
}
