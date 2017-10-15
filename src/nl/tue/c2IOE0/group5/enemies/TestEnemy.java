package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.PositionInterpolator;
import nl.tue.c2IOE0.group5.engine.rendering.*;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Texture;
import nl.tue.c2IOE0.group5.providers.Cell;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class TestEnemy extends Enemy {
    private static final float SPEED = 1.5f;
    private Timer loopTimer;
    private List<Vector2i> targetPositions;
    private long timeToDoDamage;
    private PositionInterpolator interpolator;


    public TestEnemy(Mesh mesh, Timer loopTimer, GridProvider gridProvider,
                     Vector2i initialPosition, List<Vector2i> targetPositions, int maxHealth) {
        super(gridProvider, maxHealth);
        setMesh(mesh);

        this.loopTimer = loopTimer;
        this.targetPositions = new ArrayList<>(targetPositions);
        setPosition(gridProvider.getCell(initialPosition).getPosition());
        this.interpolator = new PositionInterpolator(this, SPEED);
        System.out.println("Position: " + getPosition());

        setScale(0.01f);
    }

    @Override
    public void update() {
        super.update();
        boolean targetReached = interpolator.update(loopTimer.getLoopTime());
        if(targetReached) {
            if(targetPositions.size() > 0) {
                targetPositions.remove(0);
            }
        }
        if(targetPositions.isEmpty()) return;
        Cell targetCell = gridProvider.getCell(targetPositions.get(0));
        AbstractTower tower = targetCell.getTower();
        if (tower == null && targetReached) {
            System.out.println("New target: " + targetCell.getPosition());
            interpolator.setTarget(targetCell.getPosition().add(0, 0.5f, 0f), loopTimer.getLoopTime());
        }
        else if (tower != null) {
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

        interpolator.draw(loopTimer.getElapsedTime());
    }
}
