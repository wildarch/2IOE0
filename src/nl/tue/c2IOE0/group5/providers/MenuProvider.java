package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Clickable;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.userinterface.MenuItem;
import nl.tue.c2IOE0.group5.userinterface.UIButton;
import nl.tue.c2IOE0.group5.userinterface.UIElement;
import org.joml.Vector2i;

import java.io.IOException;

import static nl.tue.c2IOE0.group5.userinterface.MenuItem.BUTTON_WIDTH;

/**
 * @author Geert van Ieperen
 */
public class MenuProvider implements Provider, Clickable {

    private int middle;

    private UIButton[] mainMenu;
    private UIButton[] optionMenu;
    private UIButton[] activeElements;

    private Engine engine;
    private Hud hud;

    @Override
    public void init(Engine engine) {
        this.engine = engine;
        engine.pause(true);

        int x = -BUTTON_WIDTH/2;
        int y = 100;
        int offset = 50;

        UIButton startGame = new MenuItem("Start Game", x, y += offset, (event) -> engine.pause(false));
        UIButton options = new MenuItem("Options", x, y += offset, (event) -> activeElements = optionMenu);
        {
            int optionsY = 0;
            UIButton graphics = new MenuItem("Graphics", x, optionsY += offset, (event) -> {});
            UIButton parameters = new MenuItem("Parameters", x, optionsY += offset, (event) -> {});
            UIButton gameState = new MenuItem("Game state", x, optionsY += offset, (event) -> {});
            UIButton backOptions = new MenuItem("Back", x, optionsY + offset, (event) -> activeElements = mainMenu);
            optionMenu = new UIButton[]{graphics, parameters, gameState, backOptions};
        }
        UIButton credits = new MenuItem("Credits", x, y += offset, (event) -> { });
        UIButton exitGame = new MenuItem("Exit Game", x, y + offset, (event) -> engine.getWindow().close());
        mainMenu = new UIButton[]{startGame, options, credits, exitGame};

        this.hud = engine.getHud();
        activeElements = mainMenu;

        hud.create(() -> {
            if (!engine.isPaused()) return;

            try {
                for (UIElement element : activeElements) {
                    element.draw(hud);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void update() {
        // set x-position of all elements to width/2
        int middle = (engine.getWindow().getWidth())/2;

        if (this.middle != middle) {
            this.middle = middle;
            for (UIElement element : activeElements) {
                element.setX(middle - BUTTON_WIDTH/2);
            }
        }
    }

    @Override
    public void onClick(MouseEvent event) {
        Vector2i mousePosition = new Vector2i(event.getX(), event.getY());
        for (UIElement element : activeElements) {
            if (element instanceof UIButton && element.contains(mousePosition)) {
                ((UIButton) element).onClick(event);
            }
        }
    }
}
