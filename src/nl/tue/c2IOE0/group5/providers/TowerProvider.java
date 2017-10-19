package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.MainTower;

public class TowerProvider extends ObjectProvider<AbstractTower> {

    GridProvider gridProvider;
    EnemyProvider enemyProvider;
    BulletProvider bulletProvider;
    private MainTower mainTower;
    private Timer loopTimer;
    private Renderer renderer;

    @Override
    public void init(Engine engine) {
        gridProvider = engine.getProvider(GridProvider.class);
        enemyProvider = engine.getProvider(EnemyProvider.class);
        bulletProvider = engine.getProvider(BulletProvider.class);
        loopTimer = engine.getRenderLoopTimer();
        renderer = engine.getRenderer();
        putMainTower();
        Mesh m = engine.getRenderer().linkMesh("/tower.obj");
        m.setMaterial(new Material("/tower.png"));
    }

    private void putMainTower() {
        int x = GridProvider.SIZE / 2;
        mainTower = new MainTower(enemyProvider, bulletProvider, loopTimer).init(renderer);
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
