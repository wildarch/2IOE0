package nl.tue.c2IOE0.group5.engine.objects;

import nl.tue.c2IOE0.group5.util.Angle;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * @author Jorren Hendriks
 */
public abstract class Positionable {

    // position of the object
    private final Vector3f position;
    // rotation of the object in degrees
    private final Vector3f rotation;

    public Positionable() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
    }

    public Positionable(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Get the current position of the object.
     *
     * @return The position of the object.
     */
    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    /**
     * Set the current position of the object.
     *
     * @param x The new x-coordinate of the object.
     * @param y The new y-coordinate of the object.
     * @param z The new z-coordinate of the object.
     */
    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    /**
     * Set the current position of the object
     * @param p new coordinates of the object
     */
    public void setPosition(Vector3f p) {
        position.set(p);
    }

    /**
     * Move the object relative to its previous location.
     *
     * @param offsetX The movement in the x direction.
     * @param offsetY The movement in the y direction.
     * @param offsetZ The movement in the z direction.
     */
    public void move(float offsetX, float offsetY, float offsetZ) {
        move(new Vector3f(offsetX, offsetY, offsetZ));
    }

    /**
     * Move the object relative to its previous location.
     *
     * @param offset The movement
     */
    public void move(Vector3f offset) {
        position.add(offset);
    }

    /**
     * Move the object relative to its previous location and its panning from the center.
     *
     * @param offsetX The movement in the x direction relative to the panning.
     * @param offsetY The movement in the y direction.
     * @param offsetZ The movement in the z direction relative to the panning.
     */
    public void moveRelative(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    /**
     * Move the object relative to its previous location and its panning from the center
     *
     * @param offset The movement
     */
    public void moveRelative(Vector3f offset) {
        moveRelative(offset.x(), offset.y(), offset.z());
    }

    /**
     * Get the current rotation of the object in degrees.
     *
     * @return The rotation of the object in degrees.
     */
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * Set the current rotation of the object.
     *
     * @param x The new x rotation of the object in degrees.
     * @param y The new y rotation of the object in degrees.
     * @param z The new z rotation of the object in degrees.
     */
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    /**
     * Set the y rotation of the object in a certain direction.
     * @param d
     */
    public void setRotation(Vector3f d) {
        float rotY = Angle.degf((float)Math.atan2(d.z, d.x));
        setRotation(rotation.x, rotY, rotation.z);
    }

    /**
     * Rotate the object relative to its current rotation.
     *
     * @param offsetX The rotation in the x direction in degrees.
     * @param offsetY The rotation in the y direction in degrees.
     * @param offsetZ The rotation in the z direction in degrees.
     */
    public void rotate(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }

}
