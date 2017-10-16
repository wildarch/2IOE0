package nl.tue.c2IOE0.group5.AI;

import nl.tue.c2IOE0.group5.AI.Data.InputGenerator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * TowerDefence
 * Created by s154796 on 12-10-2017.
 */
public class TrainingManager extends JFrame{
    TacticalTrainer trainer = null;
    DataSet data = null;
    boolean trainerActive = false;

    JPanel btnPanel = new JPanel();
    JPanel statusPanel = new JPanel();

    JButton loadNetworkBtn = new JButton("Load NN");
    JButton saveNetworkBtn = new JButton("Save NN");
    JButton generateNetworkBtn = new JButton("Generate NN");
    JButton trainBtn = new JButton("Train");
    JButton stopTrainBtn = new JButton("Stop Training");
    JButton loadDataBtn = new JButton("Load Data");
    JButton generateDataBtn = new JButton("Generate Data");
    JButton saveDataBtn = new JButton("Save Data");

    JFileChooser modelSelector = new JFileChooser();
    JFileChooser dataSelector = new JFileChooser();

    public TrainingManager(){
        super("Training Manager");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);

        loadNetworkBtn.addActionListener(a -> {
            if(modelSelector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                try {
                    trainer = TacticalTrainer.FromFile(modelSelector.getSelectedFile());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                setEnabled();
            }
        });

        saveNetworkBtn.addActionListener(a -> {
            if(modelSelector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                try {
                    trainer.saveNetwork(modelSelector.getSelectedFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setEnabled();
            }
        });

        generateNetworkBtn.addActionListener(a -> {
            trainer = TacticalTrainer.FromGeneratedModel(9, 5, 5, 50);
            setEnabled();
        });

        loadDataBtn.addActionListener(a -> {
            if(dataSelector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                try {
                    data = InputGenerator.fromFile(dataSelector.getSelectedFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setEnabled();
            }
        });

        saveDataBtn.addActionListener(a -> {
            if(dataSelector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                try {
                    InputGenerator.export(dataSelector.getSelectedFile(), data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setEnabled();
            }
        });

        generateDataBtn.addActionListener(a -> {
            int numInputs = 10000;
            int gridSize = 9;
            int nrTowers = 5;
            int nrDeployTypes = 5;
            int nrTowerLevels = 5;
            data = InputGenerator.getTrainingData(numInputs, gridSize, nrTowers, nrTowerLevels, nrDeployTypes, 5, input -> {
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
            });
            setEnabled();
        });

        setEnabled();

        btnPanel.add(loadNetworkBtn);
        btnPanel.add(saveNetworkBtn);
        btnPanel.add(generateNetworkBtn);
        btnPanel.add(trainBtn);
        btnPanel.add(stopTrainBtn);

        btnPanel.add(loadDataBtn);
        btnPanel.add(saveDataBtn);
        btnPanel.add(generateDataBtn);

        add(btnPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.CENTER);

        pack();
    }

    void setEnabled(){
        saveDataBtn.setEnabled(data != null);
        saveNetworkBtn.setEnabled(trainer != null);
        trainBtn.setEnabled(data != null && trainer != null && !trainerActive);
        stopTrainBtn.setEnabled(trainerActive);
    }


    public void showUI(){
        this.setVisible(true);
    }

    public static void main(String[] args){
        TrainingManager tm = new TrainingManager();
        tm.showUI();
    }
}
