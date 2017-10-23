package nl.tue.c2IOE0.group5.towers;

/**
 * TowerDefence
 * Created by s154796 on 5-10-2017.
 */
public enum TowerType {
    CASTLE(0),
    WALL(1),
    LASER(2),
    FREEZE(3),
    ROCKET(4),
    SHOTGUN(5),
    NUCLEAR(6),
    CANNON(7);

    private final int value;

    TowerType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public static int getSize(){
        return TowerType.values().length;
    }
}
