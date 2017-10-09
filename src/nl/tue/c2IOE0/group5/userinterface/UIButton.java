package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;

import java.util.function.Consumer;

/**
 * @author Jorren Hendriks.
 */
public class UIButton extends UIElement {

    private Consumer<MouseEvent> click;

    public UIButton(int x1, int y1, int x2, int y2, Runnable render, Consumer click) {
        super(x1, y1, x2, y2, render);
        this.click = click;
    }

    public void onClick(MouseEvent event) {
        click.accept(event);
    }
}
