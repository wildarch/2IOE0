package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Texture;
import nl.tue.c2IOE0.group5.engine.rendering.Window;

import java.io.IOException;

/**
 * @author Jorren
 */
public class TestObject extends GameObject {

    private Mesh mesh;

    public TestObject() {
        super();

        try {
            this.mesh = new Mesh(new float[] {
                -0.5f,   0.8f,   0.5f,
                -0.4f,  -0.8f,   0.4f,
                 0.4f,  -0.8f,   0.4f,
                 0.5f,   0.8f,   0.5f,
                -0.5f,   0.8f,  -0.5f,
                 0.5f,   0.8f,  -0.5f,
                -0.4f,  -0.8f,  -0.4f,
                 0.4f,  -0.8f,  -0.4f,
            }, new float[]{
                0.2f, 0.2f,
                0.2f, 0.8f,
                0.8f, 0.8f,
                0.8f, 0.2f,
                0.8f, 0.2f,
                0.2f, 0.2f,
                0.8f, 0.8f,
                0.2f, 0.8f,
            }, new int[] {
                0, 1, 3, 3, 1, 2,
                4, 0, 3, 5, 4, 3,
                3, 2, 7, 5, 3, 7,
                6, 1, 0, 6, 0, 4,
                2, 1, 6, 2, 6, 7,
                7, 6, 4, 7, 4, 5,
            }, new Texture("/texture.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);

        mesh.draw();
    }

}
