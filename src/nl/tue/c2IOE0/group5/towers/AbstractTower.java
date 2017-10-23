package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.controllers.PlayerController;
import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.*;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractTower extends GameObject {

    private int range;
    private int level = 1;
    private final int maxLevel;
    private final int maxHealth;
    private int health;
    private EnemyProvider enemyProvider;
    private BulletProvider bulletProvider;
    private GridProvider gridProvider;
    private long timeToDoDamage;
    private Timer loopTimer;
    private Timer renderTimer;
    private HealthBolletje healthBolletje;
    private final int attackTime;
    private final float bulletSpeed;
    private final int bulletDamage;
    private float healthHeight;
    private float bulletOffset;

    private Mesh mesh;
    private Cell cell;
    private Renderer renderer;


    public AbstractTower(int range, int maxLevel, int maxHealth, int attackTime, float bulletSpeed, int bulletDamage, float healthHeight, float bulletOffset, TowerProvider towerProvider) {
        this.range = range;
        this.maxLevel = maxLevel;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.enemyProvider = towerProvider.enemyProvider;
        this.bulletProvider = towerProvider.bulletProvider;
        this.gridProvider = towerProvider.gridProvider;
        this.loopTimer = towerProvider.loopTimer;
        this.renderTimer = towerProvider.renderTimer;
        this.renderer = enemyProvider.getRenderer();
        this.healthBolletje = new HealthBolletje(this).init(renderer);
        this.attackTime = attackTime;
        this.bulletSpeed = bulletSpeed;
        this.bulletDamage = bulletDamage;
        this.healthHeight = healthHeight;
        this.bulletOffset = bulletOffset;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);

        healthBolletje.setPosition(x, y + healthHeight, z);
    }

    @Override
    public void setPosition(Vector3f p) {
        super.setPosition(p);

        healthBolletje.setPosition(p.add(0, healthHeight, 0));
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }
    public Cell getCell() {return this.cell;}

    /**
     * Level a tower up
     * @return whether or not the level up is possible (not leveling higher than the maximum level)
     */
    public boolean levelUp() {
        if (level < maxLevel) {
            level = level + 1;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the range of this tower
     * @return
     */
    public int getRange() {
        return this.range;
    }

    /**
     * Reduce health by given damage
     * @param damage Damage to incur
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            die();
        }
    }

    private void die() {
        health = 0;
        gridProvider.destroyTower(cell.getGridPosition().x(), cell.getGridPosition().y());
        healthBolletje.stopDrawing();
        onDie();
    }

    protected abstract void onDie();

    /**
     * @return Remaining health
     */
    public int getHealth() {
        return health;
    }

    public boolean isDead() {
        return health == 0;
    }

    private void attack() {
        List<Enemy> inRange = enemyProvider.getEnemies().stream().filter(this::isInRange).collect(Collectors.toList());
        Optional<Enemy> e = inRange.stream()
                .min(Comparator.comparingDouble(a -> this.cell.getGridPosition().distance(a.getCurrentCell().getGridPosition())));
        if (e.isPresent()) {
            Enemy closest = e.get();
            attack(closest);
        }
    }

    protected void attack(Enemy e) {
        Bullet b = new Bullet(bulletSpeed, bulletDamage, bulletOffset, e, this, loopTimer, renderTimer).init(renderer);
        bulletProvider.addBullet(b);
    }

    private boolean isInRange(Enemy e) {
        Cell enemyCell = e.getCurrentCell();
        return this.cell.getGridPosition().distance(enemyCell.getGridPosition()) <= range;
    }

    @Override
    public void update() {
        if (timeToDoDamage < loopTimer.getLoopTime()) {
            attack();
            timeToDoDamage = loopTimer.getLoopTime() + attackTime;
        }
        this.healthBolletje.update();
    }

    protected void setMesh(Mesh m) {
        mesh = m;
    }

    protected class HealthBolletje extends GameObject {

        final float MAX_SIZE = 0.15f;
        final float MIN_SIZE = 0.075f;
        private AbstractTower tower;
        private Vector3f color = new Vector3f(0f, 1f, 0f);
        private InstancedMesh iMesh;

        public HealthBolletje(AbstractTower t) {
            this.tower = t;
            this.setScale(MAX_SIZE);
        }

        @Override
        public void update() {
            float percentage = getPercentage();
            color =  new Vector3f(1-percentage, percentage, 0f);
            this.setScale(getPercentage() * (MAX_SIZE-MIN_SIZE) + MIN_SIZE);
        }

        @Override
        public void renderInit(Renderer renderer) {
            Mesh mesh = renderer.linkMesh("/health.obj");
            mesh.setMaterial(new Material("/square.png"));
            iMesh = renderer.linkMesh(mesh, () -> {
                setModelView(renderer);
                renderer.ambientLight(color);
                renderer.noDirectionalLight();
            });
        }

        private float getPercentage() {
            return (float) tower.health / (float) tower.maxHealth;
        }

        public void stopDrawing() {
            if(renderer != null)
                renderer.unlinkMesh(iMesh);
        }
    }

    public static class MetaData {
        public String name;
        public String icon;
        public int price;
    }
}
