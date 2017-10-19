package nl.tue.c2IOE0.group5.engine.objects;

import nl.tue.c2IOE0.group5.engine.Timer;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Interpolates between the current position of an object and its target.
 * @author Daan de Graaf
 */
public class PositionInterpolator {
    /**
     * Difference in position so small that nobody cares anymore
     */
    private static final double EPSILON = 0.01;

    private Positionable p;
    private Vector3f position;
    private Vector3f target = null;
    private long targetReachTime = 0;
    private float speed;    // In milliseconds

    /**
     * Creates a new PositionInterpolator
     * @param p The positionable to move
     * @param speed The speed to move with, in units per second
     */
    public PositionInterpolator(Positionable p, float speed) {
        this.p = p;
        this.position = p.getPosition();
        this.speed = speed / 1000;
    }


    public Vector3fc getTarget() {
        return target.toImmutable();
    }

    public void setTarget(Vector3f target, long currentTime) {
        this.position = p.getPosition();
        this.target = new Vector3f(target);
        float distance = target.distance(position.toImmutable());
        targetReachTime = currentTime + (long)(distance / speed);
    }

    /**
     * Call this at every game tick
     * @param currentTime Current time in milliseconds
     *                    (as returned by {@link Timer#getLoopTime()}
     * @return whether or not the target was reached
     */
    public boolean update(long currentTime) {
        if (currentTime > targetReachTime) {
            if (target != null) p.setPosition(target);
            target = null;
            targetReachTime = Long.MAX_VALUE;
            return true;
        }
        return false;
    }

    /**
     * Call this at each draw call
     * @param deltaTime Time between now and the last frame, in milliseconds
     *                  (as returned by {@link Timer#getElapsedTime()})
     * @return whether or not the target was reached
     */
    public boolean draw(float deltaTime) {
        if(target == null) return true;
        float step = deltaTime * speed;
        // TODO find out why we need to multiply by 0.5
        p.move(getDirection().mul(step * 0.25f));
        float distance = p.getPosition().distance(target.toImmutable());
        if (distance < EPSILON) {
            target = null;
            return true;
        }
        return false;
    }

    private Vector3f getDirection() {
        if(target == null) return new Vector3f(0);
        Vector3f offset = new Vector3f(target);
        offset.sub(p.getPosition().toImmutable());
        offset.normalize();
        return offset;
    }
}
