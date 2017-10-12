package nl.tue.c2IOE0.group5.engine.objects;

import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import org.joml.Vector3f;

/**
 * @author Yoeri Poels, Jorren Hendriks
 */
public abstract class GameObject extends Positionable {

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
     * {@link #setModelView(Renderer, Vector3f 0, Vector3f 0, float 0)}
     */
    protected void setModelView(Renderer renderer) {
        renderer.setModelViewMatrix(getPosition(), getRotation(), getScale());
    }

    protected void setModelLightView(Renderer renderer) {
        renderer.setModelLightViewMatrix(getPosition(), getRotation(), getScale());
    }

    /**
     * {@link #setModelView(Renderer, Vector3f, Vector3f 0, float 0)}
     */
    protected void setModelView(Renderer renderer, Vector3f posOffset) {
        renderer.setModelViewMatrix(posOffset.add(getPosition()), getRotation(), getScale());
    }

    /**
     * {@link #setModelView(Renderer, Vector3f, Vector3f, float 0)}
     */
    protected void setModelView(Renderer renderer, Vector3f posOffset, Vector3f rotOffset) {
        renderer.setModelViewMatrix(posOffset.add(getPosition()), rotOffset.add(getRotation()), getScale());
    }

    /**
     * Set the modelview matrix for this object, possibly with some offset for sub-elements of this object.
     *
     * @param renderer An instance of the renderer that will draw this object.
     * @param posOffset The offset for the position.
     * @param rotOffset The offset for the rotation.
     * @param scaleOffset The offset for the scale.
     */
    protected void setModelView(Renderer renderer, Vector3f posOffset, Vector3f rotOffset, float scaleOffset) {
        renderer.setModelViewMatrix(posOffset.add(getPosition()), rotOffset.add(getRotation()), scaleOffset+getScale());
    }

    protected void setModelLightView(Renderer renderer, Vector3f posOffset, Vector3f rotOffset, float scaleOffset) {
        renderer.setModelLightViewMatrix(posOffset.add(getPosition()), rotOffset.add(getRotation()), scaleOffset+getScale());
    }



    /**
     * Initialize the gameobject. Link meshes here and define their render methods.
     *
     * @param renderer An instance of the renderer that will draw this object.
     * @return this object.
     */
    public abstract GameObject init(Renderer renderer);
}
