package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import org.joml.Vector4f;

import java.util.function.Consumer;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;

/**
 * @author Geert van Ieperen
 * a button with set shape and text
 */
public class MenuItem extends UIButton {

    public static final int BUTTON_WIDTH = 500;
    public static final int BUTTON_HEIGHT = 75;
    private static final float TEXT_SIZE = 30f;
    private static final Vector4f BACK_COLOR = new Vector4f(0.3f, 0.3f, 0.8f, 0.8f);
    private static final Vector4f TEXT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector4f LINE_COLOR = new Vector4f(0.8f, 0.3f, 0.3f, 0.8f);;
    private static final int STROKE_WIDTH = 5;
    private final String text;

    private Consumer<MouseEvent> click;

    public MenuItem(String text, int x, int y, Consumer<MouseEvent> click) {
        this(text, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, click);
    }

    public MenuItem(String text, int x, int y, int width, int height, Consumer<MouseEvent> click) {
        super(x, y, width, height);
        this.text = text;
        this.click = click;
    }

    public MenuItem(String text, PositionState state, Consumer<MouseEvent> click){
        this(text, state.getX(), state.getY(), click);
    }

    @Override
    public void draw(Hud hud) {
        hud.roundedRectangle(x, y, width, height, 10);
        hud.fill(BACK_COLOR.x, BACK_COLOR.y, BACK_COLOR.z, BACK_COLOR.w);
        hud.stroke(STROKE_WIDTH, LINE_COLOR.x, LINE_COLOR.y, LINE_COLOR.z, LINE_COLOR.w);
        hud.text(x + width /2, y + height /2, TEXT_SIZE, Hud.Font.MEDIUM, NVG_ALIGN_CENTER, text, TEXT_COLOR);
    }

    @Override
    public void onClick(MouseEvent event) {
        click.accept(event);
    }
}
