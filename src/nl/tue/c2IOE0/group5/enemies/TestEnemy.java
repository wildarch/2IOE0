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

public class TestEnemy extends Enemy {
    private float t = 0;
    private Timer loopTimer;
    private Vector3f targetPosition;
    private long timeToDie;


    public TestEnemy(Mesh mesh, Timer loopTimer, Vector3f initialPosition, Vector3f targetPosition) {
        setMesh(mesh);

        this.loopTimer = loopTimer;
        this.targetPosition = targetPosition;
        setPosition(initialPosition);
        setScale(0.1f);
        timeToDie = loopTimer.getLoopTime() + 7000;
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
        super.draw(window, renderer);
        float step = loopTimer.getElapsedTime() / 10000f;
        Vector3f offset = new Vector3f(0, 0, 0);
        targetPosition.toImmutable().sub(getPosition().toImmutable(), offset);
        if (offset.length() > 0.001f) {
            offset = offset.normalize().mul(step);
            move(offset);
        }
    }
}
