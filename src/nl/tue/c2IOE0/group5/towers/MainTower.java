package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.providers.BulletProvider;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;

public class MainTower extends AbstractTower {

    private static final int RANGE = 2;
    private static final int MAX_LEVEL = 1;
    private static final int MAX_HEALTH = 100;

    private Renderer renderer;
    private InstancedMesh iMesh;

    public MainTower(EnemyProvider enemyProvider, BulletProvider bulletProvider, Timer timer, Renderer renderer) {
        super(RANGE, MAX_LEVEL, MAX_HEALTH, 500, 0.05f, 1000, enemyProvider, bulletProvider, timer, renderer);
    }

    @Override
    public MainTower init(Renderer renderer) {
        setScale(40f);

        iMesh = renderer.linkMesh("/tower.obj", () -> setModelView(renderer));

        this.renderer = renderer;
        return this;
    }

    @Override
    protected void onDie() {
        //renderer.unlinkMesh(mesh, render, shadowRender);
        renderer.unlinkMesh(iMesh);
    }

    @Override
    public TowerType getType() {
        return TowerType.CASTLE;
    }

    /**
     * Returns the level of the tower
     *
     * @return double x where 0 < x <= 1.0
     */
    @Override
    public double getLevel() {
        return MAX_LEVEL;
    }
}
