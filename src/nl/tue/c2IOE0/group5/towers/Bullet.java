package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.objects.PositionInterpolator;
import nl.tue.c2IOE0.group5.engine.rendering.*;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import org.joml.Vector3f;

public class Bullet extends GameObject implements Drawable {
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
    private Vector3f drawOffset = new Vector3f();

    public Bullet(float speed, int damage, float verticalOffset, Enemy target, AbstractTower source, Timer loopTimer, Timer renderTimer) {
        this.speed = speed;
        this.damage = damage;
        this.target = target;
        this.color = new Vector3f(0.5f, 0, 0.5f);
        setPosition(source.getPosition().add(0f, verticalOffset, 0f));
        this.interpolator = new PositionInterpolator(this, this.speed);
        this.loopTimer = loopTimer;
        this.renderTimer = renderTimer;
    }

    public boolean isDone() {
        return isDone;
    }

    @Override
    public void update() {
        Vector3f targetPosition = new Vector3f(target.getPosition());
        interpolator.setTarget(targetPosition);
        interpolator.update(loopTimer.getElapsedTime());
        //boolean targetReached = interpolator.update(loopTimer.getTime());                 //doesn't seem to work as I expect it to work
        boolean targetReached = this.getPosition().distance(target.getPosition()) < 0.1f;       //so using this method instead
        if (targetReached || target.isDead()) {
            target.getDamage(damage);
            isDone = true; //target is hit and this bullet should be removed
            renderer.unlinkMesh(iMesh); //stop drawing this bullet
            return;
        }
        this.setRotation(interpolator.getDirection());
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(0.05f);
        Mesh bullet = renderer.linkMesh("/models/items/bullet4.obj");
        bullet.setMaterial(new Material("/general/square.png"));
        iMesh = renderer.linkMesh(bullet, () -> {
            setModelView(renderer, drawOffset);
            renderer.ambientLight(color);
            renderer.noDirectionalLight();
        });
        this.renderer = renderer;
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        drawOffset = interpolator.getOffset(renderTimer.getTime() - loopTimer.getTime());
    }
}
