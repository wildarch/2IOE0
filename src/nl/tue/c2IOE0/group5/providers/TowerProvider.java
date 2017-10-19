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
import nl.tue.c2IOE0.group5.towers.RocketTower;

public class TowerProvider extends ObjectProvider<AbstractTower> {

    GridProvider gridProvider;
    EnemyProvider enemyProvider;
    BulletProvider bulletProvider;
    private MainTower mainTower;
    private Timer loopTimer;

    @Override
    public void init(Simulator engine) {
        super.init(engine);
        gridProvider = engine.getProvider(GridProvider.class);
        enemyProvider = engine.getProvider(EnemyProvider.class);
        bulletProvider = engine.getProvider(BulletProvider.class);
        loopTimer = engine.getGameloopTimer();
        putMainTower();
        putCannonTower();
        putRocketTower();
    }

    @Override
    public void renderInit(Engine engine) {
        Mesh m = engine.getRenderer().linkMesh("/tower.obj");
        m.setMaterial(new Material("/tower.png"));
    }

    private void putCannonTower() {
        CannonTower ct = new CannonTower(enemyProvider, bulletProvider, loopTimer).init(getRenderer());
        gridProvider.getCell(2, 2).placeTower(ct);
        objects.add(ct);
    }


    private void putRocketTower() {
        RocketTower rt = new RocketTower(enemyProvider, bulletProvider, loopTimer).init(getRenderer());
        gridProvider.getCell(2, 4).placeTower(rt);
        objects.add(rt);
    }
    private void putMainTower() {
        int x = GridProvider.SIZE / 2;
        Cell baseCell = gridProvider.getCell(x, x);
        mainTower = new MainTower(enemyProvider, bulletProvider, loopTimer).init(getRenderer());
        baseCell.placeTower(mainTower);
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
