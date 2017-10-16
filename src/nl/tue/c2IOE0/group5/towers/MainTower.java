package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.OBJLoader;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.BulletProvider;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import sun.applet.Main;

import java.io.IOException;

public class MainTower extends AbstractTower {

    private static final int RANGE = 2;
    private static final int MAX_LEVEL = 1;
    private static final int MAX_HEALTH = 100;

    public MainTower(EnemyProvider enemyProvider, BulletProvider bulletProvider, Timer timer) {
        super(RANGE, MAX_LEVEL, MAX_HEALTH, enemyProvider, bulletProvider, timer);
    }

    @Override
    public MainTower init(Renderer renderer) {
        setScale(40f);
        renderer.linkMesh("/tower.obj", (mesh -> {
            setModelView(renderer);
            mesh.draw();
        }), (mesh -> {
            setModelLightView(renderer);
            mesh.draw();
        }));
        return this;
    }
}
