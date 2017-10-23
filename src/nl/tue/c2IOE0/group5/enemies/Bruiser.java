package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.Animatable;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.util.LinearlyUpdatable;
import nl.tue.c2IOE0.group5.util.SmoothUpdatable;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

import static java.lang.Math.sin;
import static nl.tue.c2IOE0.group5.engine.objects.Animatable.AnimationLoop.DEFAULT;
import static nl.tue.c2IOE0.group5.engine.objects.Animatable.AnimationLoop.WALK;

/**
 * @author Geert van Ieperen
 *         created on 13-10-2017.
 */
public class Bruiser extends Enemy implements Animatable {

    private static final float SPEED = 1.5f;
    private static final long ATTACKSPEED = 5;

    private InstancedMesh head;
    private InstancedMesh body;
    private InstancedMesh leftArm;
    private InstancedMesh rightArm;

    private AnimationLoop currentAnim = DEFAULT;

    private SmoothUpdatable headOffset;
    private SmoothUpdatable leftArmOffset;
    private SmoothUpdatable rightArmOffset;

    public Bruiser(Timer loopTimer, Timer renderTimer, GridProvider gridProvider,
                     Vector2i initialPosition, List<Vector2i> targetPositions, int maxHealth) {
        super(loopTimer, renderTimer, gridProvider, initialPosition, targetPositions, maxHealth, SPEED, ATTACKSPEED);
    }

    /**
     * returns the offset of the arm on the given timestamp
     * @param loopTime time since the last animation loop
     * @return offset based on current animation
     */
    private float armOffset(float loopTime){
        if (currentAnim == WALK) {
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
        if (currentAnim == WALK) {
            return (float) (0.2 * sin(2 * loopTime));
        }
        return 0;
    }

    @Override
    public void updateAnimation(float animTime, float deltaTime) {
        headOffset.updateFluent(headOffset(animTime), deltaTime);
        leftArmOffset.updateFluent(armOffset(animTime), deltaTime);
        rightArmOffset.updateFluent(-armOffset(animTime), deltaTime);
    }

    @Override
    public void update() {

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
        // unnecessary
        currentAnim = DEFAULT;
        renderer.unlinkMesh(head);
        renderer.unlinkMesh(leftArm);
        renderer.unlinkMesh(rightArm);
        renderer.unlinkMesh(body);
    }

    @Override
    public void renderInit(Renderer renderer) {
        headOffset = new LinearlyUpdatable(headOffset(0), 0.1f);
        leftArmOffset = new LinearlyUpdatable(armOffset(0), 0.1f);
        rightArmOffset = new LinearlyUpdatable(-armOffset(0), 0.1f);
        Material material = new Material(new Vector4f(1f, 0.5f, 1f, 1f), 1f);
        Mesh headMesh = renderer.linkMesh("/bruiser_head.obj");
        Mesh bodyMesh = renderer.linkMesh("/bruiser_body.obj");
        Mesh lArmMesh = renderer.linkMesh("/bruiser_lArm.obj");
        Mesh rArmMesh = renderer.linkMesh("/bruiser_rArm.obj");
        headMesh.setMaterial(material);
        bodyMesh.setMaterial(material);
        lArmMesh.setMaterial(material);
        rArmMesh.setMaterial(material);
        head = renderer.linkMesh(headMesh, () ->
                setModelView(renderer, new Vector3f(0f, 1f + headOffset.current(), 1f)));
        body = renderer.linkMesh(bodyMesh, () ->
                setModelView(renderer));
        leftArm = renderer.linkMesh(lArmMesh, () ->
                setModelView(renderer, new Vector3f(1f, 0f, leftArmOffset.current())));
        rightArm = renderer.linkMesh(rArmMesh, () ->
                setModelView(renderer, new Vector3f(1f, 0f, rightArmOffset.current())));
    }
}
