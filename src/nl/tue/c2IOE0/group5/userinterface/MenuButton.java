package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.util.PositionState;
import org.joml.Vector4f;

import java.util.function.Consumer;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;

/**
 * @author Geert van Ieperen
 * a button with set shape and text
 */
public class MenuButton extends UIButton {

    private final String text;
    private Vector4f backColor = BACK_COLOR;
    private Vector4f textColor = TEXT_COLOR;

    private Consumer<MouseEvent> click;

    public MenuButton(String text, int x, int y, Consumer<MouseEvent> click) {
        this(text, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, click);
    }

    public MenuButton(String text, int x, int y, int width, int height, Consumer<MouseEvent> click) {
        super(x, y, width, height);
        this.text = text;
        this.click = click;
    }

    public MenuButton(String text, PositionState state, Consumer<MouseEvent> click){
        this(text, state.getX(), state.getY(), click);
    }

    public MenuButton(String text, PositionState state){
        this(text, state.getX(), state.getY(), (none) -> {});
        backColor = new Vector4f(0.5f, 0.5f, 0.5f, 1);
        textColor = TEXT_COLOR.div(2);
    }

    @Override
    public void draw(Hud hud) {

        hud.roundedRectangle(x, y, width, height, INDENT);
        hud.fill(backColor.x, backColor.y, backColor.z, backColor.w);
        hud.stroke(STROKE_WIDTH, LINE_COLOR.x, LINE_COLOR.y, LINE_COLOR.z, LINE_COLOR.w);
        hud.text(x + width /2, (int) (y + TEXT_SIZE + 10), TEXT_SIZE, Hud.Font.MEDIUM, NVG_ALIGN_CENTER, text, textColor);
    }

    @Override
    public void onClick(MouseEvent event) {
        click.accept(event);
    }
}
