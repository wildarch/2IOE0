package nl.tue.c2IOE0.group5.towers;

/**
 * TowerDefence
 * Created by s154796 on 5-10-2017.
 */
public enum TowerType {
    CASTLE(0),
    WALL(1),
    ROCKET(2),
    CANNON(3);

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

    public Class<? extends AbstractTower> getTowerClass() {
        switch(this) {
            case CASTLE:
                return MainTower.class;
            case WALL:
                return WallTower.class;
            case CANNON:
                return CannonTower.class;
            case ROCKET:
                return RocketTower.class;
            default:
                throw new RuntimeException("Tower type " + this.name() + " does not have a corresponding class");
        }
    }
}
