package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.BulletProvider;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.util.Angle;
import org.joml.Vector3f;

/**
 * @Author Yoeri Poels
 */
public class CannonTower extends AbstractTower {

    private static final int RANGE = 5;
    private static final int MAX_LEVEL = 1;
    private static final int MAX_HEALTH = 20;

    private Vector3f cannonRotation = new Vector3f(0f, 0f, 0f);

    private Renderer renderer;
    private InstancedMesh iBaseMesh;
    private InstancedMesh iCannonMesh;

    public CannonTower(EnemyProvider enemyProvider, BulletProvider bulletProvider, Timer timer) {
        super(RANGE, MAX_LEVEL, MAX_HEALTH, 500, 0.2f, 1000, 1.3f, enemyProvider, bulletProvider, timer);
    }

    @Override
    protected void attack(Enemy e) {
        super.attack(e);
        //calculate rotation of the cannon, based on the angle between the position of the tower / the enemy
        cannonRotation.y = 180f + Angle.degf((float)Math.atan((e.getPosition().z - getPosition().z) / (e.getPosition().x - getPosition().x)));
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(1f);
        Mesh base = renderer.linkMesh("/models/towers/cannontower/base.obj");
        Mesh cannon = renderer.linkMesh("/models/towers/cannontower/cannon.obj");
        base.setMaterial(new Material("/models/towers/cannontower/base.png"));
        cannon.setMaterial(new Material("/models/towers/cannontower/cannon.png"));

        iBaseMesh = renderer.linkMesh(base, () -> setModelView(renderer));
        iCannonMesh = renderer.linkMesh(cannon, () -> setModelView(renderer, new Vector3f(0f, 0.269f, 0f), cannonRotation));


        this.renderer = renderer;
    }

    @Override
    protected void onDie() {
        //renderer.unlinkMesh(mesh, render, shadowRender);
        renderer.unlinkMesh(iBaseMesh);
        renderer.unlinkMesh(iCannonMesh);
    }
}