package nl.tue.c2IOE0.group5.controllers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Event;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Listener;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.providers.TestProvider;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

/**
 * @author Jorren Hendriks
 */
public class TestController implements Controller,Listener {

    // define required resources here, e.g.
    private TestProvider testProvider;

    @Override
    public void init(Engine engine) {
        // you can initialize resources here, e.g.
        this.testProvider = (TestProvider) engine.getProvider("Test");
    }

    @Override
    public void update() {
        // you can use resources here, e.g.
        String testProviderName = this.testProvider.getName();
    }

    @Override
    public void onKeyPressed(Event event) {
        if (event.getSubject() == GLFW_KEY_W) {
            System.out.println("You pressed W");
        }
    }

    @Override
    public void onKeyReleased(Event event) {
        if (event.getSubject() == GLFW_KEY_W) {
            System.out.println("You released W");
        }
    }

    @Override
    public void onMouseButtonPressed(MouseEvent event) {
        if (event.getSubject() == GLFW_MOUSE_BUTTON_1) {
            System.out.println("Click at (" + event.getX() + ", " + event.getY() + ")");
        }
    }

    @Override
    public void onMouseButtonReleased(MouseEvent event) {

    }

    @Override
    public void onMouseMove(MouseEvent event) {

    }

}
