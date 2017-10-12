package nl.tue.c2IOE0.group5;

import nl.tue.c2IOE0.group5.controllers.AiController;
import nl.tue.c2IOE0.group5.controllers.PlayerController;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.providers.*;

/**
 * @author Jorren Hendriks
 */
public class TowerDefence {

    public static void main(String[] args) {
        Engine e = new Engine(true);
        e.addProviders(new Provider[] {
                new TestProvider(),
                new EnemyProvider(),
                new GridProvider(),
                new UIProvider(),
                new BackgroundProvider(),
        });
        e.addControllers(new Controller[] {
                new PlayerController(),
                new AiController()
        });
        try {
            e.run();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

}
