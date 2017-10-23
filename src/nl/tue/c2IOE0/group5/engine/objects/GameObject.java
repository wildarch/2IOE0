package nl.tue.c2IOE0.group5.engine.objects;

import nl.tue.c2IOE0.group5.engine.provider.Updatable;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import org.joml.Vector3f;

/**
 * @author Yoeri Poels, Jorren Hendriks
 */
public abstract class GameObject extends Positionable implements Updatable {

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

    /**
     * {@link #setModelView(Renderer, Vector3f 0, Vector3f 0, Vector3f 0)}
     */
    protected void setModelView(Renderer renderer) {
        renderer.setMatrix(getPosition(), getRotation(), new Vector3f(scale));
    }

    /**
     * {@link #setModelView(Renderer, Vector3f, Vector3f 0, Vector3f 0)}
     */
    protected void setModelView(Renderer renderer, Vector3f posOffset) {
        renderer.setMatrix(getPosition().add(posOffset), getRotation(), new Vector3f(scale));
    }

    /**
     * {@link #setModelView(Renderer, Vector3f, Vector3f, Vector3f 0)}
     */
    protected void setModelView(Renderer renderer, Vector3f posOffset, Vector3f rotOffset) {
        renderer.setMatrix(getPosition().add(posOffset), getRotation().add(rotOffset), new Vector3f(scale));
    }

    /**
     * Set the modelview matrix for this object, possibly with some offset for sub-elements of this object.
     *
     * @param renderer An instance of the renderer that will draw this object.
     * @param posOffset The offset for the position.
     * @param rotOffset The offset for the rotation.
     * @param scaleOffset The offset for the scale.
     */
    protected void setModelView(Renderer renderer, Vector3f posOffset, Vector3f rotOffset, Vector3f scaleOffset) {
        renderer.setMatrix(getPosition().add(posOffset), getRotation().add(rotOffset), new Vector3f(scale).add(scaleOffset));
    }

    /**
     * Initialize the GameObject. calls renderInit if a renderer was passed
     *
     * @param renderer An instance of the renderer that will draw this object.
     * @return this object.
     */
    public final <T extends GameObject> T init(Renderer renderer){
        if (renderer != null) renderInit(renderer);
        return (T) this;
    }

    /**
     * Link meshes here and set parameters.
     * @param renderer An instance of the renderer that will draw this object.
     * @return this object.
     */
    protected abstract void renderInit(Renderer renderer);
}
