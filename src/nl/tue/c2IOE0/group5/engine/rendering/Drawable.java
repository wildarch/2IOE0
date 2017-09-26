package nl.tue.c2IOE0.group5.engine.rendering;

/**
 * @author Jorren Hendriks
 *
 * A drawer can draw graphics to the window.
 */
public interface Drawable {

    /**
     * Draw the current scene from the perspective of the drawer. Note that
     * multiple drawer instances might draw at a certain point in time, so one
     * instance cannot guarantee how the final result will look like.
     *
     * @param window The window on which the drawer is drawing.
     */
    void draw(Window window);

}
