package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.Animatable;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.AnimationProvider;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.util.LinearlyUpdatable;
import nl.tue.c2IOE0.group5.util.SmoothUpdatable;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

import static java.lang.Math.sin;
import static nl.tue.c2IOE0.group5.util.Angle.rotateVector;

/**
 * @author Geert van Ieperen
 *         created on 23-10-2017.
 * the fast unit with meelee attacks.
 */
public class DrillEnemy extends Enemy implements Animatable {

    private final static int MAXHEALTH = 50;
    private final static float SPEED = 0.7f;
    private final static int ATTACKSPEED = 100;
    private final static int DAMAGE = 3;

    private InstancedMesh body;
    private InstancedMesh drill;
    private InstancedMesh wheel;

    private float drillRotation = 0f;
    private float wheelRoatation = 0f;

    private SmoothUpdatable drillOffset;

    private AnimationProvider animationProvider;

    public DrillEnemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider, Vector2i initialPosition,
                      List<Vector2i> targetPositions, QLearner qlearner, AnimationProvider animationProvider) {
        super(loopTimer, renderTimer, gridProvider, initialPosition, targetPositions, MAXHEALTH, DAMAGE, SPEED, ATTACKSPEED, qlearner);
        setScale(0.03f);

        this.animationProvider = animationProvider;
    }

    @Override
    public void updateAnimation(float animTime, float deltaTime) {
        if (attacking){
            drillOffset.update(drillOffset(animTime));
        }
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

    /**
     * returns the offset of the arm on the given timestamp
     * @param loopTime time since the last animation loop
     * @return offset based on current animation
     */
    private float drillOffset(float loopTime){
        return attacking ? (float) (0.2 * sin(3141.5/ATTACKSPEED * loopTime)) : 0;
    }

    @Override
    public EnemyType getType() {
        return EnemyType.DRILL;
    }

    @Override
    public void renderInit(Renderer renderer) {
        animationProvider.add(this);
        rotationOffset = -90f;

        drillOffset = new LinearlyUpdatable(0f, 100);

        this.renderer = renderer;
        Material darkMatter = new Material("/silver.png");

        body = renderer.linkMesh("/models/enemies/drillEnemy/BODY.obj", darkMatter, () -> {
            rotateAndSet(renderer, new Vector3f());
        });

        drill = renderer.linkMesh("/models/enemies/drillEnemy/DRILL.obj", darkMatter, () -> {
            final Vector3f offset = new Vector3f(2.713f + this.drillOffset.current(), 1.674f, 0f);
            rotateAndSet(renderer, offset);
        });

        wheel = renderer.linkMesh("/models/enemies/drillEnemy/WHEEL.obj", darkMatter, () -> {
            final Vector3f offset = new Vector3f(2.08f, 0.628f, 0f);
            rotateAndSet(renderer, offset);
        });
    }

    private void rotateAndSet(Renderer renderer, Vector3f offset) {
        final Vector3f wheelOffset = offset.mul(getScale());
        final Vector3f displacement = rotateVector(wheelOffset, new Vector3f(0, -1, 0), getRotation().y + 90);
        if(!attacking) displacement.add(interpolator.getOffset(renderTimer.getTime() - loopTimer.getTime()));
        setModelView(renderer, displacement, new Vector3f(0, 0, 0));
    }
}
