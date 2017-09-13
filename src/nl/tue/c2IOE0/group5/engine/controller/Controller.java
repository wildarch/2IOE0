package nl.tue.c2IOE0.group5.engine.controller;

import nl.tue.c2IOE0.group5.engine.Engine;

/**
 * @author Jorren Hendriks
 *
 * Interface of a Controller. A controller can be initialized inside the {@link #init(Engine)} method. After that, each
 * controller should have an update method to notify it when there is a gameloop update.
 */
public interface Controller {

    /**
     * Initialize the controller. This method will only be called once at startup. The {@link Engine} parameter can be
     * used to initialize a link to all required resources.
     *
     * @param engine The game engine.
     */
    void init(Engine engine);

    /**
     * Update the controller. Handle controller-specific timed tasks here. This method will be called every game tick.
     * Any resources necessary should already be available from {@link #init(Engine)}.
     */
    void update();

}
