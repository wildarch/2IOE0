package nl.tue.c2IOE0.group5.userinterface;

import org.joml.Vector2i;

/**
 * @author Jorren Hendriks.
 */
public class MenuPositioner {

    private int x;
    private int y;
    private final int margin;

    private final Vector2i pos = new Vector2i();

    public MenuPositioner(int x, int y, int margin) {
        this.x = x;
        this.y = y;
        this.margin = margin;
    }

    public Vector2i place(UIElement element, boolean newline) {
        return place(element.width, element.height, newline);
    }

    public Vector2i place(int width, int height, boolean newline) {
        pos.set(x, y);
        if (newline) {
            y += height + margin;
        } else {
            x += width + margin;
        }
        return pos;
    }
}
