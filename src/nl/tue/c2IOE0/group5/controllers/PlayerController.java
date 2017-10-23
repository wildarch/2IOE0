package nl.tue.c2IOE0.group5.controllers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Event;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Listener;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.providers.*;
import org.joml.Vector2i;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jorren Hendriks
 */
public class PlayerController implements Controller,Listener {

    // define required resources here, e.g.
    private Engine engine;
    private MenuProvider menuProvider;
    private UIProvider uiProvider;
    private GridProvider gridProvider;
    private TowerProvider towerProvider;
    private Camera camera;
    private Renderer renderer;
    private float oldx = 0;
    private float oldy = 0;
    private float sensitivity = 5;//Camera Sensitivity on a scale from 1 to 10
    private boolean rightMouseButton = false;
    private boolean middleMouseButton = false;
    private float ticksSinceTrigger = 0.000f;

    private float maxX;
    private float maxY;
    private float maxZ;

    private float minX;
    private float minY = 0.1f;
    private float minZ;
    private int budget = 1000;

    public PlayerController() {
    }

    @Override
    public void init(Engine engine) {
        // you can initialize resources here, e.g.
        this.engine = engine;
        this.menuProvider = engine.getProvider(MenuProvider.class);
        this.uiProvider = engine.getProvider(UIProvider.class);
        this.gridProvider = engine.getProvider(GridProvider.class);
        this.towerProvider = engine.getProvider(TowerProvider.class);
        this.camera = engine.getCamera();
        this.renderer = engine.getRenderer();
        //this.window = engine.getWindow();

        camera.setPosition(GridProvider.SIZE /2, 6f, GridProvider.SIZE /2);
        camera.setRotation(90, 90, 0);
        calculateXYZ();
    }

    private void calculateXYZ(){
        maxX = GridProvider.SIZE /2 + GridProvider.SIZE;
        maxY = GridProvider.SIZE;
        maxZ = GridProvider.SIZE /2 + GridProvider.SIZE;

        minX = GridProvider.SIZE /2 - GridProvider.SIZE;
        minZ = GridProvider.SIZE /2 - GridProvider.SIZE;
    }

    @Override
    public void update() {
        // you can use resources here, e.g.

    }

    @Override
    public void onKeyPressed(Event event) {
        if (engine.isPaused()) return;

        switch (event.getSubject()) {
            case GLFW_KEY_L:
                camera.setRotation(0,0,0);
                break;
            case GLFW_KEY_ESCAPE:
                engine.pause(true);
        }
    }

    @Override
    public void onKeyReleased(Event event) {
    }

    @Override
    public void onKeyHold(Event event) {
        if (engine.isPaused()) return;

        double frameTime = event.getSource().getFrameTime();
        float movement = (float)frameTime * 0.01f;
        switch (event.getSubject()) {
            case GLFW_KEY_A:
                moveRelativeLocal(-movement,0f,0f);
                break;
            case GLFW_KEY_D:
                moveRelativeLocal(movement,0f,0f);
                break;
            case GLFW_KEY_W:
                moveRelativeLocal(0f,0f, -movement);
                break;
            case GLFW_KEY_S:
                moveRelativeLocal(0f,0f, movement);
                break;
            case GLFW_KEY_SPACE:
                moveRelativeLocal(0f,movement,0f);
                break;
            case GLFW_KEY_LEFT_SHIFT:
                moveRelativeLocal(0f,-movement,0f);
                break;
        }
    }

    private PositionContainer getRelativeMovement(float offsetX, float offsetY, float offsetZ) {
        float nextXPosition = camera.getPosition().x;
        float nextYPosition = camera.getPosition().y;
        float nextZPosition = camera.getPosition().z;

        if ( offsetZ != 0 ) {
            nextXPosition += (float)Math.sin(Math.toRadians(camera.getRotation().y)) * -1.0f * offsetZ;
            nextZPosition += (float)Math.cos(Math.toRadians(camera.getRotation().y)) * offsetZ;
        }
        if ( offsetX != 0) {
            nextXPosition += (float)Math.sin(Math.toRadians(camera.getRotation().y - 90)) * -1.0f * offsetX;
            nextZPosition += (float)Math.cos(Math.toRadians(camera.getRotation().y - 90)) * offsetX;
        }
        nextYPosition += offsetY;

        return new PositionContainer(nextXPosition,nextYPosition,nextZPosition);
    }

