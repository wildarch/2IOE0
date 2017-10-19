package nl.tue.c2IOE0.group5.util;

/**
 * @author Geert van Ieperen
 *         created on 16-10-2017.
 * a class to track button positions.
 * not sure whether this could be made a private class
 */
public class PositionState {
    private int x;
    private int y;
    private final int offset;

    // current position
    public PositionState(int x, int y, int offset){
        this.x = x;
        this.y = y;
        this.offset = offset;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        int ret = y;
        this.y += offset;
        return ret;
    }
}