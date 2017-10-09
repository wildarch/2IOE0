package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.Vector;

/**
 * @author Tom Peters
 */

public class Cell extends GameObject {
    //the tower on this cell
    AbstractTower tower;

    //the position in the grid
    private Vector2i position;
    private Vector3f defaultColor = new Vector3f(0.3f);
    private Vector3f color;

    private final CellType cellType;
    private Mesh mesh;

    /**
     * The x and y coordinates are of the grid, not in 3d space!
     * @param type
     * @param x
     * @param y
     */
    public Cell(CellType type, int x, int y) {
        super();
        this.cellType = type;
        this.position = new Vector2i(x, y);

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
            mesh.setMaterial(new Material("/square.png"));
            switch (cellType) {
                case BASE:
                    defaultColor = new Vector3f(0f, 1f, 0f);
                    break;
                case BORDER:
                    defaultColor = new Vector3f(1f, 0f, 0f);
                    break;
                case SPAWN:
                    defaultColor = new Vector3f(1f);
                    break;
            }
            color = defaultColor;
            this.deactivate();
            this.setPosition(x*this.getScale(), -0.5f, y*this.getScale());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //set the texture of this cell to activated
    public void activate() {
        color = new Vector3f(1f, 1f, 0f);
    }

    //set the texture of this cell to deactivated
    public void deactivate() {
        color = defaultColor;
    }

    /**
     * Places a tower if possible
     * @param t the tower to be placed
     * @throws ArrayIndexOutOfBoundsException if this cell is a bordercell and thus no tower can be placed here
     */
    public void placeTower(AbstractTower t) throws ArrayIndexOutOfBoundsException {
        if (isBorderCell()) {
            throw new ArrayIndexOutOfBoundsException("This ("+position.x()+","+position.y()+" is a bordercell, you cannot place a tower here.");
        } else {
            this.tower = t;
        }
    }

    public void destroyTower() throws NullPointerException {
        if (tower == null) {
            throw new NullPointerException("There was no tower to destroy on (" +position.x() + ","+ position.y()+ ")");
        } else if (isBorderCell()) {
            throw new ArrayIndexOutOfBoundsException("This (" + position.x() + "," + position.y() + " is a bordercell, you cannot destroy a tower here.");
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
        return cellType == CellType.BORDER;
    }

    /**
     * Gets the coordinates of this cell
     * @return
     */
    public Vector2i getGridPosition() {
        return this.position;
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);
        renderer.ambientLight(color, () ->
                mesh.draw(renderer)
        );
    }
}
