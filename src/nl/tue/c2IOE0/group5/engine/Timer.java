package nl.tue.c2IOE0.group5.engine;

/**
 * @author Jorren Hendriks & Geert van Ieperen
 */
public class Timer {

    private Updatable<Long> time;

    /**
     * Initialize the timer, set previous time to current.
     */
    public void init() {
        time = new Updatable<>(System.currentTimeMillis());
    }

    /**
     * @return The current system time.
     */
    public long getTime() {
        return System.currentTimeMillis();
//        return time.current();
    }

    /**
     * @return The elapsed time since previous request.
     */
    public long getElapsedTime() {
        return time.current() - time.previous();
    }

    /**
     * @return The previous time at which the timer was requested.
     */
    public long getPreviousTime() {
        return time.previous();
    }

    public void updateLooptime(){
        time.update(System.currentTimeMillis());
    }
}
