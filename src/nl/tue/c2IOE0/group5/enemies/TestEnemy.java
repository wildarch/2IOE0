package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.PositionInterpolator;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.providers.Cell;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class TestEnemy extends Enemy {
    private static final float SPEED = 1.5f;
    private static final long ATTACKSPEED = 500;
    private Renderer renderer;
    private InstancedMesh cube;


    public TestEnemy(Timer loopTimer, GridProvider gridProvider,
                     Vector2i initialPosition, List<Vector2i> targetPositions, int maxHealth) {
        super(loopTimer, gridProvider, initialPosition, targetPositions, maxHealth, SPEED, ATTACKSPEED);
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(0.25f);

        cube = renderer.linkMesh("/cube.obj", () -> {
            setModelView(renderer);
            renderer.ambientLight(new Vector3f(0f, 0f,1f ));
            if(!attacking) interpolator.draw(loopTimer.getElapsedTime());
        });
        this.renderer = renderer;
    }

    @Override
    protected void onDie() {
        if(renderer != null) renderer.unlinkMesh(cube);
    }
}
