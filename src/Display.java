package src;

public class Display {

    Elevator elevator;
    private final GUI gui;
    public Display(Elevator e){
        elevator = e;
        gui = new GUI(e.getId(), e.getPort());
    }

    public void display(String message) {
        gui.display(message);
    }
}