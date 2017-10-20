package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.objects.PositionInterpolator;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import org.joml.Vector3f;

public class Bullet extends GameObject {
    private float speed;
    private int damage;
    private Enemy target;
    private Vector3f color;
    private Renderer renderer;
    private InstancedMesh iMesh;
    protected Timer loopTimer;
    protected Timer renderTimer;
    private PositionInterpolator interpolator;
    private boolean isDone = false; //When target is hit

    public Bullet(float speed, int damage, Enemy target, AbstractTower source, Timer loopTimer, Timer renderTimer) {
        this.speed = speed;
        this.damage = damage;
        this.target = target;
        this.color = new Vector3f(0.5f, 0, 0.5f);
        setPosition(source.getPosition().add(0f, 1f, 0f));
        this.interpolator = new PositionInterpolator(this, this.speed);
        this.loopTimer = loopTimer;
        this.renderTimer = renderTimer;
    }

    public boolean isDone() {
        return isDone;
    }

    @Override
    public void update() {
        Vector3f targetPosition = target.getPosition();
        interpolator.setTarget(targetPosition, loopTimer.getLoopTime());
        boolean targetReached = interpolator.update(loopTimer.getLoopTime());
        if (this.getPosition().distance(target.getPosition()) < 0.01f || target.isDead()) { //targetReached doesn't seem to work as I expect it to work
            target.getDamage(damage);
            isDone = true; //target is hit and this bullet should be removed
            renderer.unlinkMesh(iMesh); //stop drawing this bullet
            return;
        }
        Vector3f direction = targetPosition.sub(this.getPosition()).normalize();
        setRotation(direction);
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(0.05f);
        Mesh bullet = renderer.linkMesh("/b4.obj");
        bullet.setMaterial(new Material("/square.png"));
        iMesh = renderer.linkMesh(bullet, () -> {
            setModelView(renderer);
            renderer.ambientLight(color);
            renderer.noDirectionalLight();
            interpolator.draw(renderTimer.getElapsedTime());
        });
        this.renderer = renderer;
    }
}
