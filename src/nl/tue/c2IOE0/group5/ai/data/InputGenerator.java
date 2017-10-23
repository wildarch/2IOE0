package nl.tue.c2IOE0.group5.ai.data;

import com.google.common.collect.Sets;
import nl.tue.c2IOE0.group5.towers.TowerType;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Daan de Graaf on 13/09/17.
 */
public class InputGenerator {

    private final int nrTowers;
    private final int nrTowerLevels;
    private final int nrDeployTypes;
    private final int nrTimeSteps;
    private final int unitBufSize;
    private final int qLearnerTrustSteps;
    private Set<List<Float>> inputs;
    private List<String> labels;

    public InputGenerator(int nrTowers, int nrTowerLevels, int nrDeployTypes, int nrTimeSteps,
                          int unitBufSize, int qLearnerTrustSteps) {
        this.nrTowers = nrTowers;
        this.nrTowerLevels = nrTowerLevels;
        this.nrDeployTypes = nrDeployTypes;
        this.nrTimeSteps = nrTimeSteps;
        this.unitBufSize = unitBufSize;
        this.qLearnerTrustSteps = qLearnerTrustSteps;
    }

    public static INDArray getInputs(int numInputs, int gridSize, int nrTowers, int nrTowerLevels,
                                     int nrDeployTypes, int qLearnerTrustSteps) {
        List<double[]> data = new ArrayList<>(numInputs);
        Random r = new Random();

//        INDArray gridResult = Nd4j.create(numInputs, gridSize * gridSize * nrTowers);
//        INDArray bufferResult = Nd4j.create(numInputs, nrDeployTypes);
//        INDArray qTrustResult = Nd4j.create(numInputs, 1);


        for (int i = 0; i < numInputs; i++){
            double[] row = new double[gridSize * gridSize * nrTowers + nrDeployTypes + 1];

            double[] grid = randomGrid(gridSize, nrTowers, nrTowerLevels, r);
            double[] buffer = randomBuffer(nrDeployTypes, r);
            double[] qtrust = new double[]{r.nextDouble()};

//            gridResult.putRow(i, Nd4j.create(grid));
//            bufferResult.putRow(i, Nd4j.create(buffer));
//            qTrustResult.putRow(i, Nd4j.create(qtrust));

            System.arraycopy(grid, 0, row, 0, grid.length);
            System.arraycopy(buffer, 0, row, grid.length, buffer.length);
            System.arraycopy(qtrust, 0, row, grid.length + buffer.length, qtrust.length);

            data.add(row);

            //System.out.println(Arrays.toString(row));
        }

        INDArray result = Nd4j.create(numInputs, gridSize * gridSize * nrTowers + nrDeployTypes + 1);

        for (int i = 0; i < data.size(); i++){
            double[] row = data.get(i);
            for(int c = 0; c < row.length; c++){
                result.putScalar(i, c, row[c]);
            }
        }

        return result;//new INDArray[]{gridResult, bufferResult, qTrustResult};
    }

    public interface TD_Q_Function{
        /**
         * Returns the function values for the given input matrix
         * @param input
         * @return
         */
        INDArray getOutputValues(INDArray input);
    }
//
    /** Create a DataSetIterator for training
     * @param x X values
     * @param function Function to evaluate
     * @param batchSize Batch size (number of examples for every call of DataSetIterator.next())
     * @param rng Random number generator (for repeatability)
     */
    public static DataSetIterator getTrainingData(final INDArray x, final TD_Q_Function function,
                                                  final int batchSize, final Random rng) {
        final INDArray y = function.getOutputValues(x);
        final org.nd4j.linalg.dataset.MultiDataSet allData = new org.nd4j.linalg.dataset.MultiDataSet(x, y);
//        MultiDataSet ds = new (x, new INDArray[]{y});

        final List<MultiDataSet> list = allData.asList();
        Collections.shuffle(list,rng);
        return new ListDataSetIterator(list,batchSize);
    }

    public static DataSetIterator getTrainingData(final DataSet allData, final int batchSize,
                                                  final Random rng){
        final List<org.nd4j.linalg.dataset.DataSet> list = allData.asList();
        Collections.shuffle(list,rng);
        return new ListDataSetIterator(list,batchSize);
    }

    public static void export(File output, int numInputs, int gridSize, int nrTowers,
                              int nrTowerLevels, int nrDeployTypes, int qLearnerTrustSteps,
                              final TD_Q_Function function) throws IOException {
        DataSet data = getTrainingData(numInputs, gridSize, nrTowers, nrTowerLevels, nrDeployTypes, qLearnerTrustSteps, function);
        data.save(output);
    }

