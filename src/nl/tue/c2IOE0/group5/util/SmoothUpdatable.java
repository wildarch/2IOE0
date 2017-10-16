package nl.tue.c2IOE0.group5.util;

/**
 * @author Geert van Ieperen
 *         created on 13-10-2017.
 */
public abstract class SmoothUpdatable extends Updatable<Float> {

    protected final float acceleration;

    public SmoothUpdatable(float initial, float acceleration) {
        super(initial);
        this.acceleration = acceleration;
    }

    public SmoothUpdatable(float current, float previous, float acceleration){
        super(current, previous);
        this.acceleration = acceleration;
    }

    public abstract double difference();

    public abstract void updateFluent(float target, float deltaTime);
}
