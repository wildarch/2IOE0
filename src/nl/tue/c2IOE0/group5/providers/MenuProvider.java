package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Clickable;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.userinterface.MenuItem;
import nl.tue.c2IOE0.group5.userinterface.PositionState;
import nl.tue.c2IOE0.group5.userinterface.UIButton;
import nl.tue.c2IOE0.group5.userinterface.UIElement;
import org.joml.Vector2i;

import java.io.IOException;

/**
 * @author Geert van Ieperen
 */
public class MenuProvider implements Provider, Clickable {

    private static final int HEIGHT_FROM_TOP = 100;
    private static final int SPACE_BETWEEN_BUTTONS = 20;

    private UIButton[] mainMenu;
    private UIButton[] optionMenu;
    private UIButton[] activeElements;

    private Hud hud;

    @Override
    public void init(Engine engine) {
        engine.pause(true);

        final int x = (engine.getWindow().getWidth()/2) - (MenuItem.BUTTON_WIDTH/2);
        final int y = HEIGHT_FROM_TOP;
        final int offset = SPACE_BETWEEN_BUTTONS + MenuItem.BUTTON_HEIGHT;

        PositionState position = new PositionState(x, y, offset);

        UIButton startGame = new MenuItem("Start Game", position, (event) -> engine.pause(false));
        UIButton options = new MenuItem("Options", position, (event) -> activeElements = optionMenu);
        {
            PositionState optPos = new PositionState(x, y, offset);
            UIButton graphics = new MenuItem("Graphics", optPos, (event) -> {});
            UIButton parameters = new MenuItem("Parameters", optPos, (event) -> {});
            UIButton gameState = new MenuItem("Game state", optPos, (event) -> {});
            UIButton backOptions = new MenuItem("Back", optPos, (event) -> activeElements = mainMenu);
            optionMenu = new UIButton[]{graphics, parameters, gameState, backOptions};
        }
        UIButton credits = new MenuItem("Credits", position, (event) -> { });
        UIButton exitGame = new MenuItem("Exit Game", position, (event) -> engine.getWindow().close());
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

    }

    @Override
    public void onClick(MouseEvent event) {
        Vector2i mousePosition = new Vector2i(event.getX(), event.getY());
        for (UIElement element : activeElements){
            if (element instanceof UIButton && element.contains(mousePosition)) {
                ((UIButton)element).onClick(event);
            }
        }
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        // do no actual drawing (in 3d space)
    }
}