    public void moveRelativeLocal(float offsetX, float offsetY, float offsetZ) {
        float XPosition = camera.getPosition().x;
        float YPosition = camera.getPosition().y;
        float ZPosition = camera.getPosition().z;

        if (offsetZ != 0) {
            if (getRelativeMovement(offsetX,offsetY,offsetZ).getX() <= maxX && getRelativeMovement(offsetX, offsetY, offsetZ).getX() >= minX) {
                XPosition += (float) Math.sin(Math.toRadians(camera.getRotation().y)) * -1.0f * offsetZ;
            }
            if (getRelativeMovement(offsetX,offsetY,offsetZ).getZ() <= maxZ && getRelativeMovement(offsetX, offsetY, offsetZ).getZ() >= minZ) {
                ZPosition += (float) Math.cos(Math.toRadians(camera.getRotation().y)) * offsetZ;
            }

        }

        if (offsetX != 0) {
            if (getRelativeMovement(offsetX,offsetY,offsetZ).getX() <= maxX && getRelativeMovement(offsetX, offsetY, offsetZ).getX() >= minX) {
                XPosition += (float) Math.sin(Math.toRadians(camera.getRotation().y - 90)) * -1.0f * offsetX;
            }
            if (getRelativeMovement(offsetX,offsetY,offsetZ).getZ() <= maxZ && getRelativeMovement(offsetX, offsetY, offsetZ).getZ() >= minZ) {
                ZPosition += (float) Math.cos(Math.toRadians(camera.getRotation().y - 90)) * offsetX;
            }
        }

        if (getRelativeMovement(offsetX, offsetY, offsetZ).getY() <= maxY && getRelativeMovement(offsetX, offsetY, offsetZ).getY() >= minY){
            YPosition += offsetY;
        }

        camera.setPosition(XPosition,YPosition,ZPosition);
    }

    public void moveLocal(float offsetX, float offsetY, float offsetZ) {
        float XPosition = camera.getPosition().x;
        float YPosition = camera.getPosition().y;
        float ZPosition = camera.getPosition().z;

        if (ZPosition + offsetZ <= maxZ && ZPosition + offsetZ >= minZ) {
            ZPosition += offsetZ;
        }

        if (XPosition + offsetX <= maxX && XPosition + offsetX >= minX) {
            XPosition += offsetX;
        }

        if (YPosition + offsetY <= maxY && YPosition + offsetY >= minY){
            YPosition += offsetY;
        }

        camera.setPosition(XPosition,YPosition,ZPosition);
    }

