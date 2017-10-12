package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.OBJLoader;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import org.joml.Vector3f;

/**
 * @author Jorren
 */
public class TestObject extends GameObject {

    private Mesh mesh;

    private float boinkyness;

    public TestObject() {
        super();
        boinkyness = 0f;
        this.setScale(40f);
        this.setPosition(0f, -1f, 0f);
    }

    @Override
    public TestObject init(Renderer renderer) {
        try {
            Mesh tower = renderer.linkMesh("/tower.obj", (mesh) -> {
                setModelView(renderer);
                mesh.draw();
                //renderer.boink((float) Math.sin(boinkyness) +1f, new Vector3f(0), new Vector3f(1), mesh::draw);
            }, (mesh) -> {
                setModelLightView(renderer);
                mesh.draw();
            });

            tower.setMaterial(new Material("/tower.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public void boink() {
        // update private members here
        boinkyness = (boinkyness + 0.01f);
        //renderer.boink((float)Math.sin(boinkyness) +1f, new Vector3f(0, 0, 0), new Vector3f(1f, 1f, 1f), ()->
    }

}
