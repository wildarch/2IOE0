package nl.tue.c2IOE0.group5.util;

/**
 * Created by s152717 on 4-5-2017.
 */
public class UpdatableFloat extends Updatable<Float> {

    public UpdatableFloat(float initial) {
        super(initial);
    }

    public UpdatableFloat(float current, float previous){
        super(current, previous);
    }

    /**
     * @return the increase of the last update, defined as (current - previous)
     */
    public float difference() {
        return current() - previous();
    }
}
