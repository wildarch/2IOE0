package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.providers.Cell;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import org.joml.Vector2ic;

import java.util.ArrayList;
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
    private long timeToDoDamage;
    private Timer loopTimer;

    private Mesh mesh;
    private Cell cell;


    public AbstractTower(int range, int maxLevel, int maxHealth, EnemyProvider enemyProvider, Timer loopTimer) {
        this.range = range;
        this.maxLevel = maxLevel;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.mesh = mesh;
        this.enemyProvider = enemyProvider;
        this.loopTimer = loopTimer;

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
            health = 0;
            cell.destroyTower();
        }
    }

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
        System.out.println("Attack! (tower)");
        e.getDamage(1000);
    }

    private boolean isInRange(Enemy e) {
        Cell enemyCell = e.getCurrentCell();
        return this.cell.getGridPosition().distance(enemyCell.getGridPosition()) <= range;
    }

    @Override
    public void update() {
        if (timeToDoDamage < loopTimer.getLoopTime()) {
            attack();
            timeToDoDamage = loopTimer.getLoopTime() + 500;
        }
    }

    protected void setMesh(Mesh m) {
        mesh = m;
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);
        mesh.draw(renderer);
    }
}
