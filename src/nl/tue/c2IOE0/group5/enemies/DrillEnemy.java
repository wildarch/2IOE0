package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.Animatable;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

import static nl.tue.c2IOE0.group5.engine.objects.Animatable.AnimationLoop.*;

/**
 * @author Geert van Ieperen
 *         created on 23-10-2017.
 * the fast unit with meelee attacks.
 */
public class DrillEnemy extends Enemy implements Animatable {

    private final static int MAXHEALTH = 100;
    private final static float SPEED = 0.5f;
    private final static int ATTACKSPEED = 400;

    private InstancedMesh body;
    private InstancedMesh drill;
    private InstancedMesh wheel;

    private AnimationLoop currentAnim = DEFAULT;
    private float drillRotation = 0f;
    private float wheelRoatation = 0f;

    public DrillEnemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider, Vector2i initialPosition, List<Vector2i> targetPositions, QLearner qlearner) {
        super(loopTimer, renderTimer, gridProvider, initialPosition, targetPositions, MAXHEALTH, SPEED, ATTACKSPEED, qlearner);
        setScale(0.03f);
    }

    @Override
    public void updateAnimation(float animTime, float deltaTime) {
        switch (currentAnim) {
            case WALK:
                wheelRoatation += 0.1f;
                break;
            case SHOOT:
                drillRotation += 0.05f;
                break;
        }
    }

    @Override
    public void setCurrentAnim(AnimationLoop newAnim) {
        currentAnim = newAnim;
    }

    @Override
    public boolean mustBeRemoved() {
        return isDead();
    }

    @Override
    protected void onDie() {
        renderer.unlinkMesh(body);
        renderer.unlinkMesh(wheel);
        renderer.unlinkMesh(drill);
    }

    @Override
    public void renderInit(Renderer renderer) {
        this.renderer = renderer;
        Material darkMatter = new Material();
        final Vector3f drillOffset = new Vector3f(0f, 1.674f, 2.713f);
        final Vector3f wheelOffset = new Vector3f(0f, 0.628f, 2.08f);

        body = renderer.linkMesh("/models/enemies/drillEnemy/BODY.obj", darkMatter, () -> {
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
            setModelView(renderer);
        });
        drill = renderer.linkMesh("/models/enemies/drillEnemy/DRILL.obj", Material.SILVER, () -> {
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
            setModelView(renderer, drillOffset.mul(getScale()), new Vector3f());
        });
        wheel = renderer.linkMesh("/models/enemies/drillEnemy/WHEEL.obj", darkMatter, () -> {
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
            setModelView(renderer, wheelOffset.mul(getScale()), new Vector3f());
        });
    }
}
