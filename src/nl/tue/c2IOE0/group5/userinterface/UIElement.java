package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.rendering.HudElement;
import org.joml.Vector2i;
import org.joml.Vector4f;

/**
 * @author Jorren Hendriks.
 */
public abstract class UIElement implements HudElement {

    public static final int BUTTON_WIDTH = 500;
    public static final int BUTTON_HEIGHT = 75;
    static final float TEXT_SIZE = 42f;
    static final Vector4f BACK_COLOR = new Vector4f(0.3f, 0.3f, 0.8f, 0.8f);
    static final Vector4f TEXT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    static final Vector4f LINE_COLOR = new Vector4f(0.8f, 0.3f, 0.3f, 0.8f);
    static final int STROKE_WIDTH = 5;
    static final int INDENT = 10;

    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public UIElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(Vector2i v) {
        return x <= v.x() && v.x() <= x + width &&
                y <= v.y() && v.y() <= y + height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}
