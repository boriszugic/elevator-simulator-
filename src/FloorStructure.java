package src;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class FloorStructure {
    @Getter
    @Setter
    private Direction buttonType;
    @Getter
    private int floorNum;
    @Getter
    private int port;
    @Getter
    @Setter
    private int elevatorPort;

    public FloorStructure(int floorNum, int port) {
        this.floorNum = floorNum;
        this.port = port;
    }
}