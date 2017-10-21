package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.joml.Vector2i;

import java.util.List;

public class TestEnemy extends Enemy {
    private static final float SPEED = 0.5f;
    private static final long ATTACKSPEED = 500;


    public TestEnemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider,
                     Vector2i initialPosition, List<Vector2i> targetPositions, int maxHealth) {
        super(loopTimer, renderTimer, gridProvider, initialPosition, targetPositions, maxHealth, SPEED, ATTACKSPEED);
    }


    @Override
    protected void onDie() {
        if(renderer != null) renderer.unlinkMesh(iMeshBody);
    }
}
