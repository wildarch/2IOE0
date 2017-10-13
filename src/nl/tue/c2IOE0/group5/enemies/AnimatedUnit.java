package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.objects.Animatable;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;

/**
 * @author Geert van Ieperen
 *         created on 13-10-2017.
 */
public abstract class AnimatedUnit extends GameObject implements Animatable {

    protected enum AnimationLoop {
        DEFAULT, WALK, SHOOT
    }

    protected AnimationLoop currentAnim;

    // time since begin of current animation loop
    protected float animationTimer = 0;


    @Override
    public void animationUpdate(float deltaT) {
        animationTimer += deltaT;
        createStructure(animationTimer, deltaT);
    }

    public abstract void createStructure(float animTime, float deltaTime);

    /**
     * initiates this unit to start with a walking animation
     */
    protected void walkAnimation(){
        animationTimer = 0;
        currentAnim = AnimationLoop.WALK;
    }

    /**
     * initiates this unit to start with a shooting animation
     */
    protected void shootAnimation(){
        animationTimer = 0;
        currentAnim = AnimationLoop.SHOOT;
    }

    /**
     * sets this units to a idle stance (often without animation)
     */
    protected void defaultStance(){
        currentAnim = AnimationLoop.DEFAULT;
    }

    public AnimationLoop getCurrentAnim() {
        return currentAnim;
    }
}
