package nl.tue.c2IOE0.group5.engine.provider;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.rendering.Drawable;

/**
 * @author Jorren Hendriks
 *
 * Interface of a Provider. A controller can be initialized inside the {@link #init(Engine)} method. After that, each
 * provider should have an updateFluent method to notify it when there is a gameloop updateFluent. Furthermore, a Provider extends
 * {@link Drawable} in which the Provider can draw it's current state to the active window.
 */
public interface Provider extends Updatable,Drawable {

    /**
     * Initialize the provider. This method will only be called once at startup. The {@link Engine} parameter can be
     * used to initialize a link to all required resources.
     *
     * @param engine The game engine.
     */
    void init(Engine engine);

}
