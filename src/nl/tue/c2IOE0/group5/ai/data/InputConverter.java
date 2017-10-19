package nl.tue.c2IOE0.group5.AI.Data;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.enemies.EnemyType;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.TowerType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TowerDefence
 * Created by s154796 on 5-10-2017.
 */
public class InputConverter {
    enum ConverterDirection {
        GameToAI,
        AIToGame
    }

    private int gridSize, nrTowers, nrDeployTypes, bufferSize;

    private EnemyType[] buffer;
    private GridProvider grid;
    private TowerType[][] tGrid;
    private double qTrust;
    private INDArray input;

    private final ConverterDirection direction;

    public static InputConverter fromNNInput(INDArray row, int gridSize, int nrTowers, int nrDeployTypes, int bufferSize){
        InputConverter converter = new InputConverter(ConverterDirection.AIToGame);
        converter.gridSize = gridSize;
        converter.nrTowers = nrTowers;
        converter.nrDeployTypes = nrDeployTypes;
        converter.bufferSize = bufferSize;
        converter.input = row;
        converter.convert();
        return converter;
    }

    public static InputConverter fromGameState(GridProvider grid, double qTrust, Enemy... buffer){
        InputConverter converter = new InputConverter(ConverterDirection.GameToAI);
        converter.buffer = Arrays.stream(buffer).map(Enemy::getType).toArray(value -> new EnemyType[0]);
        converter.grid = grid;
        converter.qTrust = qTrust;
        converter.gridSize = grid.SIZE;
        converter.nrTowers = TowerType.getSize();
        converter.nrDeployTypes = EnemyType.getSize();
        converter.bufferSize = buffer.length;
        converter.convert();
        return converter;
    }

    private void convert(){
        switch (direction){
            case GameToAI:
                convertGameToAI();
                break;
            case AIToGame:
                convertAIToGame();
                break;
        }
    }

    private InputConverter(ConverterDirection direction){
        this.direction = direction;
    }

    /**
     * Convert game objects to NN input neurons
     */
    private void convertGameToAI() {
        double[] result = new double[getGridInputSize() + getBufferInputSize() + getQTrustInputSize()];

        double[] gridResult = convertGrid();
        double[] bufferResult = convertBuffer();
        double[] qResult = new double[]{qTrust};

        System.arraycopy(gridResult, 0, result, 0, getGridInputSize());
        System.arraycopy(bufferResult, 0, result, getGridInputSize(), getBufferInputSize());
        System.arraycopy(qResult, 0, result, getGridInputSize() + getBufferInputSize(), getQTrustInputSize());

        input = Nd4j.create(result);
    }

    private void convertAIToGame(){
        tGrid = new TowerType[gridSize][gridSize];
        generateGrid();
        buffer = new EnemyType[0];
        generateBuffer();
        qTrust = input.getDouble(0, getGridInputSize() + getBufferInputSize());
    }

    private void generateGrid(){
        for (int x = 0; x < gridSize; x++){
            for (int y = 0; y < gridSize; y++){
                for (int i = 0; i < nrTowers; i++){
                    if (input.getDouble(0, x * gridSize * nrTowers + y * nrTowers + i) > 0)
                        tGrid[x][y] = TowerType.values()[i];
                }
            }
        }
    }

    private void generateBuffer() {
        List<EnemyType> bufferList = new ArrayList<>(bufferSize);
        for (int i = 0; i < nrDeployTypes; i++){
            int pos = getGridInputSize() + i;
            int count = (int) Math.floor(input.getDouble(0, pos) * bufferSize);
            for (int j = 0; j < count; j++){
                bufferList.add(EnemyType.values()[i]);
            }
        }
        buffer = bufferList.toArray(buffer);
    }

    public EnemyType[] getBuffer(){
        if (direction != ConverterDirection.AIToGame) throw new RuntimeException("illegal request");
        return buffer;
    }

    public TowerType[][] getGrid(){
        if (direction != ConverterDirection.AIToGame) throw new RuntimeException("illegal request");
        return tGrid;
    }

    public double getqTrust(){
        if (direction != ConverterDirection.AIToGame) throw new RuntimeException("illegal request");
        return qTrust;
    }

    public INDArray getInput(){
        if (direction != ConverterDirection.GameToAI) throw new RuntimeException("illegal request");
        return input;
    }

    /**
     * get the number of neurons needed for the buffer input
     * @return int nr. of neurons
     */
    public int getBufferInputSize(){
        return nrDeployTypes;
    }

    /**
     * get the number of neurons needed for the grid
     * @return int nr. of neurons
     */
    public int getGridInputSize(){
        return gridSize * gridSize * nrTowers;
    }

    /**
     * get the number of neurons needed for q-trust
     * @return int nr. of neurons
     */
    public int getQTrustInputSize(){
        return 1;
    }

    /**
     * convert the grid object to input neurons
     * @return array input neurons
     */
    private double[] convertGrid(){
        double[] result = new double[gridSize * gridSize * nrTowers];
        int positionId, towerId;
        AbstractTower tower;
        for (int x = 0; x < gridSize; x++){
            for (int y = 0; y < gridSize; y++){
                positionId = x * gridSize * nrTowers + y * nrTowers;
                tower = grid.getCell(x, y).getTower();
                towerId = tower.getType().getValue();
                result[positionId + towerId] = tower.getLevel();
            }
        }
        return result;
    }

    /**
     * convert the buffer to input neurons
     * @return array input neurons
     */
    private double[] convertBuffer(){
        double[] result = new double[nrDeployTypes];

        //count the number of units
        for (EnemyType t : buffer){
            result[t.getValue()] += 1;
        }

        //only divide at the end to preserve decimal precision
        for (int i = 0; i < result.length; i++){
            result[i] /= bufferSize;
        }

        return result;
    }
}
