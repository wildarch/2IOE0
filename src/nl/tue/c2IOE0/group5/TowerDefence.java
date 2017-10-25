package nl.tue.c2IOE0.group5;

import nl.tue.c2IOE0.group5.controllers.AiController;
import nl.tue.c2IOE0.group5.controllers.PlayerController;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.providers.*;

import java.io.File;
import java.io.IOException;

/**
 * @author Jorren Hendriks
 */
public class TowerDefence {

    public static void main(String[] args) {
        boolean music = true;
        if(args.length > 0) {
            if(args[0].equals("--no-music")) {
                music = false;
            }
        }
        runGame(music);
    }

    public static void runGame(boolean music) {
        Engine engine = new Engine();
        engine.addProviders(new Provider[] {
                new MenuProvider(),
                new EnemyProvider(),
                new AnimationProvider(),
                new GridProvider(),
                new BackgroundProvider(),
                new TowerProvider(),
                new UIProvider(),
                new BulletProvider(),
                new MusicProvider(),
                new TowerConnectionProvider()
        });
        if (!music) {
            engine.getProvider(MusicProvider.class).toggle();
        }
        engine.addControllers(new Controller[] {
                new PlayerController(),
                new AiController(new File("res/networks/network_b10_training.zip"))
        });
        try {
            engine.run();
        } catch (IOException e) {
            throw new RuntimeException("IOException occured: " + e.getMessage());
        }
    }

}
