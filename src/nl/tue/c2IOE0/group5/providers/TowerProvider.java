package nl.tue.c2IOE0.group5.providers;

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

    @Override
    public void init(Simulator engine) {
        super.init(engine);
        gridProvider = engine.getProvider(GridProvider.class);
        enemyProvider = engine.getProvider(EnemyProvider.class);
        bulletProvider = engine.getProvider(BulletProvider.class);
        loopTimer = engine.getGameloopTimer();
        putMainTower();
        try {
            buildTower(2, 2, WallTower.class);
            buildTower(2, 3, WallTower.class);
            buildTower(8, 8, RocketTower.class);
            buildTower(9, 8, CannonTower.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public void renderInit(Engine engine) {
        Mesh m = engine.getRenderer().linkMesh("/tower.obj");
        m.setMaterial(new Material("/tower.png"));
        renderTimer = engine.getRenderLoopTimer();
    }

    /**
     * Create a tower according to a tower type at a certain location
     * @Returns true if build succesful, false if there already is a tower
     * @Throws Many exceptions when passing class type as argument fails: So an incorrect type was passed (not a tower)
     */
    public boolean buildTower(int x, int y, Class<? extends AbstractTower> towertype) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (gridProvider.getCell(x, y).getTower() != null) {
            //not null: tower exists here already
            return false;
        }
        //create the tower
        Constructor<?> constructor = towertype.getDeclaredConstructor(TowerProvider.class);
        AbstractTower tower = (AbstractTower) constructor.newInstance(this);
        tower.init(getRenderer());
        //place it
        gridProvider.placeTower(x, y, tower);
        objects.add(tower);
        //placing tower succesfull!
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

    private void putMainTower() {
        int x = GridProvider.SIZE / 2;
        mainTower = new MainTower(this).init(getRenderer());
        gridProvider.placeTower(x, x, mainTower);
        objects.add(mainTower);
    }

    public MainTower getMainTower() {
        return mainTower;
    }

    @Override
    public void update() {
        objects.removeIf((t -> t.isDead()));
        super.update();
    }

    @Override
    public void draw(Window window, Renderer renderer) {

    }
}
