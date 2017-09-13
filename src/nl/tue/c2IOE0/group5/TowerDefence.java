package nl.tue.c2IOE0.group5;

import nl.tue.c2IOE0.group5.controllers.TestController;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.providers.TestProvider;

/**
 * @author Jorren Hendriks
 */
public class TowerDefence {

    public static void main(String[] args) {
        Engine e = new Engine();
        e.addProviders(new Provider[] {
                new TestProvider(),
        });
        e.addControllers(new Controller[] {
                new TestController(),
        });
        e.run();
    }

}
