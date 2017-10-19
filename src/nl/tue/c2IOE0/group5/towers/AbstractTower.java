package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.OBJLoader;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.BulletProvider;
import nl.tue.c2IOE0.group5.providers.Cell;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractTower extends GameObject {

    private int range;
    private int level;
    private final int maxLevel;
    private final int maxHealth;
    private int health;
    private EnemyProvider enemyProvider;
    private BulletProvider bulletProvider;
    private long timeToDoDamage;
    private Timer loopTimer;
    private HealthBolletje healthBolletje;
    private final int attackTime;
    private final float bulletSpeed;
    private final int bulletDamage;

    private Mesh mesh;
    private Cell cell;


    public AbstractTower(int range, int maxLevel, int maxHealth, int attackTime, float bulletSpeed, int bulletDamage,
                         EnemyProvider enemyProvider, BulletProvider bulletProvider, Timer loopTimer) {
        this.range = range;
        this.maxLevel = maxLevel;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.enemyProvider = enemyProvider;
        this.bulletProvider = bulletProvider;
        this.loopTimer = loopTimer;
        this.healthBolletje = new HealthBolletje(this);
        this.attackTime = attackTime;
        this.bulletSpeed = bulletSpeed;
        this.bulletDamage = bulletDamage;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

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
        cell.destroyTower();
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

    private void attack(Enemy e) {
        Bullet b = new Bullet(bulletSpeed, bulletDamage, e, this);
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

    /*
    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);
        mesh.draw(renderer);
        this.healthBolletje.draw(window, renderer);
    }
    */

    protected class HealthBolletje extends GameObject {

        final float MAX_SIZE = 0.15f;
        final float MIN_SIZE = 0.075f;
        private AbstractTower tower;
        private Mesh mesh;
        private Vector3f color;

        public HealthBolletje(AbstractTower t) {
            this.tower = t;
            setMesh();
            color =  new Vector3f(0f, 1f, 0f);
        }

        /*
        @Override
        public void draw(Window window, Renderer renderer) {
            super.draw(window, renderer);
            renderer.ambientLight(color, () ->
                    renderer.noDirectionalLight(() -> mesh.draw(renderer))
            );
        }
        */

        @Override
        public void update() {
            float percentage = (float)tower.health / (float)tower.maxHealth;
            this.setScale(percentage * (MAX_SIZE-MIN_SIZE) + MIN_SIZE);
            color =  new Vector3f(1-percentage, percentage, 0f);
            this.setPosition(tower.getPosition().add(new Vector3f(0, 2.5f, 0)));
        }

        private void setMesh() {
            try {
                Mesh m = OBJLoader.loadMesh("/health.obj");
                this.mesh = m;
                m.setMaterial(new Material("/square.png"));
                setScale(10f);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load Tower model");
            }
        }

        @Override
        public GameObject init(Renderer renderer) {
            setPosition(tower.getPosition().add(new Vector3f(0, 2.5f, 0)));
            renderer.linkMesh("/health.obj", () -> {
                        setModelView(renderer);
                    });
            return this;
        }
    }
}
