package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.OBJLoader;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Random;

public class Bullet extends GameObject {
    private float speed;
    private int damage;
    private Enemy target;
    private Timer loopTimer;
    private long timeToChange;
    private Mesh mesh;
    private Vector3f color;
    private boolean isDone = false; //When target is hit
    private Random r = new Random();

    public Bullet(float speed, int damage, Enemy target, AbstractTower source, Timer loopTimer) {
        this.speed = speed;
        this.damage = damage;
        this.target = target;
        this.loopTimer = loopTimer;
        changeColor();
        setMesh();
        setPosition(source.getPosition().add(0f, 1f, 0f));
    }

    private void setMesh() {
        try {
            Mesh m = OBJLoader.loadMesh("/cube_10mm_10mm.obj");
            this.mesh = m;
            m.setMaterial(new Material("/square.png"));
            setScale(0.005f);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load cube model");
        }
    }

    private void move() {
        Vector3f position = this.getPosition();
        Vector3f targetPosition = target.getPosition();
        float distance = position.distance(targetPosition);
        if (distance <= speed) {
            target.getDamage(damage);
            isDone = true; //target is hit and it should be removed
        } else {
            Vector3f direction = targetPosition.sub(position);
            direction.normalize();
            move(direction.mul(speed));
        }
    }

    public boolean isDone() {
        return isDone;
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);
        renderer.ambientLight(color, () ->
                renderer.drawHealthBolletje(() -> mesh.draw(renderer))
        );
    }

    public void changeColor() {
        color = new Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat());
    }

    @Override
    public void update() {
        if (timeToChange < loopTimer.getLoopTime()) {
            changeColor();
            timeToChange = loopTimer.getLoopTime() + 1;
        }
        move();
    }
}
