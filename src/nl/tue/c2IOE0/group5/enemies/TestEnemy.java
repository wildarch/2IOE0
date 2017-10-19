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
    private static final float SPEED = 0.5f;
    private static final long ATTACKSPEED = 500;


    public TestEnemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider,
                     Vector2i initialPosition, List<Vector2i> targetPositions, int maxHealth) {
        super(loopTimer, renderTimer, gridProvider, initialPosition, targetPositions, maxHealth, SPEED, ATTACKSPEED);
    }


    @Override
    protected void onDie() {

    }
}