    public static void export(File output, DataSet data) throws IOException {
        data.save(output);
    }

    public static DataSet fromFile(File input) throws IOException {
        DataSet set = new DataSet();
        set.load(input);
        return set;
    }

//    public static MultiDataSetIterator getTrainingData(int numInputs, int gridSize, int nrTowers, int nrTowerLevels, int nrDeployTypes, int qLearnerTrustSteps, final TD_Q_Function function, final int batchSize, final Random rng) {
//        INDArray[] genData = getInputs(numInputs, gridSize, nrTowers, nrTowerLevels, nrDeployTypes, qLearnerTrustSteps);
//        return getTrainingData(genData, function, batchSize, rng);
//    }

    private static DataSet getTrainingData(final INDArray x, final TD_Q_Function function){
        final INDArray y = function.getOutputValues(x);

        return new DataSet(x, y);
    }

    public static DataSet getTrainingData(int numInputs, int gridSize, int nrTowers,
                                          int nrTowerLevels, int nrDeployTypes,
                                          int qLearnerTrustSteps, final TD_Q_Function function){
        INDArray genData = getInputs(numInputs, gridSize, nrTowers, nrTowerLevels, nrDeployTypes, qLearnerTrustSteps);
        return getTrainingData(genData, function);
    }

    public static void main(String[] args){
        getInputs(10, 9, 5, 10, 8, 10);
    }

    private static double[] randomBuffer(int nrDeployTypes, Random r){
        double[] buffer = new double[nrDeployTypes];
        double sum = 0.0;
        for (int i = 0; i < nrDeployTypes; i++){
            buffer[i] = r.nextDouble();
            sum += buffer[i];
        }
        for (int i = 0; i < nrDeployTypes; i++){
            buffer[i] = buffer[i] / sum;
        }
        return buffer;
    }

    private static double[] randomGrid(int gridSize, int nrTowers, int nrTowerLevels, Random r){
        double[] grid = new double[gridSize * gridSize * nrTowers];

        //grid data
        for (int x = 0; x < gridSize; x++){
            for (int y = 0; y < gridSize; y++){
                //this cell contains a tower
                int towerType = r.nextInt(nrTowers);

                //tower levels are disabled
                double towerLevel = 1;//r.nextInt(nrTowerLevels) / (double)nrTowerLevels;

                if(x == gridSize / 2 && y == gridSize / 2){
                    towerType = TowerType.CASTLE.getValue();
                }

                if(!r.nextBoolean()) continue;

                grid[x * gridSize * nrTowers + y * nrTowers + towerType] = towerLevel;

            }
        }
        return grid;
    }

    @Deprecated
    public Set<List<Float>> getInputs() {
        if (inputs != null) {
            return inputs;
        }
        List<Set<Float>> properties = new ArrayList<>();
        labels = new ArrayList<>();

        for (int i = 0; i < nrTowers; i++) {
            properties.add(getTowerValues(nrTowerLevels));
            labels.add("Tower " + i + " level");
        }

        for (int i = 0; i < nrDeployTypes; i++) {
            properties.add(getDeployValues(unitBufSize));
            labels.add("Deploy type " + i);
        }

        properties.add(getQLearnerTrustValues(qLearnerTrustSteps));
        labels.add("QLearner trust");

        // Only allow inputs where at most one unit is placed
        inputs = Sets.cartesianProduct(properties).stream().filter(l -> {
            int sum = 0;
            for (int i = nrTowers; i < nrTowers + nrDeployTypes; i++) {
                sum += l.get(i);
            }
            return sum == 1;
        }).collect(Collectors.toSet());

        return inputs;
    }

    public void writeCSV(FileOutputStream fos) throws IOException {
        String header = labels.stream().collect(Collectors.joining(",")) + "\n";
        fos.write(header.getBytes());
        for (List<Float> row : getInputs()) {
            String line = row.stream().map(Object::toString).collect(Collectors.joining(",")) + "\n";
            fos.write(line.getBytes());
        }

    }

    private static Set<Float> getQLearnerTrustValues(int steps) {
        return discretize(steps);
    }

    private static Set<Float> getTowerValues(int levels) {
        return discretize(levels);
    }

    private static Set<Float> discretize(int steps) {
        Set<Float> res = new HashSet<>(steps + 1);
        for (float i = 0; i <= steps; i++) {
            res.add(i / steps);
        }
        return res;
    }

    private static Set<Float> getDeployValues(int unitBufSize) {
        return discretize(unitBufSize);
    }
}
