package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.controllers.QLearner;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

/**
 * A class providing the grid.
 * The grid is a double array containing cells. The x and y coordinates work as expected in a mathematical system, with
 * [0][0] lying in the bottom left corner and [1][0] the cell adjacent to it on the right side.
 */
public class GridProvider implements Provider {

    //total size of the grid (including spawn cells). Change this to change the total grid
    public static final int SIZE = 13;
    //size of the grid in which towers can be placed
    public static final int PLAYFIELDSIZE = 9;
    //the actual grid
    private final Cell[][] grid = new Cell[SIZE][SIZE];

    //the cell currently active (pointed to)
    private Cell activeCell;

    @Override
    public void init(Engine engine) {
        try {
            // Setup cell mesh
            Mesh cell = engine.getRenderer().linkMesh("/cube.obj");
            cell.setMaterial(new Material("/square.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the player base cells
        int bordersize = (SIZE - PLAYFIELDSIZE - 1)/2;
        for (int x = bordersize+1; x < SIZE - bordersize-1; x++) {
            for (int y = bordersize+1; y < SIZE - bordersize-1; y++) {
                //initialize the playfield as non-bordercells
                grid[x][y] = new Cell(CellType.BASE, x, y).init(engine.getRenderer());
                //initialize the estimated damage per cell to 0
            }
        }

        // Create the borders
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (grid[x][y] == null) {
                    //initialize all cells not yet initialized as a bordercell
                    grid[x][y] = new Cell(CellType.BORDER, x, y).init(engine.getRenderer());
                }
            }
        }

        activeCell = getCell(0, 0);
    }

    /**
     * Get a cell at a specific coordinate
     * @param x
     * @param y
     * @return The cell on that specific coordinate
     */
    public Cell getCell(int x, int y) throws ArrayIndexOutOfBoundsException {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
            throw new ArrayIndexOutOfBoundsException("cannot get the cell at coordinates: (" + x + ","  + y + ")");
        }
        return grid[x][y];
    }

    public Cell getCell(Vector2i position) throws ArrayIndexOutOfBoundsException {
        return getCell(position.x(), position.y());
    }

    /**
     * Set a tower on a specific position
     * @param x the x coordinate to set the tower to
     * @param y the y coordinate to set the tower to
     * @param tower the tower to place
     * @return true if succeeded, false if the cell on the coordinates is a bordercell
     * @throws ArrayIndexOutOfBoundsException when the cell is a bordercell or the cell is not even in the grid
     */
    public void placeTower(int x, int y, AbstractTower tower) throws ArrayIndexOutOfBoundsException {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
            throw new ArrayIndexOutOfBoundsException("The coordinates of this cell are outside the grid.");
        }
        getCell(x, y).placeTower(tower);
    }

    public void levelUpTower(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
            throw new ArrayIndexOutOfBoundsException("The coordinates of this cell are outside the grid.");
        }
        Cell cell = getCell(x, y);
        AbstractTower tower = cell.getTower();
        if (tower == null) {
            throw new NullPointerException("No tower on cell (" + cell.getGridPosition().x() + "," + cell.getGridPosition().y() + ")");
        }
        tower.levelUp();
    }

    /**
     * Destroys a tower on a specific cell
     * @param x
     * @param y
     * @throws ArrayIndexOutOfBoundsException when the coordinates are out of range or if it is a bordercell
     * @throws NullPointerException when there was no tower to destroy
     */
    public void destroyTower(int x, int y) throws ArrayIndexOutOfBoundsException, NullPointerException {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
            throw new ArrayIndexOutOfBoundsException("The coordinates of this cell are outside the grid.");
        }
        Cell cell = getCell(x, y);
        cell.destroyTower();
    }

    /**
     * Set the active cell and color it
     * @param x the x coordinate of the active cell
     * @param y the y coordinate of the active cell
     */
    public void setActiveCell(int x, int y) {
        activeCell.deactivate();
        this.activeCell = getCell(x, y);
        activeCell.activate();
    }

    public void recalculateActiveCell(Vector2i mousePos, Camera c, Renderer r, Window window) {
        int mouseX = mousePos.x();
        int mouseY = mousePos.y();
        float viewPortX = 2 * (float)mouseX / (float)window.getWidth() - 1;
        float viewPortY = 1 - 2 * (float)mouseY / (float)window.getHeight();

        Vector3f direction3f = getDirectionOfCamera(r, window, viewPortX, viewPortY);

        //the ray is now defined using the position of the camera and direction
        if (direction3f.y() >= 0) {
            activeCell.deactivate();
        }
        float lambda = -c.getPosition().y()/direction3f.y(); //assuming the y = 0
        float x = c.getPosition().x() + lambda * direction3f.x();
        float z = c.getPosition().z() + lambda * direction3f.z();
        int gridX = Math.round(x);
        int gridY = Math.round(z);
        if (!(gridX < 0 || gridY < 0 || gridX >= SIZE || gridY >= SIZE)) {
            setActiveCell(gridX, gridY);
            activeCell.activate();
        }
    }

    public Vector3f getDirectionOfCamera(Renderer r, Window window, float viewPortX, float viewPortY) {
        Matrix4f viewMatrix = r.getViewMatrix();
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        int viewPortZ = -1;
        int viewPortW = 1;

        Vector4f viewPortPosition = new Vector4f(viewPortX, viewPortY, viewPortZ, viewPortW);
        Matrix4f projectionMatrixInverse = projectionMatrix.invert();
        Matrix4f viewMatrixInverse = viewMatrix.invert();

        Vector4f stepone = viewPortPosition.mul(projectionMatrixInverse);
        Vector4f steptwo = new Vector4f(stepone.x(), stepone.y(), -1f, 0);
        Vector4f direction = steptwo.mul(viewMatrixInverse);
        return new Vector3f(direction.x, direction.y, direction.z);
    }

    /**
     * A helper method to show the qlearner output, by first deactivating all the cells and then activating the cells on the path
     */
    private void deactivateAll() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                getCell(x, y).deactivate();
            }
        }
    }

    public void drawPath(List<Integer> path) {
        deactivateAll();
        for (int state : path) {
            Vector2i position = QLearner.getPoint(state);
            getCell(position).activate();
        }
    }

    public Vector2i getActiveCell() {
        return this.activeCell.getGridPosition();
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Window window, Renderer renderer) {

    }
}
