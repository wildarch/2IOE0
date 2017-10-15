package nl.tue.c2IOE0.group5.engine.objects;

import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Jorren
 */
public class PositionsInterpolator {

    private Positionable positionable;
    private Vector3f direction;             // direction for estimate in draw frame
    private List<Vector3f> targets = null;
    private float speed;
    private boolean hold;

    /**
     * Create a new position interpolator
     *
     * @param positionable Reference to the position to interpolate
     * @param speed The speed in units / ms
     */
    public PositionsInterpolator(Positionable positionable, float speed) {
        this.positionable = positionable;
        this.speed = speed / 1000f;
        this.targets = new LinkedList<>();
        direction = new Vector3f(0);
    }

    public List<Vector3f> getTargets() {
        return targets;
    }

    private boolean hasTarget() {
        return targets.size() > 0;
    }

    public void addTarget(Vector3f target) {
        targets.add(target);
    }

    private boolean removeTarget() {
        if (!hasTarget()) return false;
        targets.remove(0);
        return true;
    }

    private Vector3f currentTarget() {
        return new Vector3f(targets.get(0));
    }

    /**
     * Call on each gametick. Return whether the interpolator is done executing its tasks.
     *
     * @param elapsedTime The time elapsed since last tick.
     * @return whether or not the target was reached.
     */
    public boolean update(long elapsedTime) {
        if (hold) return !hasTarget();

        double distance = elapsedTime * speed;

        while (distance > 0) {
            if (!hasTarget()) return true;
            Vector3f position = positionable.getPosition();
            Vector3f route = currentTarget();
            if (distance <= route.distance(position)) {
                route.sub(position);
                positionable.setPosition(positionable.getPosition().add(route.normalize().mul((float) distance)));
                direction = positionable.getPosition().sub(position).normalize();
                // subtract old position from new position
                break;
            } else {
                positionable.setPosition(route);
                if (!removeTarget()) {
                    return true;
                }
                distance -= route.length();
            }
        }
        return false;
    }


    /**
     * Call on each frame draw. Return the position offset from the {@link Positionable} at this frame.
     *
     * @param elapsedTime The time elapsed since last frame.
     * @return The offset of the positionable position.
     */
    public Vector3f draw(float elapsedTime) {
        float distance = speed * elapsedTime;
        return new Vector3f(direction).mul(distance);
    }

    public void hold() {
        this.hold = true;
    }

    public void release() {
        this.hold = false;
    }

}
