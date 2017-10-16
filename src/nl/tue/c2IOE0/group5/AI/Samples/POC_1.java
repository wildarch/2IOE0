package nl.tue.c2IOE0.group5.AI.Samples;

import nl.tue.c2IOE0.group5.AI.Data.InputGenerator;
import nl.tue.c2IOE0.group5.AI.TacticalTrainer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * TowerDefence PROOF OF CONCEPT AI
 * Created by s154796 on 9-10-2017.
 */
public class POC_1 {

    public static void main(String[] args){
        //Loader.load(cudnn.class);

        int gridSize = 9, nrTowers = 5, nrDeployTypes = 5;
        int numInputs = 10000, iterations = 10;
        File f = new File("test.dat");
        
        TacticalTrainer trainer = TacticalTrainer.FromGeneratedModel(9, 5, 5, iterations);
        DataSetIterator data;

//        try {
//            data = InputGenerator.fromFile(f);
//
//            //System.out.println(Arrays.toString(set.getFeatures()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        System.out.println("generating data...");

        data = InputGenerator.getTrainingData(InputGenerator.getTrainingData(numInputs, gridSize, nrTowers, 5, nrDeployTypes, 5, input -> {
            INDArray output = Nd4j.zeros(numInputs, 1);

            for (int i = 0; i < numInputs; i++){
                double res = 0;
                for (int j = gridSize * gridSize * nrTowers; j < gridSize * gridSize * nrTowers + nrDeployTypes; j++){
                    if (input.getDouble(i, j) > 1.0 / nrDeployTypes){
                        res += 1.0 / nrDeployTypes;
                    }
                }

                output.putScalar(i, 0, res * input.getDouble(i, gridSize * gridSize * nrTowers + nrDeployTypes));
                //output.putScalar(i, 0, 0);
                System.out.println(Math.round(i * 100.0 / (double)numInputs) + "%");
            }

            return output;
        }), 50, new Random());

        System.out.println("data generated!");
//        System.out.println("writing to file: " + f.toString());
//
//        try {
//            InputGenerator.export(f, data);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("done writing!");

        for (int i = 0; i < 200; i ++){
            trainer.fit(data, 1);
            System.out.println("results: ");
            data.reset();
            double tDiff = 0;
            int c = 20;
            for(int k = 0; k < c; k++){
                List<DataSet> dList = data.next().asList();
                DataSet ds = dList.get((int) (Math.random() * dList.size()));
                INDArray in = ds.getFeatures();
                INDArray expected = ds.getLabels();
                //System.out.println("input: " + Arrays.toString(in));
                INDArray[] result = trainer.eval(in);
                System.out.println("output: " + Arrays.toString(result));
                System.out.println("expected: " + expected);

                double diff = Math.abs(expected.getDouble(0, 0) - result[0].getDouble(0, 0));
                tDiff += diff;

                System.out.println("diff: " + diff);
            }
            System.out.println("avgDiff: " + tDiff / c);

        }
        //trainer.fit(data, 1);


    }
}
