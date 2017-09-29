package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;

/**
 * @author Jorren Hendriks
 */
public class TestProvider implements Provider {

    // register resources here, e.g.
    private int updatecounter;

    private GameObject object;

    private Camera camera;

    @Override
    public void init(Engine engine) {
        // initialize resources here, e.g.
        updatecounter = 0;
        camera = engine.getCamera();
        this.object = new TestObject();
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
        object.setPosition(0f, -0.5f, -4);
        //object.setRotation(0f, 0f, 0f);
        object.rotate(0.0f, 2f, 0f);

        window.setClearColor(r, g, b, 1f);

        //camera.moveRelative(0.05f/*((float)Math.random()-0.5f)*/, 0.01f/*((float)Math.random()-0.5f)*/, 0.10f/*((float)Math.random()-0.5f)*/);
        //camera.rotate(0.00f/*((float)Math.random()-0.5f)*/, 0.00f/*((float)Math.random()-0.5f)*/, 6.00f/*((float)Math.random()-0.5f)*/);

        object.draw(window, renderer);
    }
}
