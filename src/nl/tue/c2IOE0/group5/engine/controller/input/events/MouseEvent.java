package nl.tue.c2IOE0.group5.engine.controller.input.events;

import nl.tue.c2IOE0.group5.engine.rendering.Window;
import org.joml.Vector2i;

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

    /**
     * Get the position at which the mouseevent fired.
     *
     * @return A 2d vector with x at index 0 and y at index 1
     */
    public Vector2i getPosition() {
        return new Vector2i(xPos, yPos);
    }
}
