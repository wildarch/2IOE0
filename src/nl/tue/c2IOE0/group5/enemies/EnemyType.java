package nl.tue.c2IOE0.group5.enemies;

import java.util.Random;

/**
 * TowerDefence
 * Created by s154796 on 5-10-2017.
 * culled by Geert van ieperen
 */
public enum EnemyType {
    BASIC(10),
    WALKER(5),
    DRILL(30);

    private final int weight;

    /**
     * @param pickWeight an enemy of weight 6 will be picked twice as much as one with weight 3
     */
    EnemyType(int pickWeight) {
        this.weight = pickWeight;
    }

    public static int getSize(){
        return EnemyType.values().length;
    }

    /**
     * select a random enemy based on its weight value.
     * @param generator a random generator object
     * @return random enemy
     */
    public static EnemyType getRandomEnemy(Random generator) {
        int totalWeight = 0;
        for (EnemyType e : values()) {
            totalWeight += e.weight;
        }
        int selection = generator.nextInt(totalWeight);
        for (EnemyType e : values()) {
            selection -= e.weight;
            if (selection < 0) return e;
        }
        return BASIC;
    }
}
