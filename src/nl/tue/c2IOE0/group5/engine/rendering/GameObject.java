package nl.tue.c2IOE0.group5.engine.rendering;

import org.joml.Vector3f;

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
        rotation = new Vector3f(0, 110f, 0);

        this.mesh = new Mesh(new float[]{
                -0.5f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f,  0.5f, 0.0f,
        }, new int[]{
                0, 1, 3, 3, 1, 2,
        });

        setPosition(0, 0, -2);
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
