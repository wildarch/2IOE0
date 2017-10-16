package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;

/**
 * @author Jorren Hendriks
 */
public class TestProvider implements Provider {

    // register resources here, e.g.
    private int updatecounter;

    private TestObject object;

    @Override
    public void init(Engine engine) {
        // initialize resources here, e.g.
        updatecounter = 0;
        this.object = new TestObject().init(engine.getRenderer());
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
        object.setPosition(3f, 2f, 3f);
        object.boink();
    }
}
