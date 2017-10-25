package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import org.joml.Vector2i;

import java.util.function.Consumer;

import static nl.tue.c2IOE0.group5.providers.MenuProvider.*;
import static nl.tue.c2IOE0.group5.providers.MenuProvider.COLOR_TEXT;
import static nl.tue.c2IOE0.group5.providers.MenuProvider.TEXT_LARGE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;

/**
 * @author Yoeri Poels
 */
public class MenuToggleMultiple extends UIButton {
    private String text;
    private int value;
    private String[] names;
    private Consumer<Integer> handler;

    public MenuToggleMultiple(String text, int x, int y, int width, int height, String[] names, Consumer<Integer> handler) {
        super(x, y, width, height);
        this.text = text;
        this.names = names;
        this.value = 0;
        this.handler = handler;
    }

    public MenuToggleMultiple(String text, String[] names, Consumer<Integer> handler) {
        this(text, 0, 0, names, handler);
    }

    public MenuToggleMultiple(String text, int x, int y, String[] names, Consumer<Integer> handler) {
        this(text, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, names, handler);
    }

    public MenuToggleMultiple(String text, Vector2i pos, String[] names, Consumer<Integer> handler) {
        this(text, pos.x, pos.y, names, handler);
    }


    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void draw(Hud hud) {
        hud.roundedRectangle(x, y, width, height, INDENT);
        hud.fill(COLOR_BLUE);
        hud.stroke(STROKE_WIDTH, COLOR_PINK);
        hud.text(x + width /2, (int) (y + TEXT_LARGE + 10), TEXT_LARGE, Hud.Font.MEDIUM, NVG_ALIGN_CENTER,
                String.format("%1$s: %2$s", text, names[value]), COLOR_TEXT);
    }

    @Override
    public void onClick(MouseEvent event) {
        value = (value += 1)%names.length;

        handler.accept(value);
    }
}
