package nl.tue.c2IOE0.group5.AI;

import org.nd4j.linalg.dataset.api.MultiDataSet;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * TowerDefence
 * Created by s154796 on 12-10-2017.
 */
public class TrainingManager extends JFrame{
    TacticalTrainer trainer = null;
    MultiDataSet data = null;
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
