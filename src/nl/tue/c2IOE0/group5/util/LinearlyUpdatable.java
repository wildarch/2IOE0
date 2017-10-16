package nl.tue.c2IOE0.group5.util;

/**
 * Created by s152717 on 4-5-2017.
 * may be superfluous
 */
public class LinearlyUpdatable extends SmoothUpdatable {

    public LinearlyUpdatable(Float initial, float acceleration) {
        super(initial, acceleration);
    }

    public LinearlyUpdatable(Float current, Float previous, float acceleration) {
        super(current, previous, acceleration);
    }

    /**
     * @return the increase of the last updateFluent, defined as (current - previous)
     */
    @Override
    public double difference() {
        return current() - previous();
    }

    /**
     * updates this value to a value that is closer toward {@code target} defined as the exact equal of {@code acceleration}
     * @param target the target value
     */
    @Override
    public void updateFluent(float target, float deltaTime){
        if (current() < target){
            super.update(
                    Math.min(target, current() + (acceleration * deltaTime))
            );
        } else {
            super.update(
                    Math.min(target, current() - (acceleration * deltaTime))
            );
        }
    }
}
