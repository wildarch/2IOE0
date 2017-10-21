package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;

/**
 * @author Jorren
 */
public class TestObject extends GameObject {

    private float boinkyness;

    public TestObject() {
        super();
        boinkyness = 0f;
        this.setScale(5f);
    }

    @Override
    public void renderInit(Renderer renderer) {
        try {
            Mesh tower = renderer.linkMesh("/tower.obj");
            tower.setMaterial(new Material("/tower.png"));
            renderer.linkMesh(tower, () -> {
                setModelView(renderer);
                renderer.boink(tower, getBounceDegree());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float getBounceDegree() {
//        return 0;
        return (float) Math.sin(boinkyness) +1f;
    }

    public void boink() {
        // updateFluent private members here
        boinkyness = (boinkyness + 0.01f);
    }

    @Override
    public void update() {
        // I'm a lazy motherfucker
    }
}
