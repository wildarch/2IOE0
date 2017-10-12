package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import org.joml.Vector2i;
import org.joml.Vector4i;

import java.util.function.Consumer;

/**
 * @author Jorren Hendriks.
 */
public class UIElement {

    protected int x;
    protected int y;
    private int width;
    private int heigth;
    private Consumer<Hud> render;

    public UIElement(int x, int y, int width, int heigth, Consumer<Hud> render) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.heigth = heigth;
        this.render = render;
    }

    public boolean contains(Vector2i v) {
        return x <= v.x() && v.x() <= x + width &&
                y <= v.y() && v.y() <= y + heigth;
    }

    public Vector4i getBounds() {
        return new Vector4i(x, y, x+width, y+heigth);
    }

    public void draw(Hud hud) {
        render.accept(hud);
    }

    public void updateXPosition(int x){
        this.x = x;
    }
}
