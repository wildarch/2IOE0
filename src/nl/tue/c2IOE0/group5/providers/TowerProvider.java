package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Simulator;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.CannonTower;
import nl.tue.c2IOE0.group5.towers.MainTower;
import nl.tue.c2IOE0.group5.towers.WallTower;
import nl.tue.c2IOE0.group5.towers.RocketTower;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class TowerProvider extends ObjectProvider<AbstractTower> {



    public GridProvider gridProvider;
    public EnemyProvider enemyProvider;
    public BulletProvider bulletProvider;
    private MainTower mainTower;
    public Timer loopTimer;
    public Timer renderTimer;

    private List<Provider> providers;


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
       // buildCannonTower(2, 2);
       // buildWallTower(3, 4);
       // buildRocketTower(4, 5);

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
    public boolean buildTower(int x, int y, Class<?> towertype) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
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
     * If there is already a tower at this spot, it just places it without warning
     */
    private void buildCannonTower(int x, int y) {
        CannonTower ct = new CannonTower(this).init(getRenderer());
        gridProvider.placeTower(x, y, ct);
        objects.add(ct);
    }

    /**
     * If there is already a tower at this spot, it just places it without warning
     */
    private void buildWallTower(int x, int y) {
        //test code for wall connections!
        WallTower wt = new WallTower(this).init(getRenderer());
        WallTower wt2 = new WallTower(this).init(getRenderer());
        WallTower wt3 = new WallTower(this).init(getRenderer());
        WallTower wt4 = new WallTower(this).init(getRenderer());
        gridProvider.placeTower(x, y, wt);
        gridProvider.placeTower(x+1, y, wt2);
        gridProvider.placeTower(x+1, y-1, wt3);
        gridProvider.placeTower(x, y-1, wt4);
        objects.add(wt);
        objects.add(wt2);
        objects.add(wt3);
        objects.add(wt4);

        wt4.takeDamage(500);
    }

    /**
     * If there is already a tower at this spot, it just places it without warning
     */
    private void buildRocketTower(int x, int y) {
        RocketTower rt = new RocketTower(this).init(getRenderer());
        gridProvider.placeTower(x, y, rt);
        objects.add(rt);
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
