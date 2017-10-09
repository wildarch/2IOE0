package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.rendering.*;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Texture;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

public class TestEnemy extends Enemy {
    private float t = 0;
    private Timer loopTimer;
    private List<Vector3f> targetPositions;
    private long timeToDie;


    public TestEnemy(Mesh mesh, Timer loopTimer, Vector3f initialPosition, List<Vector3f> targetPositions) {
        setMesh(mesh);

        this.loopTimer = loopTimer;
        this.targetPositions = targetPositions;
        setPosition(initialPosition);
        setScale(0.01f);
        timeToDie = loopTimer.getLoopTime() + 20000;
    }

    @Override
    public void update() {
        super.update();
        if(loopTimer.getLoopTime() > timeToDie) {
            die();
            System.out.println("Goodbye!");
        }
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        if(targetPositions.size() == 0) {
            return;
        }
        super.draw(window, renderer);
        float step = loopTimer.getElapsedTime() / 1000f;
        Vector3f offset = new Vector3f(0, 0, 0);
        targetPositions.get(0).toImmutable().sub(getPosition().toImmutable(), offset);
        if (offset.length() > 0.01f) {
            offset = offset.normalize().mul(step);
            move(offset);
        }
        else {
            targetPositions.remove(0);
        }
    }
}
