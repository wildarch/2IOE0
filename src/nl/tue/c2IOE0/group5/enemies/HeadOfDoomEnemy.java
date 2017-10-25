package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.controllers.PlayerController;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

/**
 * @author Geert van Ieperen
 *         created on 25-10-2017.
 */
public class HeadOfDoomEnemy extends Enemy {

    private final static int MAXHEALTH = 1000;
    private final static float SPEED = 0.1f;
    private final static int ATTACKSPEED = 1000;
    private final static int DAMAGE = 50;

    public HeadOfDoomEnemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider, Vector2i initialPosition, List<Vector2i> targetPositions, QLearner qlearner, PlayerController playerController) {
        super(loopTimer, renderTimer, gridProvider, initialPosition, targetPositions,
                MAXHEALTH, DAMAGE, SPEED, ATTACKSPEED, qlearner, playerController, 0);
        move(0, -1, 0);

        System.out.println("HeadOfDoomEnemy: spawning at " + initialPosition);
    }

    @Override
    public EnemyType getType() {
        return EnemyType.BOSS;
    }

    @Override
    protected void onDie() {
        renderer.unlinkMesh(iMeshBody);
    }

    /**
     * A standard cube. To be overridden by the subclasses
     * @param renderer An instance of the renderer that will draw this object.
     */
    @Override
    public void renderInit(Renderer renderer) {
        rotationOffset = 180f;
        drawOffset = new Vector3f(0, 5, -1);
        setScale(5f);

        Material SILVER = new Material("/general/white.png");

        iMeshBody = renderer.linkMesh("/models/enemies/walkerEnemy/HEAD.obj", SILVER, () -> {
            setModelView(renderer, drawOffset);
        });
        this.renderer = renderer;
    }

}
