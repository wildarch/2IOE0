package nl.tue.c2IOE0.group5.engine.objects;

import nl.tue.c2IOE0.group5.engine.Timer;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Interpolates between the current position of an object and its target.
 * @author Daan de Graaf
 */
public class PositionInterpolator {

    private static final Vector3f ZERO_VECTOR = new Vector3f();

    private Positionable p;
    private Vector3f target = null;
    private float speed;    // In milliseconds

    private Vector3f direction = new Vector3f();


    /**
     * Creates a new PositionInterpolator
     * @param p The positionable to move
     * @param speed The speed to move with, in units per second
     */
    public PositionInterpolator(Positionable p, float speed) {
        this.p = p;
        this.speed = speed / 1000;
    }


    public Vector3fc getTarget() {
        return target.toImmutable();
    }

    public void setTarget(Vector3f target) {
        this.target = new Vector3f(target);

        direction = new Vector3f(target);
        direction.sub(p.getPosition()).normalize();
    }

    public boolean targetReached() {
        return target == null || target.equals(p.getPosition());
    }

    /**
     * Call this at every game tick
     * @param elapsedTime Current time in milliseconds
     *                    (as returned by {@link Timer#getTime()}
     * @return whether or not the target was reached
     */
    public long update(long elapsedTime) {
        if (target == null) return 0;

        float distance = elapsedTime * speed;
        float maxDist = p.getPosition().sub(target).length();
        if (distance > maxDist) {
            p.setPosition(target);
            target = null;
            return (long) ((distance - maxDist) / speed);
        }
        Vector3f path = new Vector3f(target).sub(p.getPosition()).normalize().mul(distance);
        p.move(path);
        return 0;
    }

    /**
     * Call this at each draw call
     * @param deltaTime Time between now and the last gametick, in milliseconds
     * @return the offset since last gametick
     */
    public Vector3f getOffset(long deltaTime) {
        if (target == null) return ZERO_VECTOR;

        float distance = deltaTime * speed;
        float maxDist = p.getPosition().sub(target).length();
        if (distance > maxDist) distance = maxDist;
        return new Vector3f(target).sub(p.getPosition()).normalize().mul(distance);
    }

    public Vector3f getDirection() {
        return direction;
    }
}
