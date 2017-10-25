package nl.tue.c2IOE0.group5.enemies;

/**
 * TowerDefence
 * Created by s154796 on 5-10-2017.
 * culled by Geert van ieperen
 */
public enum EnemyType {
    BASIC,
    WALKER,
    DRILL;

    public static int getSize(){
        return EnemyType.values().length;
    }
}
