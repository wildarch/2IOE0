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
        startTime = renderTimer.getTime();
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

    Vector3f getPositionOffset() {
        float deltaTime = renderTimer.getTime() - startTime;
        float r = deltaTime / FALL_TIME;
        if(r > 1) {
            return new Vector3f(0);
        }
        r *= r;
        Vector3f off = new Vector3f(FALL_OFFSET).mul(1-r);
        return off;
    }

    float getBounceDegree() {
        float deltaTime = renderTimer.getTime() - startTime - FALL_TIME;
        if (deltaTime < 0) return 0;
        float r = deltaTime / BOUNCE_TIME;
        if (r > 1) {
            return 0;
        }
        r *= Math.PI;
        r = (float) Math.sin(r);
        return r;
    }

    public void destroy() {
        renderer.unlinkMesh(iMesh);
    }
}
