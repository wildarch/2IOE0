package nl.tue.c2IOE0.group5.engine.provider;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjectProvider<T extends GameObject> implements Provider {
    protected List<T> objects = new ArrayList<>();

    @Override
    public void update() {
        for (T o : objects) {
            o.update();
        }
    }
}
