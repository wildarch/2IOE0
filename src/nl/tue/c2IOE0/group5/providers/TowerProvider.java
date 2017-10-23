package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.controllers.PlayerController;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Simulator;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.towers.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class TowerProvider extends ObjectProvider<AbstractTower> {
    public GridProvider gridProvider;
    public EnemyProvider enemyProvider;
    public BulletProvider bulletProvider;
    public Timer loopTimer;
    public Timer renderTimer;

    private MainTower mainTower;
    private Engine engine;

    boolean gameStarted = false;

    @Override
    public void init(Simulator engine) {
        super.init(engine);
        gridProvider = engine.getProvider(GridProvider.class);
        enemyProvider = engine.getProvider(EnemyProvider.class);
        bulletProvider = engine.getProvider(BulletProvider.class);
        loopTimer = engine.getGameloopTimer();
        gameStarted = false;
    }

    /**
     * called when the "start game" button is pressed.
     */
    public void startGame() {
        //only place towers when game is started for the first time
        if(gameStarted) return;

        putMainTower();
        buildTower(7, 8, WallTower.class);
        buildTower(6, 8, WallTower.class);
        buildTower(8, 8, RocketTower.class);
        buildTower(7, 7, CannonTower.class);
        gameStarted = true;
    }

    @Override
    public void renderInit(Engine engine) {
        this.engine = engine;
        Mesh m = engine.getRenderer().linkMesh("/models/towers/mainbase/mainbase.obj");
        m.setMaterial(new Material("/models/towers/mainbase/mainbase.png"));
        renderTimer = engine.getRenderLoopTimer();
    }

    /**
     * Create a tower according to a tower type at a certain location
     * @Returns true if build succesful, false if there already is a tower
     * @Throws Many exceptions when passing class type as argument fails: So an incorrect type was passed (not a tower)
     */
    public boolean buildTower(int x, int y, Class<? extends AbstractTower> towertype) {
        if (gridProvider.getCell(x, y).getTower() != null) {
            //not null: tower exists here already
            return false;
        }
        //create the tower
        AbstractTower tower = null;
        try {
            Constructor<?> constructor = towertype.getDeclaredConstructor(TowerProvider.class);
            tower = (AbstractTower) constructor.newInstance(this);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Class " + towertype.getName() +
                    " does not have a matching constructor, should take one parameter: TowerProvider");
        }
        tower.init(getRenderer());
        //place it
        gridProvider.placeTower(x, y, tower);
        objects.add(tower);
        //placing tower succesfull!

        //subtract the price
        if (all().contains(towertype)) {
            AbstractTower.MetaData metaData;
            try {
                Field meta = towertype.getField("metadata");
                metaData = (AbstractTower.MetaData) meta.get(null);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new IllegalStateException("Tower " + towertype.getName() +
                        " does not have a field metadata, or it is not marked static public");
            }
            int price = metaData.price;
            PlayerController playerController = engine.getController(PlayerController.class);
            playerController.addBudget(-price);
        }

        return true;
    }

    /**
     * Get a list of all tower classes
     *
     * @return A list of tower classes
     */
    public List<Class<? extends AbstractTower>> all() {
        return Arrays.asList(CannonTower.class, RocketTower.class, WallTower.class);
    }

    public void putMainTower() {
        int x = gridProvider.SIZE / 2;
        mainTower = new MainTower(this).init(getRenderer());
        gridProvider.placeTower(x, x, mainTower);
        objects.add(mainTower);
    }

    public MainTower getMainTower() {
        return mainTower;
    }

    @Override
    public void update() {
        if (engine == null || engine.isPaused()) return;
        objects.removeIf((t -> t.isDead()));
        super.update();
    }

    @Override
    public void draw(Window window, Renderer renderer) {

    }
}
