package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.rendering.*;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.IOException;
import java.text.NumberFormat;

public class TestEnemy extends Enemy {
    private float t = 0;
    private Timer loopTimer;
    private Vector3f targetPosition;


    public TestEnemy(Timer loopTimer, Vector3f targetPosition) {
        try {
            Mesh mesh = OBJLoader.loadMesh("/bunny.obj");
            mesh.setTexture(new Texture("/tower.png"));
            super.setMesh(mesh);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.loopTimer = loopTimer;
        this.targetPosition = targetPosition;
        setScale(20f);
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
            System.out.println(offset);
        }
    }
}
