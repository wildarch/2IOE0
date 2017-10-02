package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Texture;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.OBJLoader;

import java.io.IOException;

/**
 * @author Jorren
 */
public class TestObject extends GameObject {

    private Mesh mesh;

    public TestObject() {
        super();

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
        mesh.draw();
    }

}