    @Override
    public void onMouseButtonPressed(MouseEvent event) {
        if (event.getSubject() == GLFW_MOUSE_BUTTON_1) {
            if (engine.isPaused()) {
                menuProvider.onClick(event);
            } else {
                if (uiProvider.onClick(event)) {
                    gridProvider.click();
                    try {
                        if (uiProvider.getSelected() != null) {
                            Vector2i pos = gridProvider.getActiveCell();
                            towerProvider.buildTower(pos.x, pos.y, uiProvider.getSelected());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
        }

        if (event.getSubject() == GLFW_MOUSE_BUTTON_2 || event.getSubject() == GLFW_MOUSE_BUTTON_3) {
            //System.out.println("M at (" + event.getX() + ", " + event.getY() + ")");
            rightMouseButton = true;
            middleMouseButton = true;

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
        if (event.getSubject() == GLFW_MOUSE_BUTTON_2 || event.getSubject() == GLFW_MOUSE_BUTTON_3) {
            //System.out.println("M at (" + event.getX() + ", " + event.getY() + ")");www
            rightMouseButton = false;
            middleMouseButton = false;
        }
    }

    @Override
    public void onMouseMove(MouseEvent event) {
        if (engine.isPaused()) return;

        gridProvider.recalculateActiveCell(new Vector2i(event.getX(), event.getY()), camera, renderer, event.getSource());
        //Get current values
        if (rightMouseButton || middleMouseButton) {
            float x = event.getX();
            float y = event.getY();

            float deltaxMouse = (x - oldx) * (sensitivity / 10);
            float deltayMouse = (y - oldy) * (sensitivity / 10);

            //If Y-axis rotation is threatening to backflip the camera, prevent it from rotating around the y-axis
            if ((camera.getRotation().x() + deltayMouse <= 90) && (camera.getRotation().x() + deltayMouse > -35)){
                camera.rotate(deltayMouse, deltaxMouse, 0);
            } else {
                camera.rotate(0,deltaxMouse,0);
            }

            //Set new old values
            oldx = x;
            oldy = y;
        }
    }

    @Override
    public void onMouseHover(MouseEvent event) {
        if(true) return;

        if (engine.isPaused()) return;

        Window window = event.getSource();

        float percDistFromEdge = 0.07f;
        float distFromEdge = percDistFromEdge*window.getWidth();
        float speed = (float)window.getFrameTime();
        float sensitivityScroll = 0.001f*speed * ticksSinceTrigger;

        //x mouse location
        float xMouse = event.getX();
        /*
        if (xMouse <= 0){xMouse=1;}
        if (xMouse >= window.getWidth()){xMouse = window.getWidth()-1;}
        if (yMouse <= 0){yMouse=1;}
        if (yMouse >= window.getHeight()){yMouse = window.getHeight()-1;}
        */

        //y mouse location
        float yMouse = event.getY();

        if (!(xMouse > distFromEdge && xMouse <= window.getWidth()-distFromEdge && yMouse > distFromEdge && yMouse
                <= window.getHeight()-distFromEdge) && ticksSinceTrigger<=120){
            ticksSinceTrigger += 0.001f;
        }

        if (xMouse > distFromEdge && xMouse <= window.getWidth()-distFromEdge && yMouse > distFromEdge && yMouse <= window.getHeight()-distFromEdge){
            ticksSinceTrigger = 0;
        }

        //Scroll left if mouse is within border range
        if (xMouse <= distFromEdge && xMouse > 0) {
            moveRelativeLocal(-(distFromEdge - xMouse) * sensitivityScroll, 0, 0);
        }

        //Scroll right if mouse is within border range
        if (xMouse >= window.getWidth() - distFromEdge && xMouse < window.getWidth()) {
            moveRelativeLocal((xMouse - (window.getWidth() - distFromEdge)) * sensitivityScroll, 0, 0);
        }

        //Scroll up if mouse is within border range
        if (yMouse <= distFromEdge && yMouse > 0) {
            moveRelativeLocal(0, 0,  -(distFromEdge - yMouse) * sensitivityScroll);
        }

        //Scroll down if mouse is within border range
        if (yMouse >= window.getHeight() - distFromEdge && yMouse < window.getHeight()) {
            moveRelativeLocal(0, 0, (yMouse - (window.getHeight() - distFromEdge)) * sensitivityScroll);
        }
    }

    @Override
    public void onMouseScroll(MouseEvent event) {
        if (engine.isPaused()) return;
        Vector3f directionOfCamera = gridProvider.getDirectionOfCamera(renderer, event.getSource(), 0, 0);
        float speed = 0.04f;
        moveLocal((event.getY()) * directionOfCamera.x()*speed,(event.getY()) * directionOfCamera.y()*speed,(event.getY()) * directionOfCamera.z()*speed);
    }

    public int getBudget() {
        return budget;
    }

    public void addBudget(int n) {
        budget += n;
    }

    private class PositionContainer{
        private float x;
        private float y;
        private float z;

        public PositionContainer(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float getX(){
            return x;
        }
        public float getY(){
            return y;
        }
        public float getZ(){
            return z;
        }
    }
}
