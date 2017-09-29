package nl.tue.c2IOE0.group5.controllers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Event;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Listener;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.providers.TestProvider;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jorren Hendriks
 */
public class TestController implements Controller,Listener {

    // define required resources here, e.g.
    private TestProvider testProvider;
    private Camera camera;

    @Override
    public void init(Engine engine) {
        // you can initialize resources here, e.g.
        this.testProvider = engine.getProvider(TestProvider.class);
        this.camera = engine.getCamera();
    }

    @Override
    public void update() {
        // you can use resources here, e.g.

    }

    @Override
    public void onKeyPressed(Event event) {

        switch (event.getSubject()) {
            case GLFW_KEY_A:
                //camera.setRotation(0, 90, 0);
                camera.moveRelative(-1f, 0f, 0f);
                break;
            case GLFW_KEY_D:
                //camera.setRotation(0, -90, 0);
                camera.moveRelative(1f, 0f, 0f);
                break;
            case GLFW_KEY_S:
                //camera.setRotation(0, 180, 0);
                camera.moveRelative(0f, 0f, 1f);
                break;
            case GLFW_KEY_W:
                //camera.setRotation(0, 0, 0);
                camera.moveRelative(0f, 0f, -1f);
                break;
        }
    }

    @Override
    public void onKeyReleased(Event event) {

    }

    @Override
    public void onMouseButtonPressed(MouseEvent event) {
        if (event.getSubject() == GLFW_MOUSE_BUTTON_1) {
            System.out.println("Click at (" + event.getX() + ", " + event.getY() + ")");
            this.testProvider.ud();
        }
    }

    @Override
    public void onMouseButtonReleased(MouseEvent event) {

    }

    @Override
    public void onMouseMove(MouseEvent event) {

    }

}
