package nl.tue.c2IOE0.group5.engine.objects;

/**
 * @author Geert van Ieperen
 *         created on 13-10-2017.
 *
 * A gameobject with multiple meshes, that uses some animation over time
 */
public interface Animatable {

    /**
     * is called every rendering frame, object should set its current animationframe forward according to deltaT
     * @param deltaT passed time since last frame (determined beforehand: all animations will use the same value)
     */
    void animationUpdate(float deltaT);
}
