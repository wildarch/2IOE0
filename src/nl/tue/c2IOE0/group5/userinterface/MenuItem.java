package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import org.joml.Vector4f;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;

/**
 * Created by Geert van Ieperen on 12-10-2017.
 */
public class MenuItem {

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
    public int x, y;

    /**
     * creates a menu button
     * @param x
     * @param y
     * @param baseColor color of the background of the button
     * @param textColor color of the text in the button
     * @param lineColor color of the bounding line around the button
     * @param text text displayed in the button
     * @param onClick execution when the button is clicked
     */
    public MenuItem(int x, int y, int width, int height, Vector4f baseColor, Vector4f textColor, Vector4f lineColor, String text, Runnable onClick) {
        this.baseColor = baseColor;
        this.textColor = textColor;
        this.width = width;
        this.height = height;
        this.lineColor = lineColor;
        this.text = text;
        this.onClick = onClick;
        this.x = x;
        this.y = y;
    }

    public MenuItem(int x, int y, Runnable onClick, String text) {
        this.x = x;
        this.y = y;
        new MenuItem(this.x, this.y, BUTTON_WIDTH, BUTTON_HEIGHT, BACK_COLOR, TEXT_COLOR, TEXT_COLOR, text, onClick);
    }

    public void draw(Hud hud) {
        hud.fill(baseColor.x, baseColor.y, baseColor.z, baseColor.w);
        hud.stroke(STROKE_WIDTH, lineColor.x, lineColor.y, lineColor.z, lineColor.w);
        hud.text(x + width/2, y + height/2, 12f, Hud.Font.MEDIUM, NVG_ALIGN_CENTER, text, textColor);
        hud.roundedRectangle(x, y, width, height, 10);
    }

    public void updatePosition(int y){
        this.y = y;
    }

    public void onClick(){
        onClick.run();
    }
}
