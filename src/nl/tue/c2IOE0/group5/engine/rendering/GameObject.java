package nl.tue.c2IOE0.group5.engine.rendering;

import org.joml.Vector3f;

import java.io.IOException;

/**
 * @author Yoeri Poels
 */
public class GameObject implements Drawable {
    Mesh mesh;

    private final Vector3f position;

    private float scale;

    private final Vector3f rotation;

    public GameObject() {
        // initialize resources here, e.g.

        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0f, 0f, -90f);

        try {
            this.mesh = new Mesh(new float[] {
                    // VO
                    -0.5f,  0.5f,  0.5f,
                    // V1
                    -0.5f, -0.5f,  0.5f,
                    // V2
                    0.5f, -0.5f,  0.5f,
                    // V3
                    0.5f,  0.5f,  0.5f,
                    // V4
                    -0.5f,  0.5f, -0.5f,
                    // V5
                    0.5f,  0.5f, -0.5f,
                    // V6
                    -0.5f, -0.5f, -0.5f,
                    // V7
                    0.5f, -0.5f, -0.5f,
            }, new float[]{
                    0f, 1f,
                    0f, 1f,
                    1f, 0f,
                    1f, 0f,
                    0f, 0f,
                    0f, 0f,
                    0f, 0f,
                    0f, 0f
            }, new int[] {
                    // Front face
                    0, 1, 3, 3, 1, 2,
                    // Top Face
                    4, 0, 3, 5, 4, 3,
                    // Right face
                    3, 2, 7, 5, 3, 7,
                    // Left face
                    6, 1, 0, 6, 0, 4,
                    // Bottom face
                    2, 1, 6, 2, 6, 7,
                    // Back face
                    7, 6, 4, 7, 4, 5,
            }, new Texture("/texture.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setPosition(0, 2, 2);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void update() {
        // do updates here using resources, e.g.
    }

    public void draw(Window window, Renderer renderer) {
        renderer.setTransformationMatrix(getPosition(), getRotation(), getScale());
        mesh.draw();
    }
}
