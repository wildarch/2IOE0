package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.userinterface.MenuItem;
import nl.tue.c2IOE0.group5.userinterface.UIButton;
import nl.tue.c2IOE0.group5.userinterface.UIElement;
import org.joml.Vector2i;

/**
 * Created by Geert van Ieperen on 12-10-2017.
 */
public class MenuProvider implements Provider {

    private Window window;

    private UIButton[] mainMenu;
    private UIButton[] optionMenu;
    private UIButton[] activeElements;

    private Engine engine;
    private Hud hud;

    @Override
    public void init(Engine engine) {
        this.engine = engine;
        engine.pause(true);

        int x = 0;
        int OFFSET = 50;

        int mainY = 100;
        UIButton startGame = new MenuItem("Start Game", x, mainY += OFFSET, (event) -> engine.pause(false));
        UIButton options = new MenuItem("Options", x, mainY += OFFSET, (event) -> activeElements = optionMenu);
        {
            int optionsY = 0;
            UIButton graphics = new MenuItem("Graphics", x, optionsY += OFFSET, (event) -> {});
            UIButton parameters = new MenuItem("Parameters", x, optionsY += OFFSET, (event) -> {});
            UIButton gameState = new MenuItem("Game state", x, optionsY += OFFSET, (event) -> {});
            UIButton backOptions = new MenuItem("Back", x, optionsY + OFFSET, (event) -> activeElements = mainMenu);
            optionMenu = new UIButton[]{graphics, parameters, gameState, backOptions};
        }
        UIButton credits = new MenuItem("Credits", x, mainY += OFFSET, (event) -> { });
        UIButton exitGame = new MenuItem("Exit Game", x, mainY + OFFSET, (event) -> engine.getWindow().close());
        mainMenu = new UIButton[]{startGame, options, credits, exitGame};

        this.hud = engine.getHud();
        activeElements = mainMenu;
    }

    @Override
    public void update() {
        // set x-position of all elements to width/2
        int middle = (engine.getWindow().getWidth())/2;

        for (UIButton element : activeElements){
            element.updateXPosition(middle);
        }
    }

    /**
     * fires the click event of only the focussed element
     */
    public void onClick(MouseEvent event) {
        Vector2i mousePosition = new Vector2i(event.getX(), event.getY());
        for (UIButton element : activeElements){
            if (element.contains(mousePosition)) {
                element.onClick(event);
            }
        }
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        if (!engine.isPaused()) return;

        for (UIElement element : activeElements) {
            hud.create(() -> element.draw(hud));
        }
    }

}
