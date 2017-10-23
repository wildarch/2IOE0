package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.Animatable;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.util.LinearlyUpdatable;
import nl.tue.c2IOE0.group5.util.SmoothUpdatable;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

import static java.lang.Math.sin;

/**
 * @author Geert van Ieperen
 *         created on 13-10-2017.
 */
public class WalkerEnemy extends Enemy implements Animatable {

    private static final float SPEED = 0.3f;
    private static final long ATTACKSPEED = 5;
    private static final int MAX_HEALTH = 20;

    private InstancedMesh body;
    private InstancedMesh head;
    private InstancedMesh leftArm;
    private InstancedMesh rightArm;
    private InstancedMesh leftLeg;
    private InstancedMesh rightLeg;

    private SmoothUpdatable headOffset;
    private SmoothUpdatable leftArmOffset;
    private SmoothUpdatable rightArmOffset;

    public WalkerEnemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider,
                       Vector2i initialPosition, List<Vector2i> targetPositions, QLearner qlearner) {
        super(loopTimer, renderTimer, gridProvider, initialPosition, targetPositions, MAX_HEALTH, SPEED, ATTACKSPEED, qlearner);
        setScale(0.5f);
    }

    /**
     * returns the offset of the arm on the given timestamp
     * @param loopTime time since the last animation loop
     * @return offset based on current animation
     */
    private float armOffset(float loopTime){
        if (!attacking) {
            return (float) sin(loopTime);
        }
        return 0;
    }

    /**
     * returns the height offset of the head on the given timestamp
     * @param loopTime time since the last animation loop
     * @return offset based on current animation
     */
    private float headOffset(float loopTime){
        return attacking ? 0 : (float) (0.1 * sin(2 * loopTime));
    }

    @Override
    public void updateAnimation(float animTime, float deltaTime) {
        headOffset.updateFluent(headOffset(animTime), deltaTime);
        leftArmOffset.updateFluent(armOffset(animTime), deltaTime);
        rightArmOffset.updateFluent(-armOffset(animTime), deltaTime);
    }

    @Override
    public EnemyType getType() {
        return EnemyType.WALKER;
    }

    @Override
    public void update() {

    }

    @Override
    public boolean mustBeRemoved() {
        return isDead();
    }

    @Override
    protected void onDie() {
        // unnecessary
        renderer.unlinkMesh(body);
        renderer.unlinkMesh(head);
        renderer.unlinkMesh(leftArm);
        renderer.unlinkMesh(rightArm);
        renderer.unlinkMesh(leftLeg);
        renderer.unlinkMesh(rightLeg);
    }

    @Override
    public void renderInit(Renderer renderer) {
        headOffset = new LinearlyUpdatable(headOffset(0), 0.1f);
        leftArmOffset = new LinearlyUpdatable(armOffset(0), 0.1f);
        rightArmOffset = new LinearlyUpdatable(-armOffset(0), 0.1f);

        Material SILVER = new Material("/silver.png");

        head = renderer.linkMesh("/models/enemies/walkerEnemy/HEAD.obj", SILVER, () -> {
            final Vector3f finalHeadOffset = new Vector3f(0f, 0.438f + this.headOffset.current(), 0f).mul(getScale());
            setModelView(renderer, finalHeadOffset);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

        body = renderer.linkMesh("/models/enemies/walkerEnemy/BODY.obj", SILVER, () -> {
            final Vector3f finalBodyOffset = new Vector3f(0f, 0.272f, 0f).mul(getScale());
            setModelView(renderer, finalBodyOffset);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

        leftArm = renderer.linkMesh("/models/enemies/walkerEnemy/FRONT_LEFT.obj", SILVER, () -> {
            final Vector3f finalLeftArmOffset = new Vector3f(-0.167f + this.leftArmOffset.current(), 0.245f, 0.131f).mul(getScale());
            setModelView(renderer, finalLeftArmOffset);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

        rightArm = renderer.linkMesh("/models/enemies/walkerEnemy/FRONT_RIGHT.obj", SILVER, () -> {
            final Vector3f finalRightArmOffset = new Vector3f(-0.167f + rightArmOffset.current(), 0.245f, 0.131f).mul(getScale());
            setModelView(renderer, finalRightArmOffset);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

        leftLeg = renderer.linkMesh("/models/enemies/walkerEnemy/BACK_LEFT.obj", SILVER, () -> {
            final Vector3f finalLeftLegOffset = new Vector3f(0.118f + rightArmOffset.current(), 0.245f, 0.131f).mul(getScale());
            setModelView(renderer, finalLeftLegOffset);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

        rightLeg = renderer.linkMesh("/models/enemies/walkerEnemy/BACK_RIGHT.obj", SILVER, () -> {
            final Vector3f finalRightLegOffset = new Vector3f(0.118f + leftArmOffset.current(), 0.245f, 0.131f).mul(getScale());
            setModelView(renderer, finalRightLegOffset);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

    }

}
