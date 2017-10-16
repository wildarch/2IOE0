package nl.tue.c2IOE0.group5.engine.rendering;

/**
 * @author Jorren
 */
public class InstancedMesh {

    private Mesh mesh;                      // reference to source;
    private Runnable render;                // render normally

    InstancedMesh(Mesh mesh, Runnable render) {
        this.mesh = mesh;
        this.render = render;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Runnable getRender() {
        return render;
    }
}
