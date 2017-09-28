package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;

import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author Jorren Hendriks
 */
public class TestProvider implements Provider {

    // register resources here, e.g.
    private int updatecounter;

    private GameObject object;

    @Override
    public void init(Engine engine) {
        // initialize resources here, e.g.
        updatecounter = 0;

        this.object = new GameObject();
    }

    public void ud() {
        updatecounter += 1;
        updatecounter %= 5;
    }

    @Override
    public void update() {
        // do updates here using resources, e.g.

    }

    @Override
    public void draw(Window window, Renderer renderer) {
        // draw attached objects here, e.g.
        float r = 0;
        float g = 0;
        float b = 0;

        switch(updatecounter) {
            case 0:
                g = 1;
            case 1:
                r = 1;
                break;
            case 2:
                g = 1;
            case 3:
                b = 1;
                break;
            case 4:
                r = 1;
                b = 1;
        }

        object.setPosition(0f, 2f, -1*updatecounter);

        window.setClearColor(r, g, b, 1f);

        object.draw(window, renderer);
    }
}
