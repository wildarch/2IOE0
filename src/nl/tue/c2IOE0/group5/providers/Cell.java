package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.*;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.towers.AbstractTower;

import java.awt.*;
import java.io.IOException;

/**
 * @author Tom Peters
 */

public class Cell extends GameObject {
    //the tower on this cell
    AbstractTower tower;

    boolean borderCell;

    //the position in the grid
    Point position;
    Texture activeTexture;
    Texture normalTexture;

    private Mesh mesh;

    /**
     * The x and y coordinates are of the grid, not in 3d space!
     * @param borderCell
     * @param x
     * @param y
     */
    public Cell(boolean borderCell, int x, int y) {
        super();
        this.borderCell = borderCell;
        this.position = new Point(x, y);

        /*try {
            this.mesh = OBJLoader.loadMesh("/bunny.obj");
            this.mesh.setTexture(new Texture("/square.png"));
            this.setScale(40f);
            this.setPosition(x*2, y*2, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        try {
            this.mesh = new Mesh(new float[] {
                    -0.5f,   0.5f,   0.5f,
                    -0.5f,  -0.5f,   0.5f,
                    0.5f,  -0.5f,   0.5f,
                    0.5f,   0.5f,   0.5f,
                    -0.5f,   0.5f,  -0.5f,
                    0.5f,   0.5f,  -0.5f,
                    -0.5f,  -0.5f,  -0.5f,
                    0.5f,  -0.5f,  -0.5f,
                    //from here to texture the top
                    -0.5f,   0.5f,   0.5f,
                    0.5f,   0.5f,   0.5f,
                    -0.5f,   0.5f,  -0.5f,
                    0.5f,   0.5f,  -0.5f,
            }, new float[] {
                    0f, 0f,
                    0f, 1f,
                    1f, 1f,
                    1f, 0f,
                    1f, 0f,
                    0f, 0f,
                    1f, 1f,
                    0f, 1f,
                    //from here to texture the top
                    0f, 1f,
                    0f, 0f,
                    1f, 1f,
                    1f, 0f,
            }, new float[] {
                1f, 1f, 1f,
            }, new int[] {
                            0, 1, 3, 3, 1, 2,
                            10, 8, 9, 11, 10, 9,
                            3, 2, 7, 5, 3, 7,
                            6, 1, 0, 6, 0, 4,
                            2, 1, 6, 2, 6, 7,
                            7, 6, 4, 7, 4, 5,
            });

            //initialize textures
            activeTexture = new Texture("/yellowsquare.png");
            normalTexture = new Texture("/square.png");
            this.deactivate();
            this.setPosition(x*this.getScale(), 0, y*this.getScale());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //set the texture of this cell to activated
    public void activate() {
        this.mesh.setTexture(activeTexture);
    }

    //set the texture of this cell to deactivated
    public void deactivate() {
        this.mesh.setTexture(normalTexture);
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
        mesh.draw();
    }
}
