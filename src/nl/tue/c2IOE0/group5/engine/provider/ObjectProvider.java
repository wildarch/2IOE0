package nl.tue.c2IOE0.group5.engine.provider;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Simulator;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjectProvider<T extends GameObject> implements Provider<Simulator> {
    protected List<T> objects = new ArrayList<>();
    private Renderer renderer;

    @Override
    public void init(Simulator sim) {
        if(sim instanceof Engine) {
            Engine e = (Engine) sim;
            renderInit(e);
            renderer = e.getRenderer();
        }
    }

    public abstract void renderInit(Engine engine);

    @Override
    public void update() {
        for (T o : objects) {
            o.update();
        }
    }

    public Renderer getRenderer() {
        return renderer;
    }
}
