package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import org.joml.Vector2i;
import org.joml.Vector3f;

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
    //private Mesh mesh;

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
            //this.mesh = OBJLoader.loadMesh("/cube.obj");

            //initialize textures
            //mesh.setMaterial(new Material("/square.png"));
            switch (cellType) {
                case BASE:
                    defaultColor = new Vector3f(0f, 0.3f, 0f);
                    break;
                case BORDER:
                    defaultColor = new Vector3f(0.3f, 0f, 0f);
                    break;
                case SPAWN:
                    defaultColor = new Vector3f(0.3f);
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
            t.setCell(this);
            t.setPosition(this.getPosition().add(0, 0.5f, 0f));
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
    public Cell init(Renderer renderer) {
        try {
            renderer.linkMesh("/cube.obj", () -> {
                setModelView(renderer);
                renderer.ambientLight(color);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public void update() {
        // I'm a lazy motherfucker
    }
}
