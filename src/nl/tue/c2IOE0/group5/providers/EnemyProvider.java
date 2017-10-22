package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.enemies.TestEnemy;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Simulator;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import org.joml.Vector2i;

import java.util.List;

public class EnemyProvider extends ObjectProvider<Enemy> {

    private Timer timer;
    private GridProvider gridProvider;

    @Override
    public void init(Simulator engine) {
        super.init(engine);
        timer = engine.getTimer();
        gridProvider = engine.getProvider(GridProvider.class);
    }

    @Override
    public void renderInit(Engine engine) {
        Mesh m = engine.getRenderer().linkMesh("/cube.obj");
        m.setMaterial(new Material("/square.png"));
    }

    public List<Enemy> getEnemies() {
        return this.objects;
    }

    public int countEnemies() {
        return objects.size();
    }

    public void putEnemy(Vector2i initialPosition, List<Vector2i> targets) {
        objects.add(new TestEnemy(
                timer,
                gridProvider,
                initialPosition,
                targets, 20
        ).init(getRenderer()));
    }

    @Override
    public void update() {
        objects.removeIf(Enemy::isDead);
        super.update();

    }

    @Override
    public void draw(Window window, Renderer renderer) {
        objects.forEach(enemy -> enemy.draw(window, renderer));
    }
}
