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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Geert van Ieperen on 12-10-2017.
 */
public class MenuProvider implements Provider {

    int screenMiddle;

    private Engine engine;
    private Hud hud;

    UIButton startGame;
    UIButton options;
        MenuItem graphics;
        MenuItem parameters;
        MenuItem gamestate;
    UIButton credits;
    UIButton exitgame;

    List<UIElement> elements;

    MenuItem[] activeItems;

    @Override
    public void init(Engine engine) {
        this.engine = engine;
        engine.pause(true);

        /*
        startGame = new MenuItem("Start Game", ()->{System.out.print("start");});
        options = new MenuItem("Options", ()-> activeItems = new MenuItem[]{graphics, parameters, gamestate});
        credits = new MenuItem("Credits", ()->{});
        exitgame = new MenuItem("Exit Game", ()->{});
        */

        this.hud = engine.getHud();
        elements = new ArrayList<>();

        startGame = new MenuItem("Begin",10, 10, 100, 50, (event) -> { engine.pause(false); });
        options = new MenuItem("Options", 10, 70, 100, 50, (event) -> {});
        credits = new MenuItem("Credits", 10, 130, 100, 50, (event) -> {});
        exitgame = new MenuItem("Exit", 10, 190, 100, 50, (event) -> {});

        elements.add(startGame);
        elements.add(options);
        elements.add(credits);
        elements.add(exitgame);

        //activeItems = new MenuItem[]{startGame, options, credits, exitgame};

        //screenMiddle = (engine.getWindow().getWidth())/2;
    }

    @Override
    public void update() { }

    public void onClick(MouseEvent event) {

        elements.forEach((element) -> {
            if (element instanceof UIButton) {
                ((UIButton) element).onClick(event);
            }
        });

        /*
        for(MenuItem menuItem : activeItems) {
            if (menuItem.contains(event.getPosition())) {
                menuItem.onClick();
            }
        }
        */
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        if (!engine.isPaused()) return;

        hud.create(() -> {
            elements.forEach((element) -> element.draw(hud));
            /*
            for (int i = 0; i < activeItems.length; i++) {
                activeItems[i].draw(screenMiddle, MenuItem.BUTTON_HEIGHT + (50 * i), hud);
            }
            */
        });
    }

}
