package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.rendering.Clickable;

/**
 * @author Jorren Hendriks.
 */
public abstract class UIButton extends UIElement implements Clickable {

    public UIButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

}
