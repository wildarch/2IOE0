package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.util.LinearlyUpdatable;
import nl.tue.c2IOE0.group5.util.SmoothUpdatable;

import static java.lang.Math.sin;
import static nl.tue.c2IOE0.group5.enemies.AnimatedUnit.AnimationLoop.WALK;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslated;

/**
 * @author Geert van Ieperen
 *         created on 13-10-2017.
 */
public class Bruiser extends AnimatedUnit {

    private Mesh body;
    private Mesh head;
    private Mesh arm;

    private SmoothUpdatable headOffset;
    private SmoothUpdatable leftArmOffset;
    private SmoothUpdatable rightArmOffset;

    @Override
    public GameObject init(Renderer renderer) {
        try {
            head = renderer.linkMesh("/bruiser_head.obj");
            body = renderer.linkMesh("/bruiser_body.obj");
            arm = renderer.linkMesh("/bruiser_arm.obj");
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
    private double armOffset(float loopTime){
        if (currentAnim == WALK) {
            return sin(loopTime);
        }
        return 0;
    }

    /**
     * returns the height offset of the head on the given timestamp
     * @param loopTime time since the last animation loop
     * @return offset based on current animation
     */
    private double headOffset(float loopTime){
        if (currentAnim == WALK) {
            return 0.2 * sin(2 * loopTime);
        }
        return 0;
    }

    @Override
    public void createStructure(float animTime, float deltaTime) {
        headOffset.updateFluent(headOffset(animTime), deltaTime);
        leftArmOffset.updateFluent(armOffset(animTime), deltaTime);
        rightArmOffset.updateFluent(-armOffset(animTime), deltaTime);

        body.draw();
        glPushMatrix();
        {
            glTranslated(0, 1 + headOffset.current(), 1);
            head.draw();
        }
        glPopMatrix();
        glPushMatrix();
        {
            glTranslated(1, 0, leftArmOffset.current());
            arm.draw();
        }
        glPopMatrix();
        glPushMatrix();
        {
            glTranslated(-1, 0, rightArmOffset.current());
            arm.draw();
        }
        glPopMatrix();
    }
}
