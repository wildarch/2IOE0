package nl.tue.c2IOE0.group5.engine.objects;

import nl.tue.c2IOE0.group5.engine.rendering.*;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Texture;
import org.joml.Vector3f;

import java.io.IOException;

/**
 * @author Jorren Hendriks.
 */
public class Skybox extends GameObject {

    private String model;
    private Texture texture;
    private Material material;

    public Skybox(String objModel, String textureFile) throws Exception {
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
    public Skybox init(Renderer renderer) {
        try {
            Mesh mesh = renderer.linkMesh(model, (m) -> {
                setModelView(renderer);
                renderer.drawSkybox(m::draw);
            }, (m) -> {});

            mesh.setMaterial(material);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }
}
