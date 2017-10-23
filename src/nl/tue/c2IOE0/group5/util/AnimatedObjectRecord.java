package nl.tue.c2IOE0.group5.util;

import nl.tue.c2IOE0.group5.engine.objects.Animatable;

/**
 * @author Geert van Ieperen
 *         created on 13-10-2017.
 * This class tracks the progress of the animation of the target, and handles changes in animation.
 * Has some properties of Decorator and Adapter pattern (i guess)
 */
public class AnimatedObjectRecord {
    
    private final Animatable target;

    // time since begin of current animation loop
    private float animationTimer = 0;

    public AnimatedObjectRecord(Animatable target) {
        this.target = target;
    }

    public void updateAnimation(float deltaT) {
        animationTimer += deltaT;
        target.updateAnimation(animationTimer, deltaT);
    }

    /**
     * may become false if the target is for instance a unit that may die
     * @return false if this unit is still is rendered
     */
    public boolean mustBeRemoved(){
        return target.mustBeRemoved();
    }
}
