package nl.tue.c2IOE0.group5.engine.controller.input.events;

/**
 * @author Jorren Hendriks
 *
 * Listener for user interaction events.
 */
public interface Listener {

    /**
     * Fired when the user presses a key on their keyboard.
     *
     * @param event The associated {@link Event}
     */
    void onKeyPressed(Event event);

    /**
     * Fired when the user releases a key on their keyboard.
     *
     * @param event The associated {@link Event}
     */
    void onKeyReleased(Event event);

    /**
     * Fired when the user holds a key on their keyboard.
     *
     * @param event The asociated {@link Event}
     */
    void onKeyHold(Event event);

    /**
     * Fired when the user presses a button on their mouse.
     *
     * @param event The associated {@link MouseEvent}
     */
    void onMouseButtonPressed(MouseEvent event);

    /**
     * Fired when the user releases a button on their mouse.
     *
     * @param event The associated {@link MouseEvent}
     */
    void onMouseButtonReleased(MouseEvent event);

    /**
     * Fired when the user moves their mouse.
     *
     * @param event The associated {@link MouseEvent}
     */
    void onMouseMove(MouseEvent event);

    /**
     * Fired when the user scrolls their mouse.
     *
     * @param event The associated {@link MouseEvent}
     */
    void onMouseScroll(MouseEvent event);
}
