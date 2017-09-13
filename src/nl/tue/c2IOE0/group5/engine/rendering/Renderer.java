package nl.tue.c2IOE0.group5.engine.rendering;

/**
 * @author Jorren Hendriks
 *
 * A renderer can draw graphics to the window.
 */
public interface Renderer {

    /**
     * Render the current scene from the perspective of the renderer. Note that multiple renderer instances might draw
     * at a certain point in time, so one instance cannot guarantee how the final result will look like.
     *
     * @param window The window on which the renderer is drawing.
     */
    void render(Window window);

}
