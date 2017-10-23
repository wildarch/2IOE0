package nl.tue.c2IOE0.group5.engine.objects;

import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Texture;
import org.joml.Vector3f;

/**
 * @author Jorren Hendriks.
 */
public class Skybox extends GameObject {

    private String model;
    private Texture texture;
    private Material material;

    public Skybox(String objModel, String textureFile) {
        super();
        this.model = objModel;
        this.texture = new Texture(textureFile);
        this.material = new Material(this.texture);
    }

    public void setPosition(Camera camera) {
        Vector3f pos = camera.getPosition();
        setPosition(pos.x, pos.y, pos.z);
    }

    @Override
    public void renderInit(Renderer renderer) {
        InstancedMesh mesh = renderer.linkMesh(model, () -> {
            setModelView(renderer);
            renderer.drawSkybox();
        });

        mesh.getMesh().setMaterial(material);
    }

    @Override
    public void update() {
        // I'm a lazy motherfucker
    }
}
