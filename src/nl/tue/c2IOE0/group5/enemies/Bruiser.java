package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.util.LinearlyUpdatable;
import nl.tue.c2IOE0.group5.util.SmoothUpdatable;
import org.joml.Vector3f;

import static java.lang.Math.sin;
import static nl.tue.c2IOE0.group5.enemies.AnimatedUnit.AnimationLoop.WALK;

/**
 * @author Geert van Ieperen
 *         created on 13-10-2017.
 */
public class Bruiser extends AnimatedUnit {

    private Mesh body;
    private Mesh head;
    private Mesh leftArm;
    private Mesh rightArm;

    private SmoothUpdatable headOffset;
    private SmoothUpdatable leftArmOffset;
    private SmoothUpdatable rightArmOffset;

    @Override
    public GameObject init(Renderer renderer) {
        try {
            head = renderer.linkMesh("/bruiser_head.obj", (mesh)->{
                setModelView(renderer, new Vector3f(0f, 1f + headOffset.current(), 1f));
                setModelLightViewScene(renderer);
            }, (mesh) -> {
                setModelLightView(renderer, new Vector3f(0f, 1f + headOffset.current(), 1f), new Vector3f(), 1f);
            });
            body = renderer.linkMesh("/bruiser_body.obj", (mesh)->{
                setModelView(renderer);
                setModelLightViewScene(renderer);
            }, (mesh) -> {
                setModelLightView(renderer);
            });
            leftArm = renderer.linkMesh("/bruiser_lArm.obj", (mesh)->{
                setModelView(renderer, new Vector3f(1f, 0f, leftArmOffset.current()));
                setModelLightViewScene(renderer);
            }, (mesh) -> {
                setModelLightView(renderer, new Vector3f(0f, 1f + headOffset.current(), 1f), new Vector3f(), 1f);
            });
            rightArm = renderer.linkMesh("/bruiser_rArm.obj", (mesh)->{
                setModelView(renderer, new Vector3f(1f, 0f, rightArmOffset.current()));
                setModelLightViewScene(renderer);
            }, (mesh) -> {
                setModelLightView(renderer, new Vector3f(0f, 1f + headOffset.current(), 1f), new Vector3f(), 1f);
            });
        } catch (Exception e) {

        }

        headOffset = new LinearlyUpdatable(headOffset(0), 0.1f);
        leftArmOffset = new LinearlyUpdatable(armOffset(0), 0.1f);
        rightArmOffset = new LinearlyUpdatable(-armOffset(0), 0.1f);

        return this;
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
    public void updateAngles(float animTime, float deltaTime) {
        headOffset.updateFluent(headOffset(animTime), deltaTime);
        leftArmOffset.updateFluent(armOffset(animTime), deltaTime);
        rightArmOffset.updateFluent(-armOffset(animTime), deltaTime);
    }
}
