package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.providers.Cell;

public abstract class AbstractTower extends GameObject {

    private int range;
    private int level;
    private final int maxLevel;
    private int damagePerAttack;
    private int attacksPerSecond;
    private final int maxHealth;
    private int health;

    private Mesh mesh;
    private Cell cell;


    public AbstractTower(int range, int maxLevel, int damagePerAttack, int attacksPerSecond, int maxHealth) {
        this.range = range;
        this.maxLevel = maxLevel;
        this.damagePerAttack = damagePerAttack;
        this.attacksPerSecond = attacksPerSecond;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.mesh = mesh;
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

    public int getDamage() {
        //TODO: calculate a specific damage value
        return 1;
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

    protected void setMesh(Mesh m) {
        mesh = m;
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);
        mesh.draw(renderer);
    }
}
