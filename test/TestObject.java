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
        Mesh tower = renderer.linkMesh("/testobjects/tower.obj");
        tower.setMaterial(new Material("/testobjects/tower.png"));
        renderer.linkMesh(tower, () -> {
            setModelView(renderer);
            renderer.boink(getBounceDegree(), tower);
        });
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
