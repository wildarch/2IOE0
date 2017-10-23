package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Simulator;
import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.TowerConnection;
import nl.tue.c2IOE0.group5.towers.WallTower;
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
public class GridProvider extends ObjectProvider<Cell> {

    //total size of the grid (including spawn cells). Change this to change the total grid
    public final int SIZE;
    //size of the grid in which towers can be placed
    public final int PLAYFIELDSIZE;
    //the actual grid
    private final Cell[][] grid;

    /* connecting tower grid: while it cannot use all spaces available using SIZE, it is more clear to work with.
    logically placing them on "-0.5 and +0.5" of towers makes the most sense (as they are between towers) but since
    this is not possible, we use an array of twice the size, thus "-1" being -0.5 and +1 to be +0.5 when reading values (vs a coord
    from the cell array). */
    private final TowerConnection[][] towerconnections;

    private TowerConnectionProvider towerConnectionProvider;

    //the cell currently active (pointed to)
    private Cell activeCell;
    private Cell rangedCell;

    public GridProvider(){
        this(13, 9);
    }

    public GridProvider(int gridSize, int playFieldSize){
        super();
        SIZE = gridSize;
        PLAYFIELDSIZE = playFieldSize;
        grid = new Cell[SIZE][SIZE];
        towerconnections = new TowerConnection[SIZE*2][SIZE*2];
    }

    @Override
    public void init(Simulator engine) {
        super.init(engine);

        towerConnectionProvider = engine.getProvider(TowerConnectionProvider.class);

        // Create the player base cells
        int bordersize = (SIZE - PLAYFIELDSIZE - 1)/2;
        for (int x = bordersize+1; x < SIZE - bordersize-1; x++) {
            for (int y = bordersize+1; y < SIZE - bordersize-1; y++) {
                //initialize the playfield as non-bordercells
                Cell c = new Cell(CellType.BASE, x, y).init(getRenderer());
                grid[x][y] = c;
                objects.add(c);
            }
        }

        // Create the borders
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (grid[x][y] == null) {
                    //initialize all cells not yet initialized as a bordercell
                    Cell c = new Cell(CellType.BORDER, x, y).init(getRenderer());
                    grid[x][y] = c;
                    objects.add(c);
                }
            }
        }

        activeCell = getCell(0, 0);
    }

    @Override
    public void renderInit(Engine engine) {
        // Setup cell mesh
        Mesh cell = engine.getRenderer().linkMesh("/models/cell/cell.obj");
        Material cellMaterial = new Material("/models/cell/cell.png");
        cellMaterial.setTransparency(true);
        cell.setMaterial(cellMaterial);
    }

    private boolean isActiveCell(int x, int y) {
        int minBorder = (SIZE - PLAYFIELDSIZE) / 2;
        int maxBorder = SIZE - minBorder - 1;

        return minBorder <= x && x <= maxBorder && minBorder <= y && y <= maxBorder;
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
        //if this is a walltower, check if a TowerConnection can be placed
        if (tower instanceof WallTower) {
            //first make sure this is a legal place for the tower to avoid future complications
            if (x == 0 || x == SIZE-1 || y == 0 || y == SIZE-1) {
                //tower placed on border of cell, should not be possible:
                throw new ArrayIndexOutOfBoundsException("Walltower was placed on the edge of the grid.");
            }
            //check for surrounding towers
            if (getCell(x-1, y).getTower() instanceof WallTower) {
                placeConnectingTower(2*x-1, 2*y, true);
            }
            if (getCell(x+1, y).getTower() instanceof WallTower) {
                placeConnectingTower(2*x+1, 2*y, true);
            }
            if (getCell(x, y-1).getTower() instanceof WallTower) {
                placeConnectingTower(2*x, 2*y-1, false);
            }
            if (getCell(x, y+1).getTower() instanceof WallTower) {
                placeConnectingTower(2*x, 2*y+1, false);
            }
        }

    }

    public void placeConnectingTower(int x, int y, boolean rotate) {
        Vector3f position = new Vector3f(((float) x ) / 2.0f, 0f, ((float) y ) / 2.0f);
        float rotation = rotate ? 0f : 90f;
        TowerConnection tower = new TowerConnection(position, rotation, getRenderer(), getEngine().getRenderLoopTimer()).init(getRenderer());
        towerconnections[x][y] = tower;
        towerConnectionProvider.addTowerConnection(tower);
    }

    public void destroyConnectingTower(int x, int y) {
        TowerConnection tower = towerconnections[x][y];
        if (tower == null) {
            throw new NullPointerException("Connecting tower being removed at " + x + ", " + y + " does not exist");
        }
        tower.destroy();
        towerconnections[x][y] = null;
        towerConnectionProvider.deleteTowerConnection(tower);
    }

    public void placePlayFieldTower(int x, int y, AbstractTower tower){
        if (x < 0 || x >= PLAYFIELDSIZE || y < 0 || y >= PLAYFIELDSIZE) {
            throw new ArrayIndexOutOfBoundsException("The coordinates of this cell are outside the grid.");
        }

        final int diff = ((SIZE - PLAYFIELDSIZE) / 2);
        placeTower(x + diff, y + diff, tower);
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
        AbstractTower tower = cell.getTower();
        cell.destroyTower();
        //check if tower destroyed was a walltower, and if so, remove connecting towers
        //if this is a walltower, check if a TowerConnection can be placed
        if (tower instanceof WallTower) {
            //first make sure this is a legal place for the tower to avoid future complications
            if (x == 0 || x == SIZE-1 || y == 0 || y == SIZE-1) {
                //tower placed on border of cell, should not be possible:
                throw new ArrayIndexOutOfBoundsException("Walltower was on the edge of the grid, should not be possible.");
            }
            //check for surrounding towers
            if (getCell(x-1, y).getTower() instanceof WallTower) {
                destroyConnectingTower(2*x-1, 2*y);
            }
            if (getCell(x+1, y).getTower() instanceof WallTower) {
                destroyConnectingTower(2*x+1, 2*y);
            }
            if (getCell(x, y-1).getTower() instanceof WallTower) {
                destroyConnectingTower(2*x, 2*y-1);
            }
            if (getCell(x, y+1).getTower() instanceof WallTower) {
                destroyConnectingTower(2*x, 2*y+1);
            }
        }
    }

    /**
     * Set the active cell and color it
     * @param cell the Cell to activate.
     */
    public void setActiveCell(Cell cell) {
        activeCell.deactivate();
        this.activeCell = cell;
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
        if (isActiveCell(gridX, gridY)) {
            Cell cell = getCell(gridX, gridY);
            setActiveCell(cell);
        } else {
            activeCell.deactivate();
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

    public void click() {
        if (rangedCell == null || rangedCell != activeCell) {
            rangedCell = activeCell;
            AbstractTower t = activeCell.getTower();
            if (t == null) {
                deRangeAll();
                return;
            }

            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    Cell c = getCell(x, y);
                    if (inRange(t, c)) {
                        c.range();
                    } else {
                        c.deRange();
                    }
                }
            }
        } else if (activeCell == rangedCell){
            deRangeAll();
            rangedCell = null;
        }
    }

    private boolean inRange(AbstractTower t, Cell c) {
        Cell tc = t.getCell();
        int range = t.getRange();
        int dist = Math.abs(tc.getGridPosition().x() - c.getGridPosition().x()) + Math.abs(tc.getGridPosition().y() - c.getGridPosition().y());

        if (dist <= range) {
            return true;
        }
        return false;
    }

    private void deRangeAll() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                getCell(x, y).deRange();
            }
        }
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
            Vector2i position = QLearner.getPoint(state, SIZE);
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
