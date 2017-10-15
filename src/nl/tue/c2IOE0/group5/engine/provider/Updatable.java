package nl.tue.c2IOE0.group5.engine.provider;

import nl.tue.c2IOE0.group5.engine.Engine;

/**
 * Interface for an object that can be updated at every game tick
 *
 * @author Daan de Graaf
 */
public interface Updatable {
    /**
     * This method will be called every game tick.
     * Any resources necessary should already be available from {@link #init(Engine)}.
     */
    void update();
}
