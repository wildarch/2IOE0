package nl.tue.c2IOE0.group5.engine;

/**
 * @author Jorren Hendriks
 */
public class Timer {

    private long previousTime;

    /**
     * Initialize the timer, set previous time to current.
     */
    public void init() {
        previousTime = System.currentTimeMillis();
    }

    /**
     * Get the current time.
     *
     * @return The current time.
     */
    public long getTime() {
        return System.currentTimeMillis();
    }

    /**
     * Get the elapsed time since previous request. Will also update {@link #previousTime} to be the current time.
     *
     * @return The elapsed time since previous request.
     */
    public long getElapsedTime() {
        long time = System.currentTimeMillis();
        long elapsedTime = time - previousTime;
        previousTime = time;
        return elapsedTime;
    }

    /**
     * Get the previous time at which the timer was requested.
     *
     * @return The previous time.
     */
    public long getPreviousTime() {
        return previousTime;
    }

}
