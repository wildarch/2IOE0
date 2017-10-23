package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.enemies.Bruiser;
import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.enemies.EnemyType;
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

    private Timer loopTimer;
    private GridProvider gridProvider;
    private Timer renderTimer;

    @Override
    public void init(Simulator engine) {
        super.init(engine);
        loopTimer = engine.getGameloopTimer();
        gridProvider = engine.getProvider(GridProvider.class);
    }

    @Override
    public void renderInit(Engine engine) {
        Mesh m = engine.getRenderer().linkMesh("/cube.obj");
        m.setMaterial(new Material("/square.png"));
        renderTimer = engine.getRenderLoopTimer();
    }

    public List<Enemy> getEnemies() {
        return this.objects;
    }

    public int countEnemies() {
        return objects.size();
    }

    public Enemy putEnemy(EnemyType enemyType, Vector2i initialPosition, List<Vector2i> targets){
        Enemy enemy;

        switch (enemyType){
            default:
                enemy = new Bruiser(loopTimer, renderTimer, gridProvider, initialPosition, targets, 100);
                break;
        }

        System.out.println("placing " + enemyType + " at " + initialPosition.toString());

        objects.add(enemy);

        return enemy;
    }

    public void putEnemy(Vector2i initialPosition, List<Vector2i> targets) {
        objects.add(new TestEnemy(
                loopTimer,
                renderTimer,
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

    }
}
