package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.util.PositionState;
import org.joml.Vector2i;

import java.util.function.Consumer;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;

/**
 * @author Jorren
 */
public class MenuSlider extends UIButton {

    private String text;
    private float value;
    private Consumer<Float> handler;

    public MenuSlider(String text, int x, int y, int width, int height, Consumer<Float> handler) {
        super(x, y, width, height);
        this.text = text;
        this.value = 1f;
        this.handler = handler;
    }

    public MenuSlider(String text, int x, int y, Consumer<Float> handler) {
        this(text, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
    }

    public MenuSlider(String text, Consumer<Float> handler) {
        this(text, 0, 0, handler);
    }

    public MenuSlider(String name, PositionState pos, Consumer<Float> handler) {
        this(name, pos.getX(), pos.getY(), handler);

    }

    @Override
    public void draw(Hud hud) {
        hud.roundedRectangle(x, y, (int) (width * Math.max(value, 0.05f)), height, INDENT);
        hud.fill(BACK_COLOR_DARK.x, BACK_COLOR_DARK.y, BACK_COLOR_DARK.z, BACK_COLOR_DARK.w);
        hud.roundedRectangle(x, y, width, height, INDENT);
        hud.fill(BACK_COLOR.x, BACK_COLOR.y, BACK_COLOR.z, BACK_COLOR.w);
        hud.stroke(STROKE_WIDTH, LINE_COLOR.x, LINE_COLOR.y, LINE_COLOR.z, LINE_COLOR.w);
        hud.text(x + width /2, (int) (y + TEXT_SIZE + 10), TEXT_SIZE, Hud.Font.MEDIUM, NVG_ALIGN_CENTER, String.format("%1$s: %2$d%%", text, (int) ((value * 100) >= 1 ? value * 100 : 1)), TEXT_COLOR);
    }

    @Override
    public void onClick(MouseEvent event) {
        value = Math.max((float) (event.getX() - this.x) / (float) this.width, 0f);
        handler.accept(value);
    }
}
