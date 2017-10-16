package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.PositionInterpolator;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.providers.Cell;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

public class TestEnemy extends Enemy {
    private static final float SPEED = 1.5f;
    private Timer loopTimer;
    private List<Vector2i> targetPositions;
    private long timeToDoDamage;
    private PositionInterpolator interpolator;
    private boolean attacking = false;


    public TestEnemy(Mesh mesh, Timer loopTimer, GridProvider gridProvider,
                     Vector2i initialPosition, List<Vector2i> targetPositions, int maxHealth) {
        super(gridProvider, maxHealth);
        setMesh(mesh);

        this.loopTimer = loopTimer;
        this.targetPositions = new ArrayList<>(targetPositions);
        setPosition(gridProvider.getCell(initialPosition).getPosition());
        this.interpolator = new PositionInterpolator(this, SPEED);

        setScale(0.01f);
    }

    @Override
    public void update() {
        super.update();

        if(targetPositions.isEmpty()) {
            return;
        }
        boolean targetReached = interpolator.update(loopTimer.getLoopTime());
        if(targetReached) {
            targetPositions.remove(0);
            if(targetPositions.isEmpty()) return;
        }
        Cell targetCell = gridProvider.getCell(targetPositions.get(0));
        AbstractTower tower = targetCell.getTower();
        Vector3f targetPosition = targetCell.getPosition().add(0, 0.5f, 0);
        if (targetReached || (attacking && tower == null)) {
            // Road is clear, move ahead
            attacking = false;
            interpolator.setTarget(targetPosition, loopTimer.getLoopTime());
        }
        else if(tower != null) {
            // Destroy the tower first
            attacking = true;
            doDamage(tower);
        }
    }

    private void doDamage(AbstractTower tower) {
        if (timeToDoDamage < loopTimer.getLoopTime()) {
            tower.takeDamage(1);
            timeToDoDamage = loopTimer.getLoopTime() + 500;
        }
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        renderer.ambientLight(new Vector3f(0f, 0f,1f ), () -> {
            super.draw(window, renderer);
        });

        if(!attacking) interpolator.draw(loopTimer.getElapsedTime());
    }
}
