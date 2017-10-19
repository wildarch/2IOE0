package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
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
        this.setScale(40f);
        this.setPosition(0f, -1f, 0f);
    }

    @Override
    public void renderInit(Renderer renderer) {
        try {
            InstancedMesh tower = renderer.linkMesh("/tower.obj", () -> {
                setModelView(renderer);
            });
            tower.getMesh().setMaterial(new Material("/tower.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void boink() {
        // update private members here
        boinkyness = (boinkyness + 0.01f);
    }

    @Override
    public void update() {
        // I'm a lazy motherfucker
    }
}
