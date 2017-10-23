package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.providers.BulletProvider;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.providers.TowerProvider;

import java.util.function.Consumer;

public class MainTower extends AbstractTower {

    private static final int RANGE = 2;
    private static final int MAX_LEVEL = 1;
    private static final int MAX_HEALTH = 100;
    private static final int PRICE = 0;

    private Renderer renderer;
    private InstancedMesh iMesh;

    public MainTower(TowerProvider towerProvider) {
        super(RANGE, MAX_LEVEL, MAX_HEALTH, 500, 3f, 1000, 2.5f, 1f, towerProvider);
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(1f);

        iMesh = renderer.linkMesh("/models/towers/mainbase/mainbase.obj", () -> setModelView(renderer));

        this.renderer = renderer;
    }

    @Override
    protected void onDie() {
        //renderer.unlinkMesh(mesh, render, shadowRender);
        renderer.unlinkMesh(iMesh);
    }
}
