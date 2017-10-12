package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.function.Consumer;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;

/**
 * Created by Geert van Ieperen on 12-10-2017.
 */
public class MenuItem extends UIButton {

    public static final int BUTTON_WIDTH = 400;
    public static final int BUTTON_HEIGHT = 50;
    public static final Vector4f BACK_COLOR = new Vector4f(0.3f, 0.3f, 0.8f, 0.8f);
    public static final Vector4f TEXT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final int STROKE_WIDTH = 5;

    private int width, height;
    private Vector4f baseColor;
    private Vector4f textColor;
    private Vector4f lineColor;
    private String text;

    private Runnable onClick;

    public MenuItem(String text, int x, int y, int width, int height, Consumer<MouseEvent> click) {
        super(x, y, width, height, (hud) -> {
            hud.roundedRectangle(x, y, width, height, 10);
            hud.fill(BACK_COLOR.x, BACK_COLOR.y, BACK_COLOR.z, BACK_COLOR.w);
            hud.text(x, y, 20f, Hud.Font.MEDIUM, 0, text, TEXT_COLOR);
        }, click);
    }

/*
    public MenuItem(int width, int height, Vector4f color, Vector4f textColor, Vector4f lineColor, String text, Runnable onClick) {
        this.baseColor = color;
        this.textColor = textColor;
        this.width = width;
        this.height = height;
        this.lineColor = lineColor;
        this.text = text;
        this.onClick = onClick;
    }

    public MenuItem(String text, Runnable onClick) {
        this(BUTTON_WIDTH, BUTTON_HEIGHT, BACK_COLOR, TEXT_COLOR, TEXT_COLOR, text, onClick);
    }
    */

    public void onClick(){
        onClick.run();
    }
}
