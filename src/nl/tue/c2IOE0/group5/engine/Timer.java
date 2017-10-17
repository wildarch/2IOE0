package nl.tue.c2IOE0.group5.engine;

import nl.tue.c2IOE0.group5.util.Updatable;

/**
 * @author Jorren Hendriks & Geert van Ieperen
 */
public class Timer {

    private Updatable<Long> time;
    private Updatable<Long> tickTime;

    /**
     * Initialize the timer, set previous time to current.
     */
    public void init() {
        time = new Updatable<>(System.currentTimeMillis());
        tickTime = new Updatable<>(System.currentTimeMillis());
    }

    /**
     * @return The current system time.
     */
    public long getSystemTime() {
        return System.currentTimeMillis();
    }

    /**
     * @return the time of the start of the frame
     */
    public long getLoopTime(){
        return time.current();
    }

    /**
     * @return The elapsed time since previous gametick.
     */
    public long getElapsedTime() {
        return time.current() - time.previous();
    }

    public long getElapsedTickTime() { return tickTime.current() - tickTime.previous(); }

    /**
     * @return The previous time at which the timer was requested.
     */
    public long getPreviousTime() {
        return time.previous();
    }

    /**
     * set timer to current system time
     * should only be called by Engine, exactly once per rendering loop
     */
    public void updateLoopTime(){
        time.update(System.currentTimeMillis());
    }

    public void updateTickTime() { tickTime.update(System.currentTimeMillis()); }
}
