package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.*;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import org.joml.Vector3f;

/**
 * @author Jorren
 */
public class TestObject extends GameObject {

    private Mesh mesh;

    private float boinkyness;

    public TestObject() {
        super();

        boinkyness = 0f;

        try {
            this.mesh = OBJLoader.loadMesh("/tower.obj");
            this.mesh.setTexture(new Texture("/tower.png"));
            this.setScale(40f);
            this.setPosition(0f, -1f, 0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);
        // Boinky?
        boinkyness = (boinkyness + 0.01f);
        renderer.setMaterial(new Material(mesh.getTexture()));
        renderer.boink((float)Math.sin(boinkyness) +1f, 2f, new Vector3f(0, 1f, 0), ()->
                mesh.draw());
    }

}
