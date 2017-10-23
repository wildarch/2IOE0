package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.TowerProvider;
import org.joml.Vector3f;

/**
 * @Author Yoeri Poels
 */
public class CannonTower extends AbstractTower {


    private static final int RANGE = 3;
    private static final int MAX_LEVEL = 1;
    private static final int MAX_HEALTH = 50;
    private static final int ATTACK_TIME = 2000;
    private static final float BULLET_SPEED = 3f;
    private static final int BULLET_DAMAGE = 5;
    private static final int PRICE = 100;

    public static MetaData metadata = new MetaData();
    static {
         metadata.name = "Cannon";
         metadata.icon = "/hud/cannontower.png";
         metadata.price = PRICE;
    }


    private Vector3f cannonRotation = new Vector3f(0f, 0f, 0f);

    private Renderer renderer;
    private InstancedMesh iBaseMesh;
    private InstancedMesh iCannonMesh;

    public CannonTower(TowerProvider towerProvider) {
        super(RANGE, MAX_LEVEL, MAX_HEALTH, ATTACK_TIME, BULLET_SPEED, BULLET_DAMAGE, 1.3f, 0.5f, towerProvider);
    }

    @Override
    protected void attack(Enemy e) {
        super.attack(e);
        //calculate rotation of the cannon, based on the angle between the position of the tower / the enemy
        cannonRotation.y = 180f + (float) Math.toDegrees(Math.atan2((e.getPosition().z - getPosition().z), (e.getPosition().x - getPosition().x)));
    }

    @Override
    public TowerType getType() {
        return TowerType.CANNON;
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(1f);
        iBaseMesh = renderer.linkMesh("/models/towers/cannontower/BASE.obj", () -> setModelView(renderer));
        iCannonMesh = renderer.linkMesh("/models/towers/cannontower/CANNON.obj", () -> setModelView(renderer, new Vector3f(0f, 0.269f, 0f), cannonRotation));


        this.renderer = renderer;
    }

    @Override
    protected void onDie() {
        //renderer.unlinkMesh(mesh, render, shadowRender);
        renderer.unlinkMesh(iBaseMesh);
        renderer.unlinkMesh(iCannonMesh);
    }
}