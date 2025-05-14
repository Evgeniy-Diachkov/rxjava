// rx/schedulers/Scheduler.java
package rx.schedulers;

public interface Scheduler {
    void execute(Runnable task);
}
