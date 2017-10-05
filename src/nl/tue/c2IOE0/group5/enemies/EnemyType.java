package nl.tue.c2IOE0.group5.enemies;

/**
 * TowerDefence
 * Created by s154796 on 5-10-2017.
 */
public enum EnemyType {
    DROID(0),
    TANK(1),
    ARCHER(2),
    SHIELD(3),
    TOWER_DESTROYER(4);

    private final int value;

    EnemyType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public static int getSize(){
        return EnemyType.values().length;
    }
}
