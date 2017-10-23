package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;

import java.util.function.Consumer;

import static nl.tue.c2IOE0.group5.providers.MenuProvider.*;
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

    @Override
    public void draw(Hud hud) {
        hud.roundedRectangle(x, y, (int) (width * Math.max(value, 0.05f)), height, INDENT);
        hud.fill(COLOR_BACK_DARK.x, COLOR_BACK_DARK.y, COLOR_BACK_DARK.z, COLOR_BACK_DARK.w);
        hud.roundedRectangle(x, y, width, height, INDENT);
        hud.fill(COLOR_BACK.x, COLOR_BACK.y, COLOR_BACK.z, COLOR_BACK.w);
        hud.stroke(STROKE_WIDTH, COLOR_STROKE.x, COLOR_STROKE.y, COLOR_STROKE.z, COLOR_STROKE.w);
        hud.text(x + width /2, (int) (y + TEXT_LARGE + 10), TEXT_LARGE, Hud.Font.MEDIUM, NVG_ALIGN_CENTER,
                String.format("%1$s: %2$d%%", text, (int) ((value * 100) >= 1 ? value * 100 : 1)), COLOR_TEXT);
    }

    @Override
    public void onClick(MouseEvent event) {
        value = Math.max((float) (event.getX() - this.x) / (float) this.width, 0f);
        handler.accept(value);
    }
}
