package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.util.PositionState;

import java.util.function.Consumer;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;

/**
 * @author Jorren
 */
public class MenuToggle extends UIButton {
    private String text;
    private boolean value;
    private String[] names;
    private Consumer<Boolean> handler;

    public MenuToggle(String text, int x, int y, int width, int height, String[] names, Consumer<Boolean> handler) {
        super(x, y, width, height);
        this.text = text;
        this.names = names;
        this.value = true;
        this.handler = handler;
    }

    public MenuToggle(String text, int x, int y, Consumer<Boolean> handler) {
        this(text, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, new String[]{"enabled","disabled"}, handler);
    }

    public MenuToggle(String text, int x, int y, String[] names, Consumer<Boolean> handler) {
        this(text, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, names, handler);
    }

    public MenuToggle(String text, PositionState pos, String[] names, Consumer<Boolean> handler) {
        this(text, pos.getX(), pos.getY(), names, handler);

    }

    public MenuToggle(String text, PositionState pos, Consumer<Boolean> handler) {
        this(text, pos.getX(), pos.getY(), handler);

    }

    @Override
    public void draw(Hud hud) {
        hud.roundedRectangle(x, y, width, height, INDENT);
        hud.fill(BACK_COLOR.x, BACK_COLOR.y, BACK_COLOR.z, BACK_COLOR.w);
        hud.stroke(STROKE_WIDTH, LINE_COLOR.x, LINE_COLOR.y, LINE_COLOR.z, LINE_COLOR.w);
        hud.text(x + width /2, (int) (y + TEXT_SIZE + 10), TEXT_SIZE, Hud.Font.MEDIUM, NVG_ALIGN_CENTER, String.format("%1$s: %2$s", text, names[value ? 0 : 1]), TEXT_COLOR);
    }

    @Override
    public void onClick(MouseEvent event) {
        value = !value;
        handler.accept(value);
    }
}
