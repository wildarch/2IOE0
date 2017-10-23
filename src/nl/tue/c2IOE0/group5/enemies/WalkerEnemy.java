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
 *         created on 13-10-2017.
 */
public class WalkerEnemy extends Enemy implements Animatable {

    private final static int MAXHEALTH = 100;
    private final static float SPEED = 0.1f;
    private final static int ATTACKSPEED = 400;

    private InstancedMesh body;
    private InstancedMesh head;
    private InstancedMesh leftArm;
    private InstancedMesh rightArm;
    private InstancedMesh leftLeg;
    private InstancedMesh rightLeg;

    private SmoothUpdatable headOffset;
    private SmoothUpdatable leftArmOffset;
    private SmoothUpdatable rightArmOffset;

    public WalkerEnemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider, Vector2i initialPosition,
                       List<Vector2i> targetPositions, QLearner qlearner, AnimationProvider animationProvider) {
        super(loopTimer, renderTimer, gridProvider, initialPosition, targetPositions, MAXHEALTH, SPEED, ATTACKSPEED, qlearner);
        setScale(0.5f);
//        move(0, 1, 0);
        animationProvider.add(this);
    }

    /**
     * returns the offset of the arm on the given timestamp
     * @param loopTime time since the last animation loop
     * @return offset based on current animation
     */
    private float armOffset(float loopTime){
        return !attacking ? (float) (0.001 * sin(0.001 * loopTime)) : 0;
    }

    /**
     * returns the height offset of the head on the given timestamp
     * @param loopTime time since the last animation loop
     * @return offset based on current animation
     */
    private float headOffset(float loopTime){
        return attacking ? 0 : (float) (0.002 * sin(0.002 * loopTime));
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
    public boolean mustBeRemoved() {
        return isDead();
    }

    @Override
    protected void onDie() {
        renderer.unlinkMesh(body);
        renderer.unlinkMesh(head);
        renderer.unlinkMesh(leftArm);
        renderer.unlinkMesh(rightArm);
        renderer.unlinkMesh(leftLeg);
        renderer.unlinkMesh(rightLeg);
    }

    @Override
    public void renderInit(Renderer renderer) {
        this.renderer = renderer;
        headOffset = new LinearlyUpdatable(headOffset(0), 0.1f);
        leftArmOffset = new LinearlyUpdatable(armOffset(0), 0.1f);
        rightArmOffset = new LinearlyUpdatable(-armOffset(0), 0.1f);

        Vector3f yVec = new Vector3f(0, 1, 0);
        Material SILVER = new Material("/silver.png");

        head = renderer.linkMesh("/models/enemies/walkerEnemy/HEAD.obj", SILVER, () -> {
            final Vector3f finalHeadOffset = new Vector3f(0f, 0.438f + this.headOffset.current(), 0f).mul(getScale());
            final Vector3f displacement = rotateVector(finalHeadOffset, yVec, getRotation().y);
            setModelView(renderer, displacement);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

        body = renderer.linkMesh("/models/enemies/walkerEnemy/BODY.obj", SILVER, () -> {
            final Vector3f finalBodyOffset = new Vector3f(0f, 0.272f, 0f).mul(getScale());
            final Vector3f displacement = rotateVector(finalBodyOffset, yVec, getRotation().y);
            setModelView(renderer, displacement);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

        leftArm = renderer.linkMesh("/models/enemies/walkerEnemy/FRONT_LEFT.obj", SILVER, () -> {
            final Vector3f finalLeftArmOffset = new Vector3f(-0.167f + this.leftArmOffset.current(), 0.245f, 0.131f).mul(getScale());
            final Vector3f displacement = rotateVector(finalLeftArmOffset, yVec, getRotation().y);
            setModelView(renderer, displacement);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

        rightArm = renderer.linkMesh("/models/enemies/walkerEnemy/FRONT_RIGHT.obj", SILVER, () -> {
            final Vector3f finalRightArmOffset = new Vector3f(-0.167f + rightArmOffset.current(), 0.245f, -0.131f).mul(getScale());
            final Vector3f displacement = rotateVector(finalRightArmOffset, yVec, getRotation().y);
            setModelView(renderer, displacement);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

        leftLeg = renderer.linkMesh("/models/enemies/walkerEnemy/BACK_LEFT.obj", SILVER, () -> {
            final Vector3f finalLeftLegOffset = new Vector3f(0.118f + rightArmOffset.current(), 0.245f, 0.131f).mul(getScale());
            final Vector3f displacement = rotateVector(finalLeftLegOffset, yVec, getRotation().y);
            setModelView(renderer, displacement);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

        rightLeg = renderer.linkMesh("/models/enemies/walkerEnemy/BACK_RIGHT.obj", SILVER, () -> {
            final Vector3f finalRightLegOffset = new Vector3f(0.118f + leftArmOffset.current(), 0.245f, -0.131f).mul(getScale());
            final Vector3f displacement = rotateVector(finalRightLegOffset, yVec, getRotation().y);
            setModelView(renderer, displacement);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

    }

}
