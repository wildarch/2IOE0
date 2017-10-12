package nl.tue.c2IOE0.group5.engine.rendering;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;

/**
 * @author Jorren
 */
public interface Clickable {

    /**
     * When the user clicks this object this method will be fired in order to update necessary values.
     *
     * @param event The mouse event that triggered the click.
     */
    void onClick(MouseEvent event);

}
