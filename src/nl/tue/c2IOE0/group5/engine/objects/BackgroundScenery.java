package nl.tue.c2IOE0.group5.engine.objects;

import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import org.joml.Vector3f;

/**
 * @author Yoeri Poels
 */
public class BackgroundScenery extends GameObject {

    public BackgroundScenery(float gridSize) {
        super();
        setPosition(gridSize/2f, 0, gridSize/2f);
    }


    @Override
    public void renderInit(Renderer renderer) {
        Mesh island = renderer.linkMesh("/models/background/island.obj");
        Material islandMaterial = new Material("/models/background/island.png");
        island.setMaterial(islandMaterial);

        Mesh water = renderer.linkMesh("/models/background/water.obj");
        Material waterMaterial = new Material("/models/background/water.png");
        water.setMaterial(waterMaterial);
        waterMaterial.setTransparency(true);

        renderer.linkMesh(island, () -> {
            setModelView(renderer);
            renderer.drawNoShadow();
        });
        renderer.linkMesh(water, () -> {
            setModelView(renderer, new Vector3f(0f, 0.85f, 0f));
            renderer.drawNoShadow();
            renderer.drawBlackAsAlpha();
        });
    }

    @Override
    public void update() {
        //nothing happens on updates
    }
}
