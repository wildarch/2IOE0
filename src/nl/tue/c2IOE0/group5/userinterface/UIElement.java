package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import org.joml.Vector2i;

import java.util.function.Consumer;

/**
 * @author Jorren Hendriks.
 */
public class UIElement {

    protected int x;
    protected int y;
    private int width;
    private int height;
    private Consumer<Hud> render;

    public UIElement(int x, int y, int width, int height, Consumer<Hud> render) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.render = render;
    }

    public boolean contains(Vector2i v) {
        return x <= v.x() && v.x() <= x + width &&
                y <= v.y() && v.y() <= y + height;
    }

    public void draw(Hud hud) {
        render.accept(hud);
    }

    public void updateXPosition(int x){
        this.x = x;
    }

    public void recreateRenderer(Consumer<Hud> render){
        this.render = render;
    }
}
