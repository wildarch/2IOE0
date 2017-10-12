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

    int screenMiddle;

    private Hud hud;

    MenuItem startGame;
    MenuItem options;
        MenuItem graphics;
        MenuItem parameters;
        MenuItem gamestate;
    MenuItem credits;
    MenuItem exitgame;

    MenuItem[] activeItems;

    @Override
    public void init(Engine engine) {
        startGame = new MenuItem("Start Game", ()->{});
        options = new MenuItem("Options", ()-> activeItems = new MenuItem[]{graphics, parameters, gamestate});
        credits = new MenuItem("Credits", ()->{});
        exitgame = new MenuItem("Exit Game", ()->{});

        this.hud = engine.getHud();
        activeItems = new MenuItem[]{startGame, options, credits, exitgame};

        screenMiddle = (engine.getWindow().getWidth())/2;
    }

    @Override
    public void update() { }

    @Override
    public void draw(Window window, Renderer renderer) {
        for (int i = 0; i < activeItems.length; i++) {
            activeItems[i].draw(screenMiddle, MenuItem.BUTTON_HEIGHT + (50 * i), hud);
        }
    }
}
