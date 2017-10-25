package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.controllers.PlayerController;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.userinterface.*;
import org.joml.Vector2i;
import org.joml.Vector4f;

/**
 * @author Jorren
 */
public class UIProvider implements Provider<Engine> {


    private Window window;
    private Hud hud;
    public TowerProvider towerProvider;

    private Class<? extends AbstractTower> selectedTower;

    public static final Vector4f COLOR_TEXT = new Vector4f(1f, 1f, 1f, 1f);
    public static final Vector4f COLOR_BLUE = new Vector4f(0.14118f, 0.20392f, 0.49804f, 1f);
    public static final Vector4f COLOR_PINK = new Vector4f(0.81569f, 0.15686f, 0.35686f, 1f);
    public static final Vector4f COLOR_DARK = new Vector4f(0f, 0f, 0f, 0.3f);

    private UIButton buildBar;
    private UIText playerBudget;
    private UIElement[] deadScreen;

    private final static String[] creditTextfield =
            (       "You did a good job.\n" +
                    "Defended the tower well.\n" +
                    "But in the end,\n" +
                    "you lost.\n" +
                    "Good luck with the rest of your life.\n" +
                    "\n" +
                    "Produced by TU/entertainment"
            ).split("\n");


    private static final int HEIGHT_FROM_TOP = 100;
    private static final int TEXTFIELD_WIDTH = 750;
    private static final int TEXTFIELD_HEIGHT = 450;
    public static final int MARGIN = 20;


    private boolean dead = false;
    private Engine engine;

    @Override
    public void init(Engine engine) {
        this.engine = engine;
        this.hud = engine.getHud();
        this.window = engine.getWindow();
        this.towerProvider = engine.getProvider(TowerProvider.class);

        buildBar = new Buildbar(80, 120, this);
        PlayerController playerController = engine.getController(PlayerController.class);
        playerBudget = new UIText(10, 40, 100, 20,
                () -> "Budget: " +playerController.getBudget()
        );

        UIElement dead = new MenuTextField("You are dead", creditTextfield, TEXTFIELD_WIDTH, TEXTFIELD_HEIGHT);
        MenuButton deadQuitButton = new MenuButton("Quit", (event) -> {
            //shutdown
            System.exit(0);
        });
        deadScreen = new UIElement[]{dead, deadQuitButton};

        hud.create(() -> {
            if (engine.isPaused()) return;
            if (!this.dead) {
                buildBar.draw(hud);
                playerBudget.draw(hud);
            } else {
                for (UIElement element : deadScreen) {
                    element.draw(hud);
                }
                MenuPositioner pos = new MenuPositioner((window.getWidth()/2), HEIGHT_FROM_TOP, MARGIN);

                for (UIElement element : deadScreen) {
                    Vector2i p = pos.place(element, true);
                    element.setX(p.x - element.getWidth()/2);
                    element.setY(p.y);
                }
            }
        });
    }

    public void die() {
        dead = true;
    }

    public boolean onClick(MouseEvent event) {
        if (!dead) {
            boolean passthrough = true;

            if (buildBar.contains(event.getPosition())) {
                buildBar.onClick(event);
                passthrough = false;
            }

            return passthrough;
        } else {
            Vector2i mousePosition = new Vector2i(event.getX(), event.getY());
            for (UIElement element : deadScreen){
                if (element instanceof UIButton && element.contains(mousePosition)) {
                    ((UIButton)element).onClick(event);
                }
            }
            return false;
        }
    }

    private int bottom(int y) {
        return window.getHeight() - y;
    }

    private int right(int x) {
        return window.getWidth() - x;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Window window, Renderer renderer) {
        buildBar.setX(window.getWidth()/2 - buildBar.getWidth()/2);
        buildBar.setY(window.getHeight() - buildBar.getHeight() - 20);
    }

    public void select(Class<? extends AbstractTower> tower) {
        if (this.selectedTower == tower || tower == null) {
            this.selectedTower = null;
            return;
        } else {
            PlayerController playerController = towerProvider.getEngine().getController(PlayerController.class);
            int budget = playerController.getBudget();
            AbstractTower.MetaData metaData = AbstractTower.getMetaData(tower);
            int price = metaData.price;

            if (price > budget) {
                this.selectedTower = null;
            } else {
                this.selectedTower = tower;
            }
        }
    }

    public Class<? extends AbstractTower> getSelected() {
        return this.selectedTower;
    }
}
