package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.*;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Texture;
import nl.tue.c2IOE0.group5.towers.AbstractTower;

import java.awt.*;

/**
 * @author Tom Peters
 */

public class Cell extends GameObject {
    //the tower on this cell
    AbstractTower tower;

    boolean borderCell;
    Point position;

    private Mesh mesh;

    public Cell(boolean borderCell, int x, int y) {
        super();
        this.borderCell = borderCell;
        this.position = new Point(x, y);

        try {
            this.mesh = OBJLoader.loadMesh("/bunny.obj");
            this.mesh.setMaterial(new Material("/square.png"));
            this.setScale(40f);
            this.setPosition(x*2, y*2, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public Point getGridPosition() {
        return this.position;
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);
        mesh.draw(renderer);
    }
}
