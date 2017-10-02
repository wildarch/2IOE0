package nl.tue.c2IOE0.group5.AI;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * TowerDefence
 * Created by s154796 on 29-9-2017.
 */
public class NNDataTest {

    public static void main(String[] args){

        //Initialize the user interface backend
        UIServer uiServer = UIServer.getInstance();

        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later

        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        //Then add the StatsListener to collect this information from the network, as it trains

        INDArray input = Nd4j.zeros(100, 3);
        INDArray labels = Nd4j.zeros(100, 1);

        for (int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                input.putScalar(i * 10 + j, 0, i);
                input.putScalar(i * 10 + j, 1, Math.random());
                input.putScalar(i * 10 + j, 2, j);
                labels.putScalar(i * 10 + j, 0, (i > 2 && i < 7) ? 1 : 0);
                //labels.putScalar(i * 10 + j, 1, (i > 2 && i < 7) ? 0 : 1);
            }
        }

        // create dataset object
        DataSet ds = new DataSet(input, labels);

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .iterations(2000)
            .learningRate(0.01)
            .seed(2256)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .updater(new Nesterovs(0.9))
            .weightInit(WeightInit.XAVIER)
            .list()
            .pretrain(false)
            .backprop(true)
            .layer(0, new DenseLayer.Builder().nIn(3).nOut(5).activation(Activation.TANH).build())
            .layer(1, new DenseLayer.Builder().nIn(5).nOut(5).activation(Activation.TANH).build())
            .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE).nIn(5).nOut(1).activation(Activation.IDENTITY).build())
            .build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        net.addListeners(new ScoreIterationListener(10), new StatsListener(statsStorage));

        net.fit(ds);

        INDArray featureMatrix = ds.getFeatureMatrix();
        INDArray out = net.output(featureMatrix);
        for (int row = 0; row < featureMatrix.rows(); row++){
            INDArray r = featureMatrix.getRow(row);
            INDArray o = out.getRow(row);
            System.out.print(r);
            System.out.print(" - ");
            System.out.println(o);

        }

//        INDArray output = net.output(ds.getFeatureMatrix());
//        System.out.println(output);
//
//        Evaluation eval = new Evaluation(2);
//        eval.eval(ds.getLabels(), output);
//        System.out.println(eval.stats());

    }
}