package nl.tue.c2IOE0.group5.userinterface;

import org.joml.Vector2i;
import org.joml.Vector4i;

/**
 * @author Jorren Hendriks.
 */
public class UIElement {

    private int x1, y1, x2, y2;
    private Runnable render;

    public UIElement(int x1, int y1, int x2, int y2, Runnable render) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.render = render;
    }

    public boolean contains(Vector2i v) {
        return x1 <= v.x() && v.x() <= x2 &&
                y1 <= v.y() && v.y() <= y2;
    }

    public Vector4i getBounds() {
        return new Vector4i(x1, y1, x2, y2);
    }

    public void draw() {
        render.run();
    }

}
