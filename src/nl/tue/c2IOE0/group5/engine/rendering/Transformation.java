package nl.tue.c2IOE0.group5.engine.rendering;

import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.util.Angle;
import org.joml.Matrix4f;
import org.joml.Vector3f;

class Transformation {

    private final Matrix4f modelMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f orthoProjMatrix;
    private final Matrix4f lightViewMatrix;
    private final Matrix4f modelLightViewMatrix;
    private final Matrix4f modelViewMatrix;
    private final Matrix4f modelLightMatrix;

    Transformation() {
        modelMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        orthoProjMatrix = new Matrix4f();
        lightViewMatrix = new Matrix4f();
        modelLightViewMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        modelLightMatrix = new Matrix4f();
    }


    public final Matrix4f getOrthoProjectionMatrix() {
        return orthoProjMatrix;
    }

    public Matrix4f updateOrthoProjectionMatrix(float left, float right, float bottom, float top, float zNear, float zFar) {
        orthoProjMatrix.identity();
        orthoProjMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
        return orthoProjMatrix;
    }

    public Matrix4f getLightViewMatrix() {
        return lightViewMatrix;
    }

    public void setLightViewMatrix(Matrix4f lightViewMatrix) {
        this.lightViewMatrix.set(lightViewMatrix);
    }

    public Matrix4f updateLightViewMatrix(Vector3f position, Vector3f rotation) {
        return updateGenericViewMatrix(position, rotation, lightViewMatrix);
    }

    private Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix) {
        matrix.identity();
        // First do the rotation so camera rotates over its position
        matrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        matrix.translate(-position.x, -position.y, -position.z);
        return matrix;
    }

    public Matrix4f buildModelViewMatrix(Vector3f position, Vector3f rotation, float scale, Matrix4f matrix) {
        modelMatrix.identity().translate(position).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(scale);
        modelViewMatrix.set(matrix);
        return modelViewMatrix.mul(modelMatrix);
    }

    public Matrix4f buildModelLightViewMatrix(Vector3f position, Vector3f rotation, float scale, Matrix4f matrix) {
        modelLightMatrix.identity().translate(position).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(scale);
        modelLightViewMatrix.set(matrix);
        return modelLightViewMatrix.mul(modelLightMatrix);
    }

    /**
     * Get the modelview matrix with multi-axis scaling. combining the model matrix with the view matrix to get a matrix representing model
     * positioning relative to the camera.
     *
     * @param position The position of the model.
     * @param rotation The rotation of the model.
     * @param scale The scale of the model.
     * @param camera The camera that will look at the model.
     * @return A modelview matrix.
     */
    Matrix4f getModelViewMatrix(Vector3f position, Vector3f rotation, Vector3f scale, Camera camera) {
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