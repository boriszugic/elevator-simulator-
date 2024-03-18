package src;

public interface SchedulerState {

    void requestReceived(SchedulerStateMachine context);
    String toString();
}
