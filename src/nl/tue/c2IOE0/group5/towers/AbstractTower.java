package nl.tue.c2IOE0.group5.towers;

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

import static nl.tue.c2IOE0.group5.providers.GridProvider.manDist;

public abstract class AbstractTower extends GameObject {

    private static final int FALL_TIME = 500; // milliseconds
    private static final Vector3f FALL_OFFSET = new Vector3f(0, 2f, 0);
    private static final int BOUNCE_TIME = 500; // milliseconds

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
    private HealthBolletje healthBolletje;
    private final int attackTime;
    private final float bulletSpeed;
    private final int bulletDamage;
    private float healthHeight;
    private float bulletOffset;

    private Mesh mesh;
    private Cell cell;
    private Renderer renderer;

    private Timer renderTimer;
    private long startTime;

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
        this.renderer = towerProvider.getRenderer();
        this.healthBolletje = new HealthBolletje(this).init(renderer);
        this.attackTime = attackTime;
        this.bulletSpeed = bulletSpeed;
        this.bulletDamage = bulletDamage;
        this.healthHeight = healthHeight;
        this.bulletOffset = bulletOffset;
        startTime = renderTimer.getTime();
    }

    /**
     * @param towertype a tower class
     * @return the general metadata of this tower, as defined in {@link MetaData}
     */
    public static MetaData getMetaData(Class<? extends AbstractTower> towertype) {
        MetaData metaData;
        try {
            Field meta = towertype.getField("metadata");
            metaData = (MetaData) meta.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalStateException("Tower " + towertype.getName() +
                    " does not have a field metadata, or it is not marked static public");
        }
        return metaData;
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
        takeDamage((double)damage);
    }

    public void takeDamage(double damage) {
        health -= damage;
        if (health <= 0) {
            die();
        }
    }

    public void die() {
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
                .min(Comparator.comparingDouble(a -> manDist(a.getCurrentCell(), this.cell)));
        if (e.isPresent()) {
            Enemy closest = e.get();
            attack(closest);
        }
    }

    public abstract int getMaxHealth();

    protected void attack(Enemy e) {
        Bullet b = new Bullet(bulletSpeed, bulletDamage, bulletOffset, e, this, loopTimer, renderTimer).init(renderer);
        bulletProvider.addBullet(b);
    }

    private boolean isInRange(Enemy e) {
        Cell enemyCell = e.getCurrentCell();
        return manDist(enemyCell, this.cell) <= range;
    }

    @Override
    public void update() {
        if (timeToDoDamage < loopTimer.getTime()) {
            attack();
            timeToDoDamage = loopTimer.getTime() + attackTime;
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
            Mesh mesh = renderer.linkMesh("/models/items/health.obj");
            mesh.setMaterial(new Material("/general/square.png"));
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

    public abstract TowerType getType();

    public abstract int getPrice();

    protected Vector3f getPositionOffset() {
        return getPositionOffset(renderTimer.getTime() - startTime);
    }

    protected float getBounceDegree() {
        return getBounceDegree(renderTimer.getTime() - startTime - FALL_TIME);
    }

    public static Vector3f getPositionOffset(float deltaTime) {
        float r = deltaTime / FALL_TIME;
        if(r > 1) {
            return new Vector3f(0);
        }
        r *= r;
        return new Vector3f(FALL_OFFSET).mul(1-r);
    }

    public static float getBounceDegree(float deltaTime) {
        if (deltaTime < 0) return 0;
        float r = deltaTime / BOUNCE_TIME;
        if (r > 1) {
            return 0;
        }
        r *= Math.PI;
        r = (float) Math.sin(r);
        return r;
    }
}
