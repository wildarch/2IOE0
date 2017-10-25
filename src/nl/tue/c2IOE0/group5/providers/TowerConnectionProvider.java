package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.towers.Bullet;
import nl.tue.c2IOE0.group5.towers.TowerConnection;

/**
 * @Author Yoeri Poels
 */


public class TowerConnectionProvider extends ObjectProvider<TowerConnection> {

    public void addTowerConnection(TowerConnection tc) {
        this.objects.add(tc);
    }

    public void deleteTowerConnection(TowerConnection tc) {
        this.objects.remove(tc);
    }

    private Engine engine;

    @Override
    public void renderInit(Engine engine) {
        this.engine = engine;
        engine.getRenderer().linkMesh("/models/towers/walltower/wall.obj")
                .setMaterial(new Material("/models/towers/walltower/wall.png"));

    }

    @Override
    public void update() {
        if (engine != null && engine.isPaused()) return;
        super.update();
    }

    @Override
    public void draw(Window window, Renderer renderer) {
    }
}
