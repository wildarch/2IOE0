package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Clickable;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.userinterface.MenuButton;
import nl.tue.c2IOE0.group5.userinterface.UIButton;
import nl.tue.c2IOE0.group5.userinterface.UIElement;
import nl.tue.c2IOE0.group5.userinterface.UITextField;
import nl.tue.c2IOE0.group5.util.PositionState;
import org.joml.Vector2i;

import static nl.tue.c2IOE0.group5.userinterface.UIElement.BUTTON_HEIGHT;
import static nl.tue.c2IOE0.group5.userinterface.UIElement.BUTTON_WIDTH;

/**
 * @author Geert van Ieperen
 */
public class MenuProvider implements Provider, Clickable {

    private final static String[] creditTextfield = // newlines with backslash because of Regex splitting
            ("Staff:/" +
                    "Tim Beurskens/" +
                    "Daan Drijver/" +
                    "Daan de Graaf/" +
                    "Jorren Hendriks/" +
                    "Geert van Ieperen/" +
                    "Tom Peters/" +
                    "Yoeri Poels/" +
                    "/" +
            "Produced on the Technical University Eindhoven"
            ).split("/");


    private static final int HEIGHT_FROM_TOP = 100;
    private static final int SPACE_BETWEEN_BUTTONS = 20;

    private UIButton[] mainMenu;
    private UIButton[] optionMenu;
    private UIElement[] creditScreen;

    private UIElement[] activeElements;

    private Hud hud;

    @Override
    public void init(Engine engine) {
        engine.pause(true);

        final Window window = engine.getWindow();
        final int x = (window.getWidth()/2) - (BUTTON_WIDTH/2);
        final int y = HEIGHT_FROM_TOP;
        final int offset = SPACE_BETWEEN_BUTTONS + UIElement.BUTTON_HEIGHT;


        PositionState position = new PositionState(x, y, offset);
        UIButton startGame = new MenuButton("Start Game", position, (event) -> engine.pause(false));
        UIButton options = new MenuButton("Options", position, (event) -> activeElements = optionMenu);
        {
            PositionState optPos = new PositionState(x, y, offset);
            UIButton graphics = new MenuButton("Graphics", optPos, (event) -> {});
            UIButton parameters = new MenuButton("Parameters", optPos, (event) -> {});
            UIButton gameState = new MenuButton("Game state", optPos, (event) -> {});
            UIButton backOptions = new MenuButton("Back", optPos, (event) -> activeElements = mainMenu);
            optionMenu = new UIButton[]{graphics, parameters, gameState, backOptions};
        }
        UIButton credits = new MenuButton("Credits", position, (event) -> activeElements = creditScreen);
        UIButton exitGame = new MenuButton("Exit Game", position, (event) -> window.close());
        mainMenu = new UIButton[]{startGame, options, credits, exitGame};

        MenuButton creditBackButton = new MenuButton("Back",
                ((window.getWidth() + UITextField.getWidth(window)) / 2) - BUTTON_WIDTH,
                window.getHeight() - (BUTTON_HEIGHT + 100),
                (event) -> activeElements = mainMenu
        );
        creditScreen = new UIElement[]{
                new UITextField("Credits", creditTextfield, window),
                creditBackButton
        };

        this.hud = engine.getHud();
        activeElements = mainMenu;

        hud.create(() -> {
            if (!engine.isPaused()) return;

            for (UIElement element : activeElements) {
                element.draw(hud);
            }
        });
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
    public void update() {

    }

    @Override
    public void draw(Window window, Renderer renderer) {

    }
}
