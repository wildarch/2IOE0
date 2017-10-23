package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Clickable;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.userinterface.*;
import org.joml.Vector2i;
import org.joml.Vector4f;

/**
 * @author Geert van Ieperen, Jorren Hendriks
 */
public class MenuProvider implements Provider<Engine>, Clickable {

    private final static String[] creditTextfield =
            ("Staff:\n" +
                    "Tim Beurskens\n" +
                    "Daan Drijver\n" +
                    "Daan de Graaf\n" +
                    "Jorren Hendriks\n" +
                    "Geert van Ieperen\n" +
                    "Tom Peters\n" +
                    "Yoeri Poels\n" +
                    "\n" +
            "Produced by TU/entertainment"
            ).split("\n");


    private static final int HEIGHT_FROM_TOP = 100;
    private static final int TEXTFIELD_WIDTH = 750;
    private static final int TEXTFIELD_HEIGHT = 450;

    public static final int MARGIN = 20;
    public static final int BUTTON_WIDTH = 500;
    public static final int BUTTON_HEIGHT = 75;
    public static final float TEXT_LARGE = 42f;
    public static final float TEXT_SMALL = 30f;
    public static final int STROKE_WIDTH = 5;
    public static final int INDENT = 10;

    public static final Vector4f COLOR_BACK = new Vector4f(0.3f, 0.3f, 0.8f, 0.8f);
    public static final Vector4f COLOR_BACK_DARK = new Vector4f(0f, 0f, 0f, 0.6f);
    public static final Vector4f COLOR_TEXT = new Vector4f(1f, 1f, 1f, 1f);
    public static final Vector4f COLOR_STROKE = new Vector4f(0.8f, 0.3f, 0.3f, 0.8f);

    private UIButton[] mainMenu;
    private UIButton[] optionMenu;
    private UIButton[] graphicsMenu;
    private UIButton[] audioMenu;
    private UIElement[] creditScreen;

    private UIElement[] activeElements;

    private Hud hud;
    private Renderer renderer;
    private Window window;
    private MusicProvider musicProvider;

    @Override
    public void init(Engine engine) {
        engine.pause(true);

        renderer = engine.getRenderer();
        window = engine.getWindow();
        musicProvider = engine.getProvider(MusicProvider.class);

        UIButton startGame = new MenuButton("Start Game", (event) -> engine.pause(false));
        UIButton options = new MenuButton("Options", (event) -> activeElements = optionMenu);
        {
            UIButton graphics = new MenuButton("Graphics", (event) -> activeElements = graphicsMenu);
            {
                UIButton shadow = new MenuToggle("Shadow", (b) -> renderer.setShadowMapping(b));
                UIButton vsync = new MenuToggle("vSync", (b) -> window.getOptions().vSync = b);
                UIButton antia = new MenuToggle("Anti Aliasing", (b) -> window.getOptions().antialiasing = b ? 4 : 0);
                UIButton backGraphics = new MenuButton("Back", (event) -> activeElements = optionMenu);
                graphicsMenu = new UIButton[]{shadow, vsync, antia, backGraphics};
            }
            UIButton audio = new MenuButton("Audio", (event) -> activeElements = audioMenu);
            {
                UIButton master = new MenuSlider("Volume", (i) -> musicProvider.setBaseVolume((i < 0.05f ? 0.05f : i)));
                UIButton toggleAudio = new MenuToggle("Music", (i) -> musicProvider.toggle());
                UIButton backAudio = new MenuButton("Back", (event) -> activeElements = optionMenu);
                audioMenu = new UIButton[]{master, toggleAudio, backAudio};
            }

            UIButton parameters = new MenuButton("Parameters", (event) -> {});
            UIButton gameState = new MenuButton("Game state", (event) -> {});
            UIButton backOptions = new MenuButton("Back", (event) -> activeElements = mainMenu);
            optionMenu = new UIButton[]{graphics, audio, parameters, gameState, backOptions};
        }
        UIButton credits = new MenuButton("Credits", (event) -> activeElements = creditScreen);
        {
            UIElement credit = new MenuTextField("Credits", creditTextfield, TEXTFIELD_WIDTH, TEXTFIELD_HEIGHT);
            MenuButton creditBackButton = new MenuButton("Back", (event) -> activeElements = mainMenu);
            creditScreen = new UIElement[]{credit, creditBackButton};
        }
        UIButton exitGame = new MenuButton("Exit Game", (event) -> window.close());
        mainMenu = new UIButton[]{startGame, options, credits, exitGame};

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
        MenuPositioner pos = new MenuPositioner((window.getWidth()/2), HEIGHT_FROM_TOP, MARGIN);

        for (UIElement element : activeElements) {
            Vector2i p = pos.place(element, true);
            element.setX(p.x - element.getWidth()/2);
            element.setY(p.y);
        }
    }
}
