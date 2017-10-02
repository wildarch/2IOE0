package nl.tue.c2IOE0.group5.engine.objects;

import nl.tue.c2IOE0.group5.engine.rendering.Drawable;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;

/**
 * @author Yoeri Poels, Jorren Hendriks
 */
public abstract class GameObject extends Positionable implements Drawable {

    private float scale;

    public GameObject() {
        super();
        scale = 1f;
    }

    /**
     * Get the current scale of the object.
     *
     * @return The current scale.
     */
    public float getScale() {
        return scale;
    }

    /**
     * Sets the scale of this gameobject.
     *
     * @param scale The scale of the object.
     */
    public void setScale(float scale) {
        this.scale = scale;
    }


    @Override
    public void draw(Window window, Renderer renderer) {
        renderer.setModelViewMatrix(getPosition(), getRotation(), getScale());

        // actually draw objects here
    }

    public void update() {
        // Update the state of objects
    }
}
