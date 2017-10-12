package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import org.joml.Vector4f;

import java.util.function.Consumer;

/**
 * Created by Geert van Ieperen on 12-10-2017.
 */
public class MenuItem extends UIButton {

    public static final int BUTTON_WIDTH = 400;
    public static final int BUTTON_HEIGHT = 50;
    private static final Vector4f BACK_COLOR = new Vector4f(0.3f, 0.3f, 0.8f, 0.8f);
    private static final Vector4f TEXT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final int STROKE_WIDTH = 5;

    public MenuItem(String text, int x, int y, Consumer<MouseEvent> onClick) {
        this(text, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, onClick);
    }

    public MenuItem(String text, int x, int y, int width, int height, Consumer<MouseEvent> click) {
        super(x, y, width, height, (hud) -> {
            hud.roundedRectangle(x, y, width, height, 10);
            hud.fill(BACK_COLOR.x, BACK_COLOR.y, BACK_COLOR.z, BACK_COLOR.w);
            hud.text(x, y, 20f, Hud.Font.MEDIUM, 0, text, TEXT_COLOR);
        }, click);
    }


}
