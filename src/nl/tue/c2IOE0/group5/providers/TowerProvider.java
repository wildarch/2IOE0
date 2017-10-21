package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Simulator;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.CannonTower;
import nl.tue.c2IOE0.group5.towers.MainTower;
import nl.tue.c2IOE0.group5.towers.WallTower;
import nl.tue.c2IOE0.group5.towers.RocketTower;

public class TowerProvider extends ObjectProvider<AbstractTower> {

    GridProvider gridProvider;
    EnemyProvider enemyProvider;
    BulletProvider bulletProvider;
    private MainTower mainTower;
    private Timer loopTimer;
    private Timer renderTimer;

    @Override
    public void init(Simulator engine) {
        super.init(engine);
        gridProvider = engine.getProvider(GridProvider.class);
        enemyProvider = engine.getProvider(EnemyProvider.class);
        bulletProvider = engine.getProvider(BulletProvider.class);
        loopTimer = engine.getGameloopTimer();
        putMainTower();
        buildCannonTower(2, 2);
        buildWallTower(3, 4);
        buildRocketTower(4, 5);
    }

    @Override
    public void renderInit(Engine engine) {
        Mesh m = engine.getRenderer().linkMesh("/tower.obj");
        m.setMaterial(new Material("/tower.png"));
        renderTimer = engine.getRenderLoopTimer();
    }

    /**
     * If there is already a tower at this spot, it just places it without warning
     */
    private void buildCannonTower(int x, int y) {
        CannonTower ct = new CannonTower(enemyProvider, bulletProvider, gridProvider, loopTimer, renderTimer).init(getRenderer());
        gridProvider.placeTower(x, y, ct);
        objects.add(ct);
    }

    /**
     * If there is already a tower at this spot, it just places it without warning
     */
    private void buildWallTower(int x, int y) {
        WallTower wt = new WallTower(enemyProvider, bulletProvider, gridProvider, loopTimer, renderTimer).init(getRenderer());
        gridProvider.placeTower(x, y, wt);
        objects.add(wt);
    }

    /**
     * If there is already a tower at this spot, it just places it without warning
     */
    private void buildRocketTower(int x, int y) {
        RocketTower rt = new RocketTower(enemyProvider, bulletProvider, gridProvider, loopTimer, renderTimer).init(getRenderer());
        gridProvider.placeTower(x, y, rt);
        objects.add(rt);
    }

    private void putMainTower() {
        int x = GridProvider.SIZE / 2;
        mainTower = new MainTower(enemyProvider, bulletProvider, gridProvider, loopTimer, renderTimer).init(getRenderer());
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
