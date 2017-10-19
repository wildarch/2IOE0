package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import org.joml.Vector3f;

public class Bullet extends GameObject {
    private float speed;
    private int damage;
    private Enemy target;
    private Vector3f color;
    private boolean isDone = false; //When target is hit

    public Bullet(float speed, int damage, Enemy target, AbstractTower source) {
        this.speed = speed;
        this.damage = damage;
        this.target = target;
        this.color = new Vector3f(0.5f, 0, 0.5f);
        setPosition(source.getPosition().add(0f, 1f, 0f));
    }

    /**
     * Copies the bullet, but with a different target
     * @param b
     * @param target
     */
    public Bullet(Bullet b, Enemy target) {
        this.speed = b.speed;
        this.damage = b.damage;
        this.target = target;
        this.color = new Vector3f(0.5f, 0, 0.5f);
        setPosition(b.getPosition());
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
            setRotation(direction);
            move(direction.mul(speed));
        }
    }

    public boolean isDone() {
        return isDone;
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public void renderInit(Renderer renderer) {
        renderer.linkMesh("b4.obj", () -> {
            setModelView(renderer);
            renderer.ambientLight(color);
            renderer.noDirectionalLight();
        });
    }
}
