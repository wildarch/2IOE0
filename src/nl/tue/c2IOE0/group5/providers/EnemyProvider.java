package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.enemies.TestEnemy;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.OBJLoader;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import org.joml.Vector2i;

import java.io.IOException;
import java.util.List;

public class EnemyProvider extends ObjectProvider<Enemy> {

    private Timer loopTimer;
    private GridProvider gridProvider;
    private Mesh mesh;

    @Override
    public void init(Engine engine) {
        loopTimer = engine.getGameloopTimer();
        gridProvider = engine.getProvider(GridProvider.class);
        try {
            mesh = OBJLoader.loadMesh("/cube.obj");
            mesh.setMaterial(new Material(("/tower.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        objects.removeIf((enemy -> enemy.isDead()));
        super.update();

    }

    public int countEnemies() {
        return objects.size();
    }

    public void putEnemy(Vector2i initialPosition, List<Vector2i> targets) {
        objects.add(new TestEnemy(
                mesh,
                loopTimer,
                gridProvider,
                initialPosition,
                targets
        ));
    }
}
