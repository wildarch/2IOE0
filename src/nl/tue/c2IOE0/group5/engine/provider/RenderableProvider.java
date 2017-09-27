package nl.tue.c2IOE0.group5.engine.provider;

import org.joml.Vector3f;

/**
 * @author Yoeri Poels
 * Interface for renderable providers. A provider extended with methods relevant to rendering in 3D space, necessary for the renderer.
 */
public interface RenderableProvider extends Provider {
    public Vector3f getPosition();

    public float getScale();

    public Vector3f getRotation();
}
