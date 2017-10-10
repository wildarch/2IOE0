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
import nl.tue.c2IOE0.group5.providers.UIProvider;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jorren Hendriks
 */
public class PlayerController implements Controller,Listener {

    // define required resources here, e.g.
    private UIProvider uiProvider;
    private GridProvider gridProvider;
    private Camera camera;
    private Renderer renderer;
    private Window window;
    private float oldx = 0;
    private float oldy = 0;
    private float sensitivity = 5;//Camera Sensitivity on a scale from 1 to 10
    private boolean MiddleMouseButton = false;

    @Override
    public void init(Engine engine) {
        // you can initialize resources here, e.g.
        this.uiProvider = engine.getProvider(UIProvider.class);
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
            case GLFW_KEY_L:
                camera.setRotation(0,0,0);
                break;
            case GLFW_KEY_T:
                float xRotation = (camera.getRotation().x());
                float yRotation = (camera.getRotation().y())%360;

                float unitVX = (float)(Math.sin((yRotation*Math.PI)/180))*((float)(Math.sin((xRotation*Math.PI)/180)));
                float unitVY = (float)Math.sin((xRotation*Math.PI)/180);
                float unitVZ = (float)(Math.cos((yRotation*Math.PI)/180))*((float)(Math.sin((xRotation*Math.PI)/180)));

                System.out.println("XRotation: " + Math.sin((xRotation*Math.PI)/180));
                System.out.println("YRotation: " + Math.cos((yRotation*Math.PI)/180));

                System.out.println("VX: " + unitVX);
                System.out.println("VY: " + unitVY);
                System.out.println("VZ: " + unitVZ);

                camera.move(unitVX/2,-unitVY/2,-unitVZ/2);
                break;
        }
    }

    @Override
    public void onKeyReleased(Event event) {

    }

    @Override
    public void onKeyHold(Event event) {
        double frameTime = event.getSource().getFrameTime();
        float speed = (float)frameTime * 0.01f;
        switch (event.getSubject()) {
            case GLFW_KEY_A:
                camera.moveRelative(-speed, 0f, 0f);
                break;
            case GLFW_KEY_D:
                camera.moveRelative(speed, 0f, 0f);
                break;
            case GLFW_KEY_W:
                camera.moveRelative(0f, 0f, -speed);
                break;
            case GLFW_KEY_S:
                camera.moveRelative(0f, 0f, speed);
                break;
            case GLFW_KEY_SPACE:
                camera.moveRelative(0f, speed, 0f);
                break;
            case GLFW_KEY_LEFT_SHIFT:
                camera.moveRelative(0f, -speed, 0f);
                break;
        }
    }

    @Override
    public void onMouseButtonPressed(MouseEvent event) {
        if (event.getSubject() == GLFW_MOUSE_BUTTON_1) {
            if (uiProvider.onClick(event)) {
                System.out.println("Click at (" + event.getX() + ", " + event.getY() + ")");
            }
        }

        if (event.getSubject() == GLFW_MOUSE_BUTTON_2) {
            //System.out.println("M at (" + event.getX() + ", " + event.getY() + ")");
            MiddleMouseButton = true;

            /******************************************************************************************\
            Set current location as the start for the delta X and Y. Otherwise it will use outdated data
            and think you'll have moveD in-between pressing releasing middle mouse and pressing it again.
            \******************************************************************************************/
            oldx = event.getX();
            oldy = event.getY();
        }
    }

    @Override
    public void onMouseButtonReleased(MouseEvent event) {
        if (event.getSubject() == GLFW_MOUSE_BUTTON_2) {
            //System.out.println("M at (" + event.getX() + ", " + event.getY() + ")");www
            MiddleMouseButton = false;
        }
    }

    @Override
    public void onMouseMove(MouseEvent event) {
        gridProvider.recalculateActiveCell(new Vector2i(event.getX(), event.getY()), camera, renderer, window);
        //Get current values
        if (MiddleMouseButton) {
            float x = event.getX();
            float y = event.getY();

            float deltaxMouse = (x - oldx) * (sensitivity / 10);
            float deltayMouse = (y - oldy) * (sensitivity / 10);

            if ((camera.getRotation().x() + deltayMouse <= 90) && (camera.getRotation().x() + deltayMouse > -35)){
                camera.rotate(deltayMouse, deltaxMouse, 0);
            }

            //Set new old values
            oldx = x;
            oldy = y;
        }
    }

    @Override
    public void onMouseScroll(MouseEvent event) {
        float scrollSpeed = event.getY()/10;
        float xRotation = (camera.getRotation().x());
        float yRotation = (camera.getRotation().y())%360;

        float unitVX = (float)Math.sin((yRotation*Math.PI)/180);
        float unitVY = (float)Math.sin((xRotation*Math.PI)/180);
        float unitVZ = (float)(Math.cos((yRotation*Math.PI)/180) + Math.cos((yRotation*Math.PI)/180))/2;


        camera.move(unitVX * scrollSpeed,-unitVY * scrollSpeed,-unitVZ * scrollSpeed);
    }

}
