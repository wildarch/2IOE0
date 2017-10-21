package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.objects.PositionInterpolator;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import org.joml.Vector2i;
import org.joml.Vector3f;

/**
 * @Author Yoeri Poels
 * Gameobject that appears between 2 adjacent WallTowers to connect them.
 */

public class TowerConnection extends GameObject {
    private InstancedMesh iMesh;
    private Renderer renderer;

    public TowerConnection(Vector3f position, float rotation, Renderer renderer) {
        this.renderer = renderer;
        setPosition(position);
        setRotation(0, rotation, 0);
        setScale(1f);
    }

    @Override
    public void update() {    }

    @Override
    public void renderInit(Renderer renderer) {
        this.renderer = renderer;
        Mesh mesh = renderer.linkMesh("/models/towers/walltower/wall.obj");
        mesh.setMaterial(new Material("/models/towers/walltower/wall.png"));
        iMesh = renderer.linkMesh(mesh, () -> setModelView(renderer));

    }

    public void destroy() {
        renderer.unlinkMesh(iMesh);
    }
}
