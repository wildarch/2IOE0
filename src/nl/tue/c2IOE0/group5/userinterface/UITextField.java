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
public class UITextField extends UIElement {

    private static final float TEXT_SIZE = 30f;
    private static final Vector4f BACK_COLOR = new Vector4f(0.3f, 0.3f, 0.8f, 0.8f);
    private static final Vector4f TEXT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector4f LINE_COLOR = new Vector4f(0.8f, 0.3f, 0.3f, 0.8f);;
    private static final int STROKE_WIDTH = 5;
    public static final int OFF_TOP = 50;
    public static final int SIDE_OFFSET = 300;
    private final String[] content;

    private UIElement titleBar;
    private MenuButton back;

    /**
     * creates a field that spreads across the screen.
     * @param title
     * @param content the content this textbox has to display
     * @param window the object with dimensions of the screen
     */
    public UITextField(String title, String[] content, Window window) {
        this(title, content, window, OFF_TOP);
    }

    /**
     * creates a field that spreads across the screen.
     * @param title
     * @param content the content this textbox has to display
     * @param window the object with dimensions of the screen
     */
    public UITextField(String title, String[] content, Window window, int offset) {
        super(
                (window.getWidth() - getWidth(window)) / 2,
                OFF_TOP + offset + BUTTON_HEIGHT,
                getWidth(window),
                // height of the middle frame
                getHeight(window, offset)
        );

        this.content = content;

        titleBar = new UIElement(x, OFF_TOP, width, BUTTON_HEIGHT) {
            @Override
            public void draw(Hud hud) {
                hud.roundedRectangle(x, y, width, height, INDENT);
                hud.fill(BACK_COLOR.x, BACK_COLOR.y, BACK_COLOR.z, BACK_COLOR.w);
                hud.stroke(STROKE_WIDTH, LINE_COLOR.x, LINE_COLOR.y, LINE_COLOR.z, LINE_COLOR.w);
                hud.text(x + width /2, (int) (y + TEXT_SIZE + 10), TEXT_SIZE, Hud.Font.MEDIUM, NVG_ALIGN_CENTER, title, TEXT_COLOR);
            }
        };
    }

    public static int getHeight(Window window, int offset) {
        return window.getHeight() - 2*BUTTON_HEIGHT - 2*offset - 100 - OFF_TOP;
    }

    public static int getWidth(Window window) {
        return window.getWidth() - 2*SIDE_OFFSET;
    }

    @Override
    public void draw(Hud hud) {
        titleBar.draw(hud);

        hud.roundedRectangle(x, y, width, height, INDENT);
        hud.fill(BACK_COLOR.x, BACK_COLOR.y, BACK_COLOR.z, BACK_COLOR.w);
        hud.stroke(STROKE_WIDTH, LINE_COLOR.x, LINE_COLOR.y, LINE_COLOR.z, LINE_COLOR.w);

        PositionState pos = new PositionState(x + width/2, (int) (y + TEXT_SIZE + 10), (int) (TEXT_SIZE * 1.2));
        for (String line : content) {
            hud.text(pos.getX(), pos.getY(), TEXT_SIZE, Hud.Font.MEDIUM, NVG_ALIGN_CENTER, line, TEXT_COLOR);
        }
    }
}
