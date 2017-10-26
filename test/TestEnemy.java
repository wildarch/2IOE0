import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.controllers.PlayerController;
import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.enemies.EnemyType;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.joml.Vector2i;

import java.util.List;

public class TestEnemy extends Enemy {
    private static final float SPEED = 0.5f;
    private static final long ATTACKSPEED = 500;
    private final static int DAMAGE = 10;

    public TestEnemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider,
                     Vector2i initialPosition, List<Vector2i> targetPositions, int maxHealth, QLearner qlearner, PlayerController playerController) {
        super(loopTimer, renderTimer, gridProvider, initialPosition, targetPositions, maxHealth, DAMAGE, SPEED, ATTACKSPEED, qlearner, playerController, 100);
    }


    @Override
    public EnemyType getType() {
        return EnemyType.BASIC;
    }

    @Override
    protected void onDie() {
        if(renderer != null) renderer.unlinkMesh(iMeshBody);
    }
}
