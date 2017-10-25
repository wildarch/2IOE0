package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import org.joml.Vector3f;

/**
 * @author Yoeri Poels
 * Gameobject that appears between 2 adjacent WallTowers to connect them.
 */

public class TowerConnection extends GameObject {


    private InstancedMesh iMesh;
    private Renderer renderer;
    //bounce related code
    private static final int FALL_TIME = 500; // milliseconds
    private static final Vector3f FALL_OFFSET = new Vector3f(0, 2f, 0);
    private static final int BOUNCE_TIME = 500; // milliseconds
    private Timer renderTimer;
    private long startTime;

    public TowerConnection(Vector3f position, float rotation, Renderer renderer, Timer renderTimer) {
        this.renderer = renderer;
        this.renderTimer = renderTimer;
        setPosition(position);
        setRotation(0, rotation, 0);
        setScale(1f);
        startTime = renderTimer.getTime() + 1000;
    }

    @Override
    public void update() {    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(0.95f);
        this.renderer = renderer;
        Mesh wallMesh = renderer.linkMesh("/models/towers/walltower/wall.obj");
        iMesh = renderer.linkMesh(wallMesh, () -> {
            setModelView(renderer, getPositionOffset());
            renderer.boink(getBounceDegree(), wallMesh);
        });
    }

    private Vector3f getPositionOffset() {
        float deltaTime = renderTimer.getTime() - startTime;
        return AbstractTower.getPositionOffset(deltaTime);
    }

    private float getBounceDegree() {
        float deltaTime = renderTimer.getTime() - startTime - FALL_TIME;
        return AbstractTower.getBounceDegree(deltaTime);
    }

    public void destroy() {
        if(renderer == null) return;
        renderer.unlinkMesh(iMesh);
    }
}
