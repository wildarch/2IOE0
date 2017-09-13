package nl.tue.c2IOE0.group5.engine.controller.input.events;

import nl.tue.c2IOE0.group5.engine.rendering.Window;

/**
 * @author Jorren Hendriks
 *
 * Representation of a user-input mouse event. Contains window at which the event was fired as well as the subject it
 * was fired on together with the coördinates at which the event happened.
 */
public class MouseEvent extends Event {
    private int xPos;
    private int yPos;

    public MouseEvent(Window source, int subject, int xPos, int yPos) {
        super(source, subject);
        this.xPos = xPos;
        this.yPos = yPos;
    }

    /**
     * Get the x-coordinate at which the event fired.
     *
     * @return The x-coordinate.
     */
    public int getX() {
        return xPos;
    }

    /**
     * Get the y-coordinate at which the event fired.
     *
     * @return The y-coordinate.
     */
    public int getY() {
        return yPos;
    }
}
