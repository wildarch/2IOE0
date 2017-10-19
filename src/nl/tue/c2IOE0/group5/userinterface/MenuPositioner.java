package nl.tue.c2IOE0.group5.userinterface;

import org.joml.Vector2i;

/**
 * @author Jorren Hendriks.
 */
public class MenuPositioner {

    private final static int MARGIN = 10;

    private int x;
    private int y;

    private final Vector2i pos = new Vector2i();

    public MenuPositioner(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2i place(boolean newline) {
        return place(UIElement.BUTTON_WIDTH, UIElement.BUTTON_HEIGHT, newline);
    }

    public Vector2i place(UIElement element, boolean newline) {
        return place(element.width, element.height, newline);
    }

    public Vector2i place(int width, int height, boolean newline) {
        pos.set(x, y);
        if (newline) {
            y += height + MARGIN;
        } else {
            x += width + MARGIN;
        }
        return pos;
    }
}
