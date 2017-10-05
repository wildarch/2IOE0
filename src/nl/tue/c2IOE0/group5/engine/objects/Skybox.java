package nl.tue.c2IOE0.group5.engine.objects;

import nl.tue.c2IOE0.group5.engine.rendering.*;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Texture;
import org.joml.Vector3f;

/**
 * @author Jorren Hendriks.
 */
public class Skybox extends GameObject {

    private Mesh mesh;

    public Skybox(String objModel, String textureFile) throws Exception {
        super();
        mesh = OBJLoader.loadMesh(objModel);
        Texture texture = new Texture(textureFile);
        mesh.setMaterial(new Material(texture, 0.0f));
    }

    public void update(Camera camera) {
        Vector3f pos = camera.getPosition();
        setPosition(pos.x, pos.y, pos.z);
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);

        mesh.draw(renderer);
    }

}
