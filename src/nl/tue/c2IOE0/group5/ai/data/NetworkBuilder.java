package nl.tue.c2IOE0.group5.AI.Data;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.PreprocessorVertex;
import org.deeplearning4j.nn.conf.graph.SubsetVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToCnnPreProcessor;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * TowerDefence
 * Created by s154796 on 9-10-2017.
 */
public class NetworkBuilder {
    public static ComputationGraphConfiguration buildNetwork(int gridSize, int nrTowers, int nrDeployTypes, int iterations){
        return new NeuralNetConfiguration.Builder()
            .iterations(iterations)
            .learningRate(0.0005)
            .seed(123)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .weightInit(WeightInit.XAVIER)
            .updater(new Nesterovs(0.9))
            .graphBuilder()
            .pretrain(false)
            .backprop(true)
            .addInputs("in")
            .setInputTypes(InputType.feedForward(gridSize * gridSize * nrTowers + nrDeployTypes + 1))
            .addVertex("grid_ff", new SubsetVertex(0, gridSize * gridSize * nrTowers - 1), "in")
            .addVertex("grid", new PreprocessorVertex(new FeedForwardToCnnPreProcessor(gridSize, gridSize, nrTowers)), "grid_ff")
            .addVertex("deploy", new SubsetVertex(gridSize * gridSize * nrTowers, gridSize * gridSize * nrTowers + nrDeployTypes - 1), "in")
            .addVertex("qtrust", new SubsetVertex(gridSize * gridSize * nrTowers + nrDeployTypes, gridSize * gridSize * nrTowers + nrDeployTypes), "in")
            //Convolutional layers for grid
            .addLayer("convlayer1", new ConvolutionLayer.Builder().nIn(nrTowers).nOut(nrTowers).activation(Activation.RELU).build(), "grid")
            .addLayer("subsampling1", new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX).build(), "convlayer1")
//            .addLayer("convlayer2", new ConvolutionLayer.Builder().nOut(2 * nrTowers).activation(Activation.IDENTITY).build(), "subsampling1")
//            .addLayer("subsampling2", new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX).build(), "convlayer2")
            .addLayer("dense_grid1", new DenseLayer.Builder().nOut(100).activation(Activation.TANH).build(), "subsampling1")

            //feedforward for buffer
            .addLayer("ff_buffer1", new DenseLayer.Builder().nOut(50).activation(Activation.TANH).build(), "deploy")

            //buffer and q-trust
            .addLayer("ff_buffer_qtrust2", new DenseLayer.Builder().nOut(50).activation(Activation.RELU).build(), "ff_buffer1", "qtrust")

            //buffer, qtrust and grid layers
            .addLayer("total_ff1", new DenseLayer.Builder().nOut(100).activation(Activation.TANH).build(), "ff_buffer_qtrust2", "dense_grid1")
            .addLayer("total_ff2", new DenseLayer.Builder().nOut(100).activation(Activation.TANH).build(), "total_ff1")

            //output
            .addLayer("outputlayer", new OutputLayer.Builder(LossFunctions.LossFunction.MSE).nOut(1).activation(Activation.IDENTITY).build(), "total_ff2")

            .setOutputs("outputlayer")
            .build();
    }
}
