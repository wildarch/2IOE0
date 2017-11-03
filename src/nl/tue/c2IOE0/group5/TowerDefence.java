package nl.tue.c2IOE0.group5;

import nl.tue.c2IOE0.group5.controllers.AiController;
import nl.tue.c2IOE0.group5.controllers.PlayerController;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.providers.*;
import nl.tue.c2IOE0.group5.util.Resource;

import java.io.IOException;
import java.util.Arrays;

/**
 * Main class of the game.
 * Prepares the engine and adds the relevant providers and controllers.
 * @author Jorren Hendriks
 */
public class TowerDefence {

    public static void main(String[] args) {
        boolean music = true;
        boolean trained = true;
        if(args.length > 0) {
            if(Arrays.stream(args).anyMatch(str -> str.equals("--no-music"))){
                music = false;
            }
            if(Arrays.stream(args).anyMatch(str -> str.equals("--untrained"))){
                trained = false;
            }
        }
        runGame(music, trained);
    }

    public static void runGame(boolean music, boolean trained) {
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
                new AiController(Resource.get(trained ? "res/networks/network_b10_f100_training_v2.zip" : "res/networks/network_b10_notraining_v2.zip"))
        });
        try {
            engine.run();
        } catch (IOException e) {
            throw new RuntimeException("IOException occured: " + e.getMessage());
        }
    }

}
