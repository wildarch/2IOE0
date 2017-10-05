package nl.tue.c2IOE0.group5.AI;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.transforms.Sin;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * TowerDefence
 * Created by s154796 on 2-10-2017.
 */
public class RegressionMathFunctions {

    //Random number generator seed, for reproducability
    public static final int seed = 12345;
    //Number of iterations per minibatch
    public static final int iterations = 2000;
    //Number of epochs (full passes of the data)
    public static final int nEpochs = 1;
    //How frequently should we plot the network output?
    public static final int plotFrequency = 1;
    //Number of data points
    public static final int nSamples = 1000;
    //Batch size: i.e., each epoch has nSamples/batchSize parameter updates
    public static final int batchSize = 100;
    //Network learning rate
    public static final double learningRate = 0.01;
    public static final Random rng = new Random(seed);
    public static final int numInputs = 2;
    public static final int numOutputs = 1;


    public static void main(final String[] args){
        //Initialize the user interface backend
        UIServer uiServer = UIServer.getInstance();

        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later

        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        //Then add the StatsListener to collect this information from the network, as it trains

        //Switch these two options to do different functions with different networks
        final MathFunction fn = new DiscreteMathFunction();
        final MultiLayerConfiguration conf = getDeepDenseLayerNetworkConfiguration();
        final ComputationGraphConfiguration graphConf = getComputationGraphNetworkConfiguration();

        //Generate the training data
        final INDArray x = Nd4j.linspace(-10,10,nSamples).reshape(nSamples, 1);



        final INDArray rand = Nd4j.rand(nSamples, 1);

        final INDArray total = Nd4j.create(nSamples, 2);
        total.putColumn(0, x);
        total.putColumn(1, rand);

        final DataSetIterator iterator = getTrainingData(total,fn,batchSize,rng);

        final ComputationGraph net = new ComputationGraph(graphConf);


        //Create the network
        //final MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(10), new StatsListener(statsStorage));

        //Train the network on the full data set, and evaluate in periodically
        final INDArray[] networkPredictions = new INDArray[nEpochs / plotFrequency];
        for( int i=0; i<nEpochs; i++ ){
            iterator.reset();
            net.fit(iterator);
            if((i+1) % plotFrequency == 0) networkPredictions[i/ plotFrequency] = net.output(total)[0];//net.output(total, false);
        }

        //Plot the target data and the network predictions
        plot(fn,x,fn.getFunctionValues(x),networkPredictions);
    }

    private static ComputationGraphConfiguration getComputationGraphNetworkConfiguration(){
        return new NeuralNetConfiguration.Builder()
            .seed(seed)
            .iterations(iterations)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .learningRate(learningRate)
            .weightInit(WeightInit.XAVIER)
            .updater(new Nesterovs(0.9))
            .graphBuilder()
            .addInputs("feedforward_in")
            .setInputTypes(InputType.feedForward(numInputs))
            .addLayer("dense1", new DenseLayer.Builder().nIn(numInputs).nOut(50).activation(Activation.TANH).build(), "feedforward_in")
            .addLayer("dense2", new DenseLayer.Builder().nIn(50).nOut(50).activation(Activation.TANH).build(), "dense1")
            .addLayer("dense3", new DenseLayer.Builder().nIn(numInputs).nOut(20).activation(Activation.TANH).build(), "feedforward_in")
            .addLayer("dense4", new DenseLayer.Builder().nIn(50 + 20).nOut(20).activation(Activation.RELU).build(), "dense2", "dense3")
            .addLayer("output", new OutputLayer.Builder(LossFunctions.LossFunction.MSE).nIn(20).nOut(1).activation(Activation.IDENTITY).build(), "dense4")
            .setOutputs("output")
            .pretrain(false).backprop(true)
            .build();
    }

    /** Returns the network configuration, 2 hidden DenseLayers of size 50.
     */
    private static MultiLayerConfiguration getDeepDenseLayerNetworkConfiguration() {
        final int numHiddenNodes = 50;
        return new NeuralNetConfiguration.Builder()
            .seed(seed)
            .iterations(iterations)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .learningRate(learningRate)
            .weightInit(WeightInit.XAVIER)
            .updater(new Nesterovs(0.9))
            .list()
            .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                .activation(Activation.TANH).build())
            .layer(1, new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                .activation(Activation.TANH).build())
            .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .activation(Activation.IDENTITY)
                .nIn(numHiddenNodes).nOut(numOutputs).build())
            .pretrain(false).backprop(true).build();
    }

    /** Create a DataSetIterator for training
     * @param x X values
     * @param function Function to evaluate
     * @param batchSize Batch size (number of examples for every call of DataSetIterator.next())
     * @param rng Random number generator (for repeatability)
     */
    private static DataSetIterator getTrainingData(final INDArray x, final MathFunction function, final int batchSize, final Random rng) {
        final INDArray y = function.getFunctionValues(x);
        final DataSet allData = new DataSet(x,y);

        final List<DataSet> list = allData.asList();
        Collections.shuffle(list,rng);
        return new ListDataSetIterator(list,batchSize);
    }

    //Plot the data
    private static void plot(final MathFunction function, final INDArray x, final INDArray y, final INDArray... predicted) {
        final XYSeriesCollection dataSet = new XYSeriesCollection();
        addSeries(dataSet,x,y,"True Function (Labels)");

        for( int i=0; i<predicted.length; i++ ){
            addSeries(dataSet,x,predicted[i],String.valueOf(i));
        }

        final JFreeChart chart = ChartFactory.createXYLineChart(
            "Regression Example - " + function.getName(),      // chart title
            "X",                        // x axis label
            function.getName() + "(X)", // y axis label
            dataSet,                    // data
            PlotOrientation.VERTICAL,
            true,                       // include legend
            true,                       // tooltips
            false                       // urls
        );

        final ChartPanel panel = new ChartPanel(chart);

        final JFrame f = new JFrame();
        f.add(panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.pack();

        f.setVisible(true);
    }

    private static void addSeries(final XYSeriesCollection dataSet, final INDArray x, final INDArray y, final String label){
        final double[] xd = x.getColumn(0).data().asDouble();
        final double[] yd = y.data().asDouble();
        final XYSeries s = new XYSeries(label);
        for( int j=0; j<xd.length; j++ ) s.add(xd[j],yd[j]);
        dataSet.addSeries(s);
    }
}

interface MathFunction {

    INDArray getFunctionValues(INDArray x);

    String getName();
}

class DiscreteMathFunction implements MathFunction {

    @Override
    public INDArray getFunctionValues(INDArray x) {
        INDArray out = Nd4j.create(x.rows(), 1);
        for (int r = 0; r < x.rows(); r++){
            out.putScalar(r, 0, (x.getDouble(r, 0) > 2 && x.getDouble(r, 0) < 7) || (x.getDouble(r, 0) < 0) ? 1 : 0);
//            for (int c = 0; c < x.columns(); c++){
//
//            }
        }
        return out;
    }

    @Override
    public String getName() {
        return "discrete";
    }
}

class SinXDivXMathFunction implements MathFunction {

    @Override
    public INDArray getFunctionValues(final INDArray x) {
        return Nd4j.getExecutioner().execAndReturn(new Sin(x.dup())).div(x);
    }

    @Override
    public String getName() {
        return "SinXDivX";
    }
}