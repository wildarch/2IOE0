package nl.tue.c2IOE0.group5.towers;

public abstract class AbstractTower {

    private int range;
    private int level;
    private int maxLevel;
    private int damagePerAttack;
    private int attacksPerSecond;

    public AbstractTower(int range, int maxLevel, int damagePerAttack, int attacksPerSecond) {
        this.range = range;
        this.maxLevel = maxLevel;
        this.damagePerAttack = damagePerAttack;
        this.attacksPerSecond = attacksPerSecond;
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
}
