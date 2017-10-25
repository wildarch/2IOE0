package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.controllers.PlayerController;
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
    private PlayerController playerController;

    @Override
    public void init(Simulator engine) {
        super.init(engine);
        loopTimer = engine.getGameloopTimer();
        gridProvider = engine.getProvider(GridProvider.class);
        if(engine instanceof Engine) {
            Engine e = (Engine) engine;
            try {
                playerController = e.getController(PlayerController.class);
            } catch (IllegalArgumentException err) {
                System.err.println("No PlayerController found");
            }
        }
    }

    @Override
    public void renderInit(Engine engine) {
        this.engine = engine;
        animationProvider = engine.getProvider(AnimationProvider.class);
        Mesh m = engine.getRenderer().linkMesh("/testobjects/cube.obj");
        m.setMaterial(new Material("/general/square.png"));
        renderTimer = engine.getRenderLoopTimer();
    }

    public List<Enemy> getEnemies() {
        return this.objects;
    }

    public int countEnemies() {
        return objects.size();
    }

    public void putEnemy(Vector2i initialPosition, List<Vector2i> targets, QLearner qlearner) {
        putEnemy(EnemyType.values()[new Random().nextInt(EnemyType.getSize())], initialPosition, targets, qlearner);
    }

    public void putEnemy(EnemyType type, Vector2i initialPosition, List<Vector2i> targets, QLearner qlearner) {
        final Enemy newEnemy;
        switch (type) {
            case DROID:
                newEnemy = new BasicEnemy(
                        loopTimer,
                        renderTimer,
                        gridProvider,
                        initialPosition,
                        targets, qlearner,
                        playerController
                );
                break;
            case WALKER:
                newEnemy = new WalkerEnemy(
                        loopTimer,
                        renderTimer,
                        gridProvider,
                        initialPosition,
                        targets, qlearner,
                        animationProvider,
                        playerController
                );
                break;
            case DRILL:
                newEnemy = new DrillEnemy(
                        loopTimer,
                        renderTimer,
                        gridProvider,
                        initialPosition,
                        targets, qlearner,
                        animationProvider,
                        playerController
                );
                break;
            default:
                newEnemy = new Enemy(loopTimer, renderTimer, gridProvider,
                        initialPosition, targets, 10, 1, 1, 300, qlearner, playerController)
                {
                    public EnemyType getType(){ return EnemyType.DROID; }
                    protected void onDie(){ renderer.unlinkMesh(iMeshBody); }
                };
        }
        objects.add(newEnemy.init(getRenderer()));
    }

    /**
     * Return true if there is an enemy on the specified cell, false otherwise
     * @param c the cell to look for
     */
    public boolean enemyOnCell(Cell c) {
        for (Enemy e : objects) {
            if (e.getCurrentCell().equals(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update() {
        if (engine != null && engine.isPaused()) return;
        objects.removeIf(Enemy::isDead);
        super.update();

    }

    @Override
    public void draw(Window window, Renderer renderer) {
        objects.forEach(enemy -> enemy.draw(window, renderer));
    }
}
