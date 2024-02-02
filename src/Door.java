package src;

import lombok.Getter;

public class Door {

    @Getter
    private boolean isOpen;

    public Door(){
        isOpen = false;
    }

    public void open(){
        isOpen = true;
    }

    public void close(){
        isOpen = false;
    }
}
