package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.BulletProvider;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.providers.GridProvider;

/**
 * @Author Tom Peters
 */
public class WallTower extends AbstractTower {

    private static final int RANGE = 0;
    private static final int MAX_LEVEL = 1;
    private static final int MAX_HEALTH = 50;

    private Renderer renderer;
    private InstancedMesh iMesh;

    public WallTower(EnemyProvider enemyProvider, BulletProvider bulletProvider, GridProvider gridProvider, Timer timer) {
        super(RANGE, MAX_LEVEL, MAX_HEALTH, -1, 0, 0, 2.5f, enemyProvider, bulletProvider, gridProvider, timer);
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(1f);
        Mesh base = renderer.linkMesh("/models/towers/cannontower/BASE.obj");
        Mesh cannon = renderer.linkMesh("/models/towers/cannontower/CANNON.obj");
        base.setMaterial(new Material("/models/towers/cannontower/Base.png"));
        cannon.setMaterial(new Material("/models/towers/cannontower/Cannon.png"));

        Mesh mesh = renderer.linkMesh("/models/towers/walltower/walltower.obj");
        mesh.setMaterial(new Material("/models/towers/walltower/walltower.png"));

        iMesh = renderer.linkMesh(mesh, () -> setModelView(renderer));
        this.renderer = renderer;
    }

    @Override
    protected void onDie() {
        //renderer.unlinkMesh(mesh, render, shadowRender);
        renderer.unlinkMesh(iMesh);
    }
}