package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import org.joml.Vector2i;
import org.joml.Vector3f;

/**
 * @author Tom Peters
 */

public class Cell extends GameObject {

    public final static float CELL_SIZE = 1.0f;

    //the tower on this cell
    AbstractTower tower;

    private Vector3f qReward = new Vector3f();
    private boolean vQLearn = true;

    //the position in the grid
    private Vector2i position;
    private Vector3f defaultColor = new Vector3f(0.1f);
    private Vector3f activeColor = new Vector3f(0.5f, 0.5f, 0f);
    private Vector3f rangeColor = new Vector3f(0f, 0f, 0.5f);
    private Vector3f color;
    private boolean showRange = false;

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

        //initialize textures
        switch (cellType) {
            case BASE:
                defaultColor = new Vector3f(0f, 0f, 0f);
                break;
            case BORDER:
                defaultColor = new Vector3f(0.1f, 0f, 0f);
                break;
            case SPAWN:
                defaultColor = new Vector3f(0.1f);
                break;
        }
        color = defaultColor;
        this.deactivate();
        this.setPosition(x*this.getScale(), -0.495f, y*this.getScale());
    }

    //set the texture of this cell to activated
    public void activate() {
        color = activeColor;
    }

    //set the texture of this cell to deactivated
    public void deactivate() {
        if (showRange) {
            color = rangeColor;
        } else {
            color = defaultColor;
        }
    }

    public void range() {
        showRange = true;
        color = rangeColor;
    }

    public void deRange() {
        showRange = false;
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
            t.setPosition(this.getPosition().add(0, 0.495f, 0f));
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

    public void setQReward(int i, boolean b) {
        float part = Math.max(0f, (float) i / -1000f);

        this.qReward.x = part;
        this.vQLearn = b;
    }

    @Override
    public void renderInit(Renderer renderer) {
        renderer.linkMesh("/models/cell/cell.obj", () -> {
            if (!isBorderCell()) { //only draw when this cell is activated
                setModelView(renderer);
                renderer.drawBlackAsAlpha();
                renderer.noDirectionalLight();
                if (vQLearn) {
                    renderer.ambientLight(new Vector3f(qReward).add(color));
                } else {
                    renderer.ambientLight(color);
                }
            }
        });
    }

    @Override
    public void update() {
        // I'm a lazy motherfucker
    }

    @Override
    public String toString() {
        Vector2i pos = getGridPosition();
        return "(" + pos.x + ", " + pos.y + ")";
    }
}
