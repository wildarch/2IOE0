package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.userinterface.MenuItem;

/**
 * Created by Geert van Ieperen on 12-10-2017.
 */
public class MenuProvider implements Provider {

    private Hud hud;
    private Window window;

    private MenuItem startGame;
    private MenuItem graphics;
    private MenuItem parameters;
    private MenuItem gameState;
    private MenuItem backOptions;
    private MenuItem options;
    private MenuItem credits;
    private MenuItem exitGame;

    private final MenuItem[] mainMenu = {startGame, options, credits, exitGame};
    private final MenuItem[] optionMenu = {graphics, parameters, gameState, backOptions};

    private MenuItem[] activeItems;

    @Override
    public void init(Engine engine) {
        int mmx = 0;
        int y = 0;
        final int OFFSET = (50 + MenuItem.BUTTON_HEIGHT);

        startGame = new MenuItem(mmx += OFFSET, y, ()->{}, "Start Game");
        options = new MenuItem(mmx += OFFSET, y, ()-> activeItems = optionMenu, "Options");
        {
            int omx = 0;
            graphics = new MenuItem(omx += OFFSET, y, ()->{}, "Graphics");
            parameters = new MenuItem(omx += OFFSET, y, ()->{}, "Parameters");
            gameState = new MenuItem(omx += OFFSET, y, ()->{}, "Game state");
            backOptions = new MenuItem(omx, y, () -> activeItems = mainMenu, "Back");
        }
        credits = new MenuItem(mmx += OFFSET, y, ()->{}, "Credits");
        exitGame = new MenuItem(mmx, y, ()->{}, "Exit Game");

        this.hud = engine.getHud();
        this.window = engine.getWindow();
        activeItems = mainMenu;
    }

    @Override
    public void update() {
        int middle = window.getWidth();
        for (MenuItem activeItem : activeItems) {
            activeItem.updatePosition(middle);
        }
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        for (MenuItem activeItem : activeItems) {
            activeItem.draw(hud);
        }
    }
}
