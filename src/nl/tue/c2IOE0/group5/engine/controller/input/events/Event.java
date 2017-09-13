package nl.tue.c2IOE0.group5.engine.controller.input.events;

import nl.tue.c2IOE0.group5.engine.rendering.Window;

/**
 * @author Jorren Hendriks
 *
 * Representation of a user-input event. Contains window at which the event was fired as well as the subject it was
 * fired on.
 */
public class Event {
    private final Window source;
    private final int subject;

    public Event(Window source, int subject) {
        this.source = source;
        this.subject = subject;
    }

    /**
     * Get the source window at which the event was fired.
     *
     * @return the source {@link Window}
     */
    public Window getSource() {
        return source;
    }

    /**
     * Get the id of the subject at which the event was fired. The id's come from GLFW, so you can identify them by
     * using constants like GLFW_KEY_W or GLFW_KEY_ESCAPE
     *
     * @return The id of the subject
     */
    public int getSubject() {
        return subject;
    }
}
