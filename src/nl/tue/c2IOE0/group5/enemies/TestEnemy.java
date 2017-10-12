package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.Timer;
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
    private Timer loopTimer;
    private List<Vector2i> targetPositions;
    private long timeToDie;
    private long timeToDoDamage;

    private GridProvider gridProvider;


    public TestEnemy(Mesh mesh, Timer loopTimer, GridProvider gridProvider,
                     Vector2i initialPosition, List<Vector2i> targetPositions) {
        setMesh(mesh);

        this.loopTimer = loopTimer;
        this.gridProvider = gridProvider;
        this.targetPositions = new ArrayList<>(targetPositions);
        setPosition(gridProvider.getCell(initialPosition).getPosition());
        setScale(0.01f);
        timeToDie = loopTimer.getLoopTime() + 15000;
    }

    @Override
    public void update() {
        super.update();
        if(targetPositions.size() > 0) {
            Cell targetCell = gridProvider.getCell(targetPositions.get(0));
            AbstractTower tower = targetCell.getTower();
            if (tower != null) {
                doDamage(tower);
            }
        }
        if(loopTimer.getLoopTime() > timeToDie) {
            die();
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

        if(targetPositions.size() == 0) {
            return;
        }

        Cell targetCell = gridProvider.getCell(targetPositions.get(0));
        AbstractTower tower = targetCell.getTower();
        if (tower == null) {
            // Cell empty, move towards it
            float step = loopTimer.getElapsedTime() / 1000f;
            Vector3f offset = targetCell.getPosition();
            offset.add(0f, 0.5f, 0f);
            offset.sub(getPosition().toImmutable());
            if (offset.length() > 0.01f) {
                offset = offset.normalize().mul(step);
                move(offset);
            }
            else {
                targetPositions.remove(0);
            }
        }
    }
}
