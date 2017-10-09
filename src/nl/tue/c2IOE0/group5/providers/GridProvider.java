package nl.tue.c2IOE0.group5.providers;

import javafx.util.Pair;
import nl.tue.c2IOE0.group5.QLearner;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.*;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * A class providing the grid.
 * The grid is a double array containing cells. The x and y coordinates work as expected in a mathematical system, with
 * [0][0] lying in the bottom left corner and [1][0] the cell adjacent to it on the right side.
 */
public class GridProvider implements Provider {

    //total size of the grid. Change this to change the total grid
    public final int SIZE = 13;
    //size of the grid in which towers can be placed
    public final int PLAYFIELDSIZE = 9;
    //the actual grid
    private final Cell[][] grid = new Cell[SIZE][SIZE];

    //the cell currently active (pointed to)
    private Cell activeCell;

    private QLearner qlearner;
    private List<Integer> optimalPath; //the current optimal path for the active cell

    @Override
    public void init(Engine engine) {
        doQLearnerStuffForTesting();


        int bordersize = (SIZE - PLAYFIELDSIZE)/2;
        for (int x = bordersize; x < SIZE - bordersize; x++) {
            for (int y = bordersize; y < SIZE - bordersize; y++) {
                //initialize the playfield as non-bordercells
                grid[x][y] = new Cell(CellType.BASE, x, y);
                //initialize the estimated damage per cell to 0
            }
        }

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (grid[x][y] == null) {
                    //initialize all cells not yet initialized as a bordercell
                    grid[x][y] = new Cell(CellType.BORDER, x, y);
                }
            }
        }
        setActiveCell(1, 1);
    }

    public void doQLearnerStuffForTesting() {
        int noIterations = 1000;
        qlearner = new QLearner(SIZE, noIterations);
        qlearner.updateRewardsMatrix(qlearner.getState(SIZE/2, SIZE/2), 1000);
        qlearner.updateRewardsMatrix(qlearner.getState(3, 3), 500);
        qlearner.updateRewardsMatrix(qlearner.getState(2, 3), -5);
        qlearner.updateRewardsMatrix(qlearner.getState(4, 3), -5);
        qlearner.updateRewardsMatrix(qlearner.getState(3, 2), -5);
        qlearner.updateRewardsMatrix(qlearner.getState(3, 4), -5);
        for (int i = 0; i < 10; i++) {
            qlearner.generateRandomPath(100);
        }
        qlearner.generateRandomPath(100, 0);
        double gamma = 0.1d;
        qlearner.execute(gamma);
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

    private boolean isInRange(Cell cellWithTower, Cell cellToCheck) {
        int range = cellWithTower.getTower().getRange();
        Vector2i positionToCheck = cellToCheck.getGridPosition();
        Vector2i positionWithTower = cellWithTower.getGridPosition();
        return positionToCheck.x() - positionWithTower.x() + positionToCheck.y() - positionWithTower.y() < range;
    }

    /**
     * Set the active cell and color it
     * @param x the x coordinate of the active cell
     * @param y the y coordinate of the active cell
     */
    public void setActiveCell(int x, int y) {
        if (activeCell != null) {
            activeCell.deactivate();
        }
        this.activeCell = getCell(x, y);
        activeCell.activate();
    }

    public void recalculateActiveCell(Vector2i mousePos, Camera c, Renderer r, Window window) {
        Matrix4f viewMatrix = r.getViewMatrix();
        Matrix4f projectionMatrix = r.getProjectionMatrix(window);
        int mouseX = mousePos.x();
        int mouseY = mousePos.y();
        float viewPortX = 2 * (float)mouseX / (float)window.getWidth() - 1;
        float viewPortY = 1 - 2 * (float)mouseY / (float)window.getHeight();
        int viewPortZ = -1;
        int viewPortW = 1;

        Vector4f viewPortPosition = new Vector4f(viewPortX, viewPortY, viewPortZ, viewPortW);
        Matrix4f projectionMatrixInverse = projectionMatrix.invert();
        Matrix4f viewMatrixInverse = viewMatrix.invert();

        Vector4f stepone = viewPortPosition.mul(projectionMatrixInverse);
        Vector4f steptwo = new Vector4f(stepone.x(), stepone.y(), -1f, 0);
        Vector4f direction = steptwo.mul(viewMatrixInverse);
        Vector3f direction3f = new Vector3f(direction.x, direction.y, direction.z);

        //the ray is now defined using the position of the camera and direction
        if (direction3f.y() >= 0) {
            activeCell.deactivate();
            return;
        }
        float lambda = -c.getPosition().y()/direction3f.y(); //assuming the y = 0
        float x = c.getPosition().x() + lambda * direction3f.x();
        float z = c.getPosition().z() + lambda * direction3f.z();
        int gridX = Math.round(x);
        int gridY = Math.round(z);
        if (!(gridX < 0 || gridY < 0 || gridX >= SIZE || gridY >= SIZE)) {
            setActiveCell(gridX, gridY);
        } else {
            activeCell.deactivate();
        }
    }

    /**
     * To be called on a click, currently shows the qlearner output
     */
    public void click() {
        //assuming the active cell is set correctly
        List<Integer> optimalPath = qlearner.getOptimalPath(qlearner.getState(activeCell.getGridPosition()));
        deactivateAll();
        for (int state : optimalPath) {
            Cell cell = getCell(qlearner.getPoint(state));
            cell.activate();
        }
    }

    /**
     * A helper method to show the qlearner output
     */
    private void deactivateAll() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                getCell(x, y).deactivate();
            }
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Window window, Renderer renderer) {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                getCell(x, y).draw(window, renderer);
            }
        }
    }
}
