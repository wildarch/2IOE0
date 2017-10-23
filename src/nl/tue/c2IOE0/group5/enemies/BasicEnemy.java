package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.List;

public class BasicEnemy extends Enemy {

    private static final float SPEED = 0.4f;
    private static final long ATTACKSPEED = 500;


    public BasicEnemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider,
                      Vector2i initialPosition, List<Vector2i> targetPositions, int maxHealth, QLearner qlearner) {
        super(loopTimer, renderTimer, gridProvider, initialPosition, targetPositions, maxHealth, SPEED, ATTACKSPEED, qlearner);
    }

    @Override
    protected void onDie() {
        renderer.unlinkMesh(iMeshBody);
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(0.05f);
        Mesh body = renderer.linkMesh("/models/enemies/basicEnemy/body.obj");
        body.setMaterial(new Material("/orange.png"));
        iMeshBody = renderer.linkMesh(body, () -> {
            setModelView(renderer);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });
        this.renderer = renderer;
    }
}
