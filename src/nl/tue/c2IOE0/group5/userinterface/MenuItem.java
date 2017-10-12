package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import org.joml.Vector4f;

import java.util.function.Consumer;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;

/**
 * Created by Geert van Ieperen on 12-10-2017.
 */
public class MenuItem extends UIButton {

    public static final int BUTTON_WIDTH = 400;
    public static final int BUTTON_HEIGHT = 50;
    private static final Vector4f BACK_COLOR = new Vector4f(0.3f, 0.3f, 0.8f, 0.8f);
    private static final Vector4f TEXT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector4f LINE_COLOR = new Vector4f(0.8f, 0.3f, 0.3f, 0.8f);;
    private static final int STROKE_WIDTH = 5;
    private final String text;
    private final int width;
    private final int height;

    public MenuItem(String text, int x, int y, Consumer<MouseEvent> onClick) {
        this(text, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, onClick);
    }

    public MenuItem(String text, int x, int y, int width, int height, Consumer<MouseEvent> click) {
        super(x, y, width, height, getRenderer(width, height, x, y, text), click);
        this.text = text;
        this.width = width;
        this.height = height;
    }

    @Override
    public void updateXPosition(int newX) {
        super.updateXPosition(newX);
        recreateRenderer(getRenderer(width, height, newX, y, text));
    }

    private static Consumer<Hud> getRenderer(int width, int height, int x, int y, String text){
        return (hud) -> {
            hud.roundedRectangle(x, y, width, height, 10);
            hud.fill(BACK_COLOR.x, BACK_COLOR.y, BACK_COLOR.z, BACK_COLOR.w);
            hud.stroke(STROKE_WIDTH, LINE_COLOR.x, LINE_COLOR.y, LINE_COLOR.z, LINE_COLOR.w);
            hud.text(x + width /2, y + height /2, 24f, Hud.Font.MEDIUM, NVG_ALIGN_CENTER, text, TEXT_COLOR);
        };
    }


}
