package nl.tue.c2IOE0.group5.engine.provider;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Simulator;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic provider class that manages a list of objects of the same type.
 * @author Daan de Graaf
 * @param <T> The type of GameObject to manage.
 */
public abstract class ObjectProvider<T extends GameObject> implements Provider<Simulator> {
    protected List<T> objects = new ArrayList<>();
    private Renderer renderer;
    private Engine engine;

    @Override
    public void init(Simulator sim) {
        if(sim instanceof Engine) {
            Engine e = (Engine) sim;
            // Only do rendering if the current simulator is actually an Engine
            renderInit(e);
            renderer = e.getRenderer();
            engine = e;
        }
    }

    public abstract void renderInit(Engine engine);

    @Override
    public void update() {
        // Update all children
        for (T o : objects) {
            o.update();
        }
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public Engine getEngine() {return this.engine;}
}
