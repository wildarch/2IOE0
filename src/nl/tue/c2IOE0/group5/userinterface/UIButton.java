package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;

import java.util.function.Consumer;

/**
 * @author Jorren Hendriks.
 */
public class UIButton extends UIElement {

    private Consumer<MouseEvent> click;

    public UIButton(int x, int y, int width, int height, Consumer<Hud> render, Consumer<MouseEvent> click) {
        super(x, y, width, height, render);
        this.click = click;
    }

    public void onClick(MouseEvent event) {
        click.accept(event);
    }
}
