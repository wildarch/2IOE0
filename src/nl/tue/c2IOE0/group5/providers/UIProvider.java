package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.controllers.AiController;
import nl.tue.c2IOE0.group5.controllers.PlayerController;
import nl.tue.c2IOE0.group5.enemies.EnemyType;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.MainTower;
import nl.tue.c2IOE0.group5.userinterface.*;
import org.joml.Vector2i;
import org.joml.Vector4f;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_RIGHT;

/**
 * Provider responsible for managing all in-game Hud elements
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
    private UIElement budget;
    private UIElement mainHealth;
    private boolean firstTime = true;
    private int enemyTypeDrill = 0;
    private int enemyTypeBasic = 0;
    private int enemyTypeWalker = 0;

    private UIText playerBudget;
    private UIText waveIndicator;
    private UIElement[] deadScreen;
    private long scoreTrack = 0;
    private long loseTime;
    private MainTower mainTower;


    private final static  String[] creditTextfield =
            (       "You did a good job.\n" +
                    "Defended the tower well.\n" +
                    "But in the end,\n" +
                    "you lost.\n" +
                    "Good luck with the rest of your life.\n" +
                    "\n" +
                    "Produced by TU/entertainment."
            ).split("\n");


    private static final int HEIGHT_FROM_TOP = 100;
    private static final int TEXTFIELD_WIDTH = 750;
    private static final int TEXTFIELD_HEIGHT = 340;
    public static final int MARGIN = 20;


    private boolean dead = false;
    private Engine engine;
    private GridProvider gridProvider;

    @Override
    public void init(Engine engine) {
        this.engine = engine;
        this.gridProvider = engine.getProvider(GridProvider.class);
        this.hud = engine.getHud();
        this.window = engine.getWindow();
        this.towerProvider = engine.getProvider(TowerProvider.class);
        buildBar = new Buildbar(80, 120, this);
        PlayerController playerController = engine.getController(PlayerController.class);
        budget = new UIText(0, 20, NVG_ALIGN_LEFT,
                () -> String.format("Budget: %d", playerController.getBudget()));
        mainHealth = new UIText(0, 20, NVG_ALIGN_RIGHT,
                () -> String.format("Health: %d/%d", mainTower.getHealth(), towerProvider.getMainTower().maxHealth));
        AiController aiController = engine.getController(AiController.class);
        waveIndicator = new UIText(0, 40, NVG_ALIGN_CENTER,
                () -> "Wave: " + aiController.getBigWaves());


        UIElement dead = new MenuTextField("You are dead", creditTextfield, TEXTFIELD_WIDTH, TEXTFIELD_HEIGHT);
        MenuButton deadQuitButton = new MenuButton("Quit", (event) -> {
            //shutdown
            System.exit(0);
        });
        deadScreen = new UIElement[]{dead, deadQuitButton};

        // Create the hud and register all elements that should be drawn in it
        hud.create(() -> {
            if (engine.isPaused()) return;
            if (!this.dead) {
                buildBar.draw(hud);
                budget.draw(hud);
                mainHealth.draw(hud);
                waveIndicator.draw(hud);
            } else {

                if (scoreTrack == 0) {
                    enemyTypeWalker = gridProvider.getKills(EnemyType.WALKER);
                    enemyTypeBasic = gridProvider.getKills(EnemyType.BASIC);
                    enemyTypeDrill = gridProvider.getKills(EnemyType.DRILL);
                    scoreTrack = (System.currentTimeMillis() - engine.getScoreTimer());
                }

                if (firstTime) {
                    firstTime = false;
                    loseTime = System.currentTimeMillis();
                }

                String[] scoreString =
                        ("Time survived: " + Long.toString(scoreTrack / 1000) + "." + Long.toString(scoreTrack % 100) + "s \n" +
                                "Drill Enemies Killed: " + enemyTypeDrill + " \n" +
                                "Basic Enemies Killed: " + enemyTypeBasic + " \n" +
                                "Walker Enemies Killed: " + enemyTypeWalker + " \n"
                        ).split("\n");
                int ScoreHeight = 235;
                UIElement score = new MenuTextField("Score", scoreString, TEXTFIELD_WIDTH, ScoreHeight);

                for (UIElement element : deadScreen) {
                    element.draw(hud);
                }

                MenuPositioner pos = new MenuPositioner((window.getWidth() / 2), HEIGHT_FROM_TOP, MARGIN);
                MenuPositioner qos = new MenuPositioner((window.getWidth() / 2), HEIGHT_FROM_TOP, MARGIN);

                Vector2i q = qos.place(score, true);
                score.setX(q.x - score.getWidth() / 2);
                score.setY(q.y + TEXTFIELD_HEIGHT + 10);
                score.draw(hud);

                for (UIElement element : deadScreen) {
                    Vector2i p = pos.place(element, true);
                    element.setX(p.x - element.getWidth()/2);
                    element.setY(p.y);
                }
                if (System.currentTimeMillis() > loseTime + 2500){
                    firstTime = true;
                    this.dead = false;
                    playerController.resetGame();
                }
            }
        });
    }

    public void setMainTower(MainTower mainTower) {
        this.mainTower = mainTower;
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

    @Override
    public void update() {

    }

    @Override
    public void draw(Window window, Renderer renderer) {
        buildBar.setX(window.getWidth()/2 - buildBar.getWidth()/2);
        buildBar.setY(window.getHeight() - buildBar.getHeight() - MARGIN);
        budget.setX(MARGIN);
        budget.setY(window.getHeight() - budget.getHeight() - MARGIN);
        mainHealth.setX(window.getWidth() - mainHealth.getWidth() - MARGIN);
        mainHealth.setY(window.getHeight() - mainHealth.getHeight() - MARGIN);
        waveIndicator.setX(window.getWidth()/2);
        waveIndicator.setY(2*MARGIN);
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
