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

/**
 * Created by Geert van Ieperen on 12-10-2017.
 */
public class MenuProvider implements Provider {

    private Window window;

    private UIButton startGame;
    private UIButton graphics;
    private UIButton parameters;
    private UIButton gameState;
    private UIButton backOptions;
    private UIButton options;
    private UIButton credits;
    private UIButton exitGame;

    private final UIButton[] mainMenu = {startGame, options, credits, exitGame};
    private final UIButton[] optionMenu = {graphics, parameters, gameState, backOptions};

    private UIButton[] activeItems;

    private Engine engine;
    private Hud hud;

    List<UIElement> elements;

    @Override
    public void init(Engine engine) {
        this.engine = engine;
        engine.pause(true);

        int x = 0;
        int mainY = 30;
        int OFFSET = 50;

        startGame = new MenuItem("Start Game", x, mainY += OFFSET, (event) -> { engine.pause(false); });
        options = new MenuItem("Options", x, mainY += OFFSET, (event)-> activeItems = optionMenu);
        {
            int optionsY = 0;
            graphics = new MenuItem("Graphics", x, optionsY += OFFSET, (event)->{});
            parameters = new MenuItem("Parameters", x, optionsY += OFFSET, (event)->{});
            gameState = new MenuItem("Game state", x, optionsY += OFFSET, (event)->{});
            backOptions = new MenuItem("Back", x, optionsY, (event) -> activeItems = mainMenu);
        }
        credits = new MenuItem("Credits", x, mainY += OFFSET, (event)->{});
        exitGame = new MenuItem("Exit Game", x, mainY, (event)->{});

        this.hud = engine.getHud();
        elements = new ArrayList<>();

        elements.add(startGame);
        elements.add(options);
        elements.add(credits);
        elements.add(exitGame);
    }

    @Override
    public void update() {
        int middle = (engine.getWindow().getWidth())/2;
        for (UIElement item : activeItems){
            item.updateXPosition(middle);
        }
    }

    public void onClick(MouseEvent event) {

        elements.forEach((element) -> {
            if (element instanceof UIButton) {
                ((UIButton) element).onClick(event);
            }
        });
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        if (!engine.isPaused()) return;

        hud.create(() -> {
            elements.forEach((element) -> element.draw(hud));
        });
    }

}
