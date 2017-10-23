package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.enemies.*;
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
import java.util.Random;

public class EnemyProvider extends ObjectProvider<Enemy> {

    private Timer loopTimer;
    private GridProvider gridProvider;
    private Timer renderTimer;
    private AnimationProvider animationProvider;

    private Engine engine;

    @Override
    public void init(Simulator engine) {
        super.init(engine);
        loopTimer = engine.getGameloopTimer();
        gridProvider = engine.getProvider(GridProvider.class);
        animationProvider = engine.getProvider(AnimationProvider.class);
    }

    @Override
    public void renderInit(Engine engine) {
        this.engine = engine;
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

    public void putEnemy(Vector2i initialPosition, List<Vector2i> targets, QLearner qlearner) {

        final Enemy newEnemy;
        switch (new Random().nextInt(3)) {
            case 0:
                newEnemy = new BasicEnemy(
                        loopTimer,
                        renderTimer,
                        gridProvider,
                        initialPosition,
                        targets, qlearner
                );
                break;
            case 1:
                newEnemy = new WalkerEnemy(
                        loopTimer,
                        renderTimer,
                        gridProvider,
                        initialPosition,
                        targets, qlearner,
                        animationProvider);
                break;
            case 2:
                newEnemy = new DrillEnemy(
                        loopTimer,
                        renderTimer,
                        gridProvider,
                        initialPosition,
                        targets, qlearner,
                        animationProvider);
                break;
            default: newEnemy = new TestEnemy(
                        loopTimer,
                        renderTimer,
                        gridProvider,
                        initialPosition,
                        targets, 10, qlearner
                );
        }
//        System.out.println("EnemyProvider.putEnemy(): spawn " + newEnemy.getClass().getSimpleName() +
//                " at (" + initialPosition.x + ", " + initialPosition.y + ")");

        objects.add(newEnemy.init(getRenderer()));
    }

    @Override
    public void update() {
        if (engine == null || engine.isPaused()) return;
        objects.removeIf(Enemy::isDead);
        super.update();

    }

    @Override
    public void draw(Window window, Renderer renderer) {

    }
}
