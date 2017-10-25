package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.controllers.PlayerController;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.joml.Vector2i;

import java.util.List;

public class BasicEnemy extends Enemy {

    private static final float SPEED = 0.4f;
    private static final long ATTACKSPEED = 2000;
    private static final int MAX_HEALTH = 30;
    private final static int DAMAGE = 10;

    public BasicEnemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider,
                      Vector2i initialPosition, List<Vector2i> targetPositions, QLearner qlearner, PlayerController playerController) {
        super(loopTimer, renderTimer, gridProvider, initialPosition, targetPositions, MAX_HEALTH, DAMAGE, SPEED, ATTACKSPEED, qlearner, playerController);
    }

    @Override
    protected void onDie() {
        renderer.unlinkMesh(iMeshBody);
    }

    @Override
    public EnemyType getType() {
        return EnemyType.DROID;
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(0.05f);
        Mesh body = renderer.linkMesh("/models/enemies/basicEnemy/body.obj");
        body.setMaterial(new Material("/general/orange.png"));
        iMeshBody = renderer.linkMesh(body, () -> {
            setModelView(renderer, drawOffset);
        });
        this.renderer = renderer;
    }
}
