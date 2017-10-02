package nl.tue.c2IOE0.group5.engine.rendering;

import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.util.Angle;
import org.joml.Matrix4f;
import org.joml.Vector3f;

class Transformation {

    private final Matrix4f projectionMatrix;
    private final Matrix4f modelMatrix;
    private final Matrix4f viewMatrix;

    Transformation() {
        modelMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }

    /**
     * Get the projection matrix.
     *
     * @param fov The field of view of the scene.
     * @param width The width of the window to which the scene will be rendered.
     * @param height The height of the window to which the scene will be rendered.
     * @param zNear The closest z-coordinate that will be rendered.
     * @param zFar The furthest z-coordinate that will be rendered.
     * @return The projection matrix.
     */
    Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        return projectionMatrix.identity().perspective(fov, aspectRatio, zNear, zFar);
    }

    /**
     * Get the modelview matrix. combining the model matrix with the view matrix to get a matrix representing model
     * positioning relative to the camera.
     *
     * @param position The position of the model.
     * @param rotation The rotation of the model.
     * @param scale The scale of the model.
     * @param camera The camera that will look at the model.
     * @return A modelview matrix.
     */
    Matrix4f getModelViewMatrix(Vector3f position, Vector3f rotation, float scale, Camera camera) {
        modelMatrix.identity().translate(position).
                rotateX(Angle.radf(-rotation.x)).
                rotateY(Angle.radf(-rotation.y)).
                rotateZ(Angle.radf(-rotation.z)).
                scale(scale);
        // return the final modelview matrix
        return getViewMatrix(camera).mul(modelMatrix);
    }

    /**
     * Get the view matrix from a camera.
     *
     * @param camera The camera to get a view matrix for.
     * @return A view matrix corresponding to the camera positioning.
     */
    Matrix4f getViewMatrix(Camera camera) {
        Vector3f position = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        return viewMatrix.identity()
                // first do the rotation so camera rotates over its position
                .rotateX(Angle.radf(rotation.x))
                .rotateY(Angle.radf(rotation.y))
                .rotateZ(Angle.radf(rotation.z))
                .translate(-position.x, -position.y, -position.z);
    }

}