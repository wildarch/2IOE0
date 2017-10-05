package nl.tue.c2IOE0.group5.controllers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Event;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Listener;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.providers.TestProvider;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jorren Hendriks
 */
public class TestController implements Controller,Listener {

    // define required resources here, e.g.
    private TestProvider testProvider;
    private GridProvider gridProvider;
    private Camera camera;
    private Renderer renderer;
    private Window window;
    private boolean spacebarPressed = false;

    @Override
    public void init(Engine engine) {
        // you can initialize resources here, e.g.
        this.testProvider = engine.getProvider(TestProvider.class);
        this.gridProvider = engine.getProvider(GridProvider.class);
        this.camera = engine.getCamera();
        this.renderer = engine.getRenderer();
        this.window = engine.getWindow();

        camera.setPosition(gridProvider.SIZE/2, 12f, gridProvider.SIZE/2);
        camera.setRotation(90, 90, 0);
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
                if (spacebarPressed){
                    camera.rotate(0f, -4f, 0f);
                }
                else {
                    camera.moveRelative(-1f, 0f, 0f);
                }
                break;
            case GLFW_KEY_D:
                //camera.setRotation(0, -90, 0);
                if (spacebarPressed){
                    camera.rotate(0f, 4f, 0f);
                }
                else {
                    camera.moveRelative(1f, 0f, 0f);
                }
                break;
            case GLFW_KEY_S:
                //camera.setRotation(0, 180, 0);
                if (spacebarPressed){
                    camera.rotate(4f, 0f, 0f);
                }
                else {
                    camera.moveRelative(0f, -1f, 0f);
                }
                break;
            case GLFW_KEY_W:
                //camera.setRotation(0, 0, 0);
                if (spacebarPressed){
                    camera.rotate(-4f, 0f, 0f);
                }
                else {
                    camera.moveRelative(0f, 1f, 0f);
                }
                break;
            case GLFW_KEY_Z:
                camera.moveRelative(0f, 0f, 1f);
                break;
            case GLFW_KEY_X:
                camera.moveRelative(0f, 0f, -1f);
            case GLFW_KEY_SPACE:
                spacebarPressed = true;
                break;
            case GLFW_KEY_ESCAPE:
                camera.setRotation(0f, 0f, 0f);
                camera.setPosition(0f, 0f, 0f);
        }
    }

    @Override
    public void onKeyReleased(Event event) {
        switch (event.getSubject()) {
            case GLFW_KEY_SPACE:
                spacebarPressed = false;
                break;
        }
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
        gridProvider.mouseMoved(event, camera, renderer, window);
    }

}
