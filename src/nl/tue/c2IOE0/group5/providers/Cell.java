package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.towers.AbstractTower;

import java.awt.*;

/**
 * @author Tom Peters
 */

public class Cell {
    //the tower on this cell
    AbstractTower tower;

    boolean borderCell;
    Point position;

    public Cell(boolean borderCell, int x, int y) {
        this.borderCell = borderCell;
        this.position = new Point(x, y);
    }

    /**
     * Places a tower if possible
     * @param t the tower to be placed
     * @return whether or not it was successful. If the cell is a bordercell, a tower cannot be placed
     */
    public void placeTower(AbstractTower t) throws ArrayIndexOutOfBoundsException {
        if (isBorderCell()) {
            throw new ArrayIndexOutOfBoundsException("This ("+position.getX()+","+position.getY()+" is a bordercell, you cannot place a tower here.");
        } else {
            this.tower = t;
        }
    }

    public void destroyTower() throws NullPointerException {
        if (tower == null) {
            throw new NullPointerException("There was no tower to destroy on (" +position.getX() + ","+ position.getY()+ ")");
        } else if (isBorderCell()) {
            throw new ArrayIndexOutOfBoundsException("This (" + position.getX() + "," + position.getY() + " is a bordercell, you cannot destroy a tower here.");
        } else {
                tower = null;
        }
    }

    /**
     * Get the placed tower
     * @return the tower currently placed on this cell. Null if no tower is placed or this cell is a bordercell
     */
    public AbstractTower getTower() {
        return this.tower;
    }

    /**
     * Returns whether or not this cell is a bordercell
     * @return whether or not this cell is a bordercell
     */
    public boolean isBorderCell() {
        return borderCell;
    }

    /**
     * Gets the coordinates of this cell
     * @return
     */
    public Point getPosition() {
        return this.position;
    }
}
