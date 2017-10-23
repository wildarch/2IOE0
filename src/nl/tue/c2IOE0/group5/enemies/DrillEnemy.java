package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.Animatable;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

import static nl.tue.c2IOE0.group5.engine.objects.Animatable.AnimationLoop.DEFAULT;
import static nl.tue.c2IOE0.group5.util.Angle.rotateVector;

/**
 * @author Geert van Ieperen
 *         created on 23-10-2017.
 * the fast unit with meelee attacks.
 */
public class DrillEnemy extends Enemy implements Animatable {

    private final static int MAXHEALTH = 100;
    private final static float SPEED = 0.1f;
    private final static int ATTACKSPEED = 400;

    private InstancedMesh body;
    private InstancedMesh drill;
    private InstancedMesh wheel;

    private AnimationLoop currentAnim = DEFAULT;
    private float drillRotation = 0f;
    private float wheelRoatation = 0f;

    public DrillEnemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider, Vector2i initialPosition, List<Vector2i> targetPositions) {
        super(loopTimer, renderTimer, gridProvider, initialPosition, targetPositions, MAXHEALTH, SPEED, ATTACKSPEED);
        setScale(0.1f);
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
        Vector3f yVec = new Vector3f(0, 1, 0);

        body = renderer.linkMesh("/models/enemies/drillEnemy/BODY.obj", darkMatter, () -> {
            setModelView(renderer, new Vector3f(), new Vector3f(0, 90, 0));

            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

        drill = renderer.linkMesh("/models/enemies/drillEnemy/DRILL.obj", Material.SILVER, () -> {
            final Vector3f displacement = new Vector3f(0f, 1.674f, 2.713f).mul(getScale());
            final Vector3f drillOffset = rotateVector(displacement, yVec, 90);
            setModelView(renderer, drillOffset, new Vector3f(0, 90, 0));

            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });

        wheel = renderer.linkMesh("/models/enemies/drillEnemy/WHEEL.obj", darkMatter, () -> {
            final Vector3f displacement = new Vector3f(0f, 0.628f, 2.08f).mul(getScale());
            final Vector3f wheelOffset = rotateVector(displacement, yVec, 90);
            setModelView(renderer, wheelOffset, new Vector3f(0, 90, 0));

            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });
    }
}
