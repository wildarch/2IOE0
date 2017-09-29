package nl.tue.c2IOE0.group5.engine.objects;

import org.joml.Vector3f;

/**
 * @author Jorren Hendriks
 */
public abstract class Positionable {

    // position of the camera
    private final Vector3f position;
    // rotation of the camera in degrees
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
     * Get the current position of the camera.
     *
     * @return The position of the camera.
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Set the current position of the camera.
     *
     * @param x The new x-coordinate of the camera.
     * @param y The new y-coordinate of the camera.
     * @param z The new z-coordinate of the camera.
     */
    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    /**
     * Move the camera relative to its previous location.
     *
     * @param offsetX The movement in the x direction.
     * @param offsetY The movement in the y direction.
     * @param offsetZ The movement in the z direction.
     */
    public void move(float offsetX, float offsetY, float offsetZ) {
        position.x += offsetX;
        position.y += offsetY;
        position.z += offsetZ;
    }

    /**
     * Move the camera relative to its previous location and its panning from the center.
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
     * Get the current rotation of the camera in degrees.
     *
     * @return The rotation of the camera in degrees.
     */
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * Set the current rotation of the camera.
     *
     * @param x The new x rotation of the camera in degrees.
     * @param y The new y rotation of the camera in degrees.
     * @param z The new z rotation of the camera in degrees.
     */
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    /**
     * Rotate the camera relative to its current rotation.
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
