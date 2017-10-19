package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.util.PositionState;
import org.joml.Vector4f;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;

/**
 * @author Geert van Ieperen
 *         created on 17-10-2017.
 * a field with the make-up of a menubutton, automatically including title and back-button
 */
public class MenuTextField extends UIElement {

    private static final float TEXT_SIZE = 30f;

    private static final Vector4f BACK_COLOR = new Vector4f(0.3f, 0.3f, 0.8f, 0.8f);
    private static final Vector4f TEXT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector4f LINE_COLOR = new Vector4f(0.8f, 0.3f, 0.3f, 0.8f);;
    private static final int STROKE_WIDTH = 5;
    public static final int OFF_TOP = 50;
    public static final int SIDE_OFFSET = 300;
    private final String title;
    private final String[] content;

    private UIElement titleBar;
    private MenuButton back;


    public MenuTextField(String title, String[] content, int width, int height) {
        this (title, content, 0, 0, width, height);
    }

    /**
     * creates a field that spreads across the screen.
     * @param title
     * @param content the content this textbox has to display
     */
    public MenuTextField(String title, String[] content, int x, int y, int width, int heigth) {
        super(x, y, width, heigth);

        this.title = title;
        this.content = content;
    }

    @Override
    public void draw(Hud hud) {
        hud.roundedRectangle(x, y, width, height, INDENT);
        hud.fill(BACK_COLOR.x, BACK_COLOR.y, BACK_COLOR.z, BACK_COLOR.w);
        hud.stroke(STROKE_WIDTH, LINE_COLOR.x, LINE_COLOR.y, LINE_COLOR.z, LINE_COLOR.w);

        hud.text(x + width /2, (y + BUTTON_HEIGHT - (int) UIElement.TEXT_SIZE/2), UIElement.TEXT_SIZE, Hud.Font.MEDIUM,
                NVG_ALIGN_CENTER, title, TEXT_COLOR);

        PositionState pos = new PositionState(x + width/2, (int) (y + BUTTON_HEIGHT + 20 + TEXT_SIZE), (int) (TEXT_SIZE * 1.2));
        for (String line : content) {
            hud.text(pos.getX(), pos.getY(), TEXT_SIZE, Hud.Font.MEDIUM, NVG_ALIGN_CENTER, line, TEXT_COLOR);
        }
    }
}
