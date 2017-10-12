package nl.tue.c2IOE0.group5.AI.Samples;

import nl.tue.c2IOE0.group5.AI.Data.InputGenerator;
import nl.tue.c2IOE0.group5.AI.TacticalTrainer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.util.Arrays;

/**
 * TowerDefence PROOF OF CONCEPT AI
 * Created by s154796 on 9-10-2017.
 */
public class POC_1 {

    public static void main(String[] args){
        int gridSize = 9, nrTowers = 5, nrDeployTypes = 5;
        int numInputs = 10000, iterations = 50;
        File f = new File("test.dat");
        
        TacticalTrainer trainer = TacticalTrainer.FromGeneratedModel(9, 5, 5, iterations);
        MultiDataSet data = null;



//        try {
//            data = InputGenerator.fromFile(f);
//
//            //System.out.println(Arrays.toString(set.getFeatures()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        System.out.println("generating data...");

        data = InputGenerator.getTrainingData(numInputs, gridSize, nrTowers, 5, nrDeployTypes, 5, input -> {
            INDArray output = Nd4j.zeros(numInputs, 1);

            for (int i = 0; i < numInputs; i++){
                double res = 0;
                for (int j = 0; j < nrDeployTypes; j++){
                    if (input[1].getDouble(i, j) > 1.0 / nrDeployTypes){
                        res += 1.0 / nrDeployTypes;
                    }
                }
                output.putScalar(i, 0, res * input[2].getDouble(i, 0));
                System.out.println(Math.round(i * 100.0 / (double)numInputs) + "%");
            }

            return output;
        });

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
            for(int k = 0; k < 5; k++){
                MultiDataSet ds = data.asList().get((int) (Math.random() * numInputs));
                INDArray[] in = ds.getFeatures();
                INDArray[] expected = ds.getLabels();
                //System.out.println("input: " + Arrays.toString(in));
                INDArray[] result = trainer.eval(in);
                System.out.println("output: " + Arrays.toString(result));
                System.out.println("expected: " + Arrays.toString(expected));
            }

        }
        //trainer.fit(data, 1);


    }
}
