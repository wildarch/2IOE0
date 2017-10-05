package nl.tue.c2IOE0.group5.AI.Data;

import nl.tue.c2IOE0.group5.enemies.EnemyType;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.towers.TowerType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * TowerDefence
 * Created by s154796 on 5-10-2017.
 */
public class InputConverter {
    private final EnemyType[] buffer;
    private final GridProvider grid;
    private final double qTrust;

    public InputConverter(GridProvider grid, double qTrust, EnemyType... buffer){
        this.buffer = buffer;
        this.grid = grid;
        this.qTrust = qTrust;
    }

    /**
     * Convert game objects to NN input neurons
     * @return INDArray input row vector
     */
    public INDArray convert() {
        double[] result = new double[getBufferInputSize() + getGridInputSize() + getQTrustInputSize()];

        double[] gridResult = convertGrid();
        double[] bufferResult = convertBuffer();
        double[] qResult = new double[]{qTrust};

        System.arraycopy(gridResult, 0, result, 0, getGridInputSize());
        System.arraycopy(bufferResult, 0, result, getGridInputSize(), getBufferInputSize());
        System.arraycopy(qResult, 0, result, getGridInputSize() + getBufferInputSize(), getQTrustInputSize());

        return Nd4j.create(result);
    }

    /**
     * get the number of neurons needed for the buffer input
     * @return int nr. of neurons
     */
    public static int getBufferInputSize(){
        return TowerType.getSize();
    }

    /**
     * get the number of neurons needed for the grid
     * @return int nr. of neurons
     */
    public int getGridInputSize(){
        return grid.SIZE * grid.SIZE * TowerType.getSize();
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
        final int numTowers = TowerType.getSize();
        double[] result = new double[grid.SIZE * grid.SIZE * numTowers];
        int positionId, towerId;
        for (int x = 0; x < grid.SIZE; x++){
            for (int y = 0; y < grid.SIZE; y++){
                positionId = x * grid.SIZE * numTowers + y * numTowers;
                towerId = grid.getCell(x, y).getTower().getType().getValue();
                result[positionId + towerId] = 1;
            }
        }
        return result;
    }

    /**
     * convert the buffer to input neurons
     * @return array input neurons
     */
    private double[] convertBuffer(){
        double totalUnits = buffer.length;
        double[] result = new double[EnemyType.getSize()];

        //count the number of units
        for (EnemyType t : buffer){
            result[t.getValue()] += 1;
        }

        //only divide at the end to preserve decimal precision
        for (int i = 0; i < result.length; i++){
            result[i] /= totalUnits;
        }

        return result;
    }
}
