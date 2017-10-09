package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.enemies.TestEnemy;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.OBJLoader;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Arrays;

public class EnemyProvider extends ObjectProvider<Enemy> {

    private Timer loopTimer;
    private Mesh mesh;

    @Override
    public void init(Engine engine) {
        loopTimer = engine.getGameloopTimer();
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

    public void putEnemy(Vector3f initialPosition) {
        objects.add(new TestEnemy(
                mesh,
                loopTimer,
                initialPosition,
                Arrays.asList(new Vector3f(7f, 1f, 7f))
        ));
    }
}
