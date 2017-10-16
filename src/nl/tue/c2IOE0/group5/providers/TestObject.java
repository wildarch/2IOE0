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
        this.setScale(1f);
    }

    @Override
    public TestObject init(Renderer renderer) {
        try {
            Mesh tower = renderer.linkMesh("/tower1.obj", (mesh) -> {
                setModelView(renderer);
                setModelLightViewScene(renderer);
                renderer.boink(getBounceDegree(), mesh::draw, mesh, false);
            }, (mesh) -> {
                setModelLightView(renderer);
                renderer.boink(getBounceDegree(), mesh::draw, mesh, true);
            });
            tower.setMaterial(new Material("/tower1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    private float getBounceDegree() {
//        return 0;
        return (float) Math.sin(boinkyness) +1f;
    }

    public void boink() {
        // updateFluent private members here
        boinkyness = (boinkyness + 0.01f);
        //renderer.boink((float)Math.sin(boinkyness) +1f, new Vector3f(0, 0, 0), new Vector3f(1f, 1f, 1f), ()->
    }

}
