package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.providers.BulletProvider;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;

import java.util.function.Consumer;

public class MainTower extends AbstractTower {

    private static final int RANGE = 2;
    private static final int MAX_LEVEL = 1;
    private static final int MAX_HEALTH = 100;

    private Renderer renderer;
    private InstancedMesh iMesh;
    private Mesh mesh;
    private Consumer<Mesh> render;
    private Consumer<Mesh> shadowRender;

    public MainTower(EnemyProvider enemyProvider, BulletProvider bulletProvider, Timer timer) {
        super(RANGE, MAX_LEVEL, MAX_HEALTH, enemyProvider, bulletProvider, timer);
    }

    @Override
    public MainTower init(Renderer renderer) {
        setScale(40f);

        iMesh = renderer.linkMesh("/tower.obj", () -> {
            setModelView(renderer);
        });

        /*
        render = mesh -> {
            setModelView(renderer);
            mesh.draw();
        };
        shadowRender = mesh -> {
            setModelLightView(renderer);
            mesh.draw();
        };
        mesh = renderer.linkMesh("/tower.obj", render, shadowRender);
        */

        this.renderer = renderer;
        return this;
    }

    @Override
    protected void onDie() {
        //renderer.unlinkMesh(mesh, render, shadowRender);
        renderer.unlinkMesh(iMesh);
    }
}
