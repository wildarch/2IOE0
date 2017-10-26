package nl.tue.c2IOE0.group5.controllers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.enemies.EnemyType;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Event;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Listener;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.providers.*;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.CannonTower;
import nl.tue.c2IOE0.group5.towers.RocketTower;
import nl.tue.c2IOE0.group5.towers.WallTower;
import nl.tue.c2IOE0.group5.util.Angle;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

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
    private EnemyProvider enemyProvider;
    private Camera camera;
    private Renderer renderer;

    private float oldx = 0;
    private float oldy = 0;
    private float oldmx = 0;
    private float oldmy = 0;
    private float oldaccumulatedx = 180;
    private float oldaccumulatedy = 15;
    private float sensitivity = 5;//Camera Sensitivity on a scale from 1 to 10
    private boolean rightMouseButton = false;
    private boolean middleMouseButton = false;
    private boolean freeCameraMode = true;
    private boolean lockedCameraMode = false;
    private int invertedXaxis;
    private boolean ctrl = false;
    private boolean leftMouseButton = false;

    private Class<? extends AbstractTower> prevTower;
    private Vector2i prevpos;

    private float gCentreX = 0;
    private float gCentreY = 0;
    private double cameraDiameter = 0;
    private double cameraDistance;

    //Keeps track of time spend on edge of screen, used to accelerate the camera in the edge direction. The longer the edge is hovered over the faster it moves.
    private float ticksSinceTrigger = 0.000f;
    float accumulatedx = 180;
    float accumulatedy = 15;
    private float oldangley = 0;

    private float maxX;
    private float maxY;
    private float maxZ;

    private float minX;
    private float minY = 0.1f;
    private float minZ;
    private int budget = 100;

    public PlayerController() {
    }

    public void toggleCameraMode(){
        lockedCameraMode = !lockedCameraMode;
        freeCameraMode = !freeCameraMode;
        System.out.println("freeCameraMode: " + freeCameraMode);

        accumulatedx = 180;
        oldaccumulatedx = 180;
        accumulatedy = 15;
        oldaccumulatedy = 15;
        cameraDistance = gridProvider.SIZE/2;
        oldangley = 0;

        gCentreX = gridProvider.SIZE/2;
        gCentreY = gridProvider.SIZE/2;

        camera.setPosition(gridProvider.SIZE/2, 2f, gridProvider.SIZE);
        camera.setRotation(accumulatedy, 0, 0);
    }

    @Override
    public void init(Engine engine) {
        // you can initialize resources here, e.g.
        this.engine = engine;
        try {
            this.menuProvider = engine.getProvider(MenuProvider.class);
            this.uiProvider = engine.getProvider(UIProvider.class);
        } catch (IllegalArgumentException e) {
            System.err.println("No menu or ui provider");
        }
        this.gridProvider = engine.getProvider(GridProvider.class);
        this.towerProvider = engine.getProvider(TowerProvider.class);
        this.enemyProvider = engine.getProvider(EnemyProvider.class);

        cameraDistance = gridProvider.SIZE/2;
        invertedXaxis = engine.getWindow().getOptions().invertedXAxis;

        this.camera = engine.getCamera();
        this.renderer = engine.getRenderer();

        camera.setPosition(gridProvider.SIZE/2, 2f, gridProvider.SIZE);
        camera.setRotation(accumulatedy, 0, 0);

        calculateXYZ();

        float xDiff = gCentreX - camera.getPosition().x();
        float zDiff = gCentreY - camera.getPosition().z();
        cameraDiameter = (float)Math.sqrt(Math.pow(xDiff,2) + Math.pow(zDiff,2));

        gCentreX = gridProvider.SIZE/2;
        gCentreY = gridProvider.SIZE/2;
    }

    private void calculateXYZ(){

        maxX = gridProvider.SIZE/2 + gridProvider.SIZE*2;
        maxY = gridProvider.SIZE*2;
        maxZ = gridProvider.SIZE/2 + gridProvider.SIZE*2;

        minX = gridProvider.SIZE/2 - gridProvider.SIZE*2;
        minZ = gridProvider.SIZE/2 - gridProvider.SIZE*2;

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
                break;
            case GLFW_KEY_H:
                engine.toggleHud();
                break;
            case GLFW_KEY_1:
                uiProvider.select(CannonTower.class);
                break;
            case GLFW_KEY_2:
                uiProvider.select(RocketTower.class);
                break;
            case GLFW_KEY_3:
                uiProvider.select(WallTower.class);
                break;
            case GLFW_KEY_B:
                addBudget(1000000);
                break;
        }
    }

    @Override
    public void onKeyReleased(Event event) {
        switch (event.getSubject()) {
            case GLFW_KEY_C:
                //toggleCameraMode();
                break;
            case GLFW_KEY_LEFT_CONTROL:
                uiProvider.select(null);
                break;
            case GLFW_KEY_R:
                //resetGame();
                break;
            case GLFW_KEY_P:
                //gridProvider.getCell(6,6).getTower().die();
                break;
        }
    }

    public void resetGame(){
        System.out.println("Resetting Game");
        //Reset Playing field
        for (int x = 0; x <= gridProvider.SIZE-1; x++ ) {
            for (int y = 0; y <= gridProvider.SIZE-1; y++ ) {
                if (gridProvider.getCell(x, y).getTower() != null && gridProvider.getCell(x,y).getTower().getType().getValue() != 0){ //Don't delete main Tower
                    gridProvider.getCell(x,y).getTower().die();
                }
            }
        }

        //Set all parameters for the player back to init
        if (gridProvider.getCell(6,6).getTower() != null) {
            gridProvider.getCell(6, 6).getTower().setHealth(200);
        }
        towerProvider.buildTower(6,6, towerProvider.getMainTower().getClass());

        update();
        engine.setScoreTimer();

        gridProvider.resetKills(EnemyType.DRILL);
        gridProvider.resetKills(EnemyType.BASIC);
        gridProvider.resetKills(EnemyType.WALKER);

        //Set all parameters for the AI back to init.
        List<Enemy> enemies = enemyProvider.getEnemies();
        for (int i = 0; i<enemies.size(); i++){
            enemies.get(i).die();
        }
        engine.getController(AiController.class).resetAI();
        engine.getController(AiController.class).startGame();

        budget = 100;

        //Back to the menu Screen
        engine.pause(true);
    }

    @Override
    public void onKeyHold(Event event) {
        if (engine.isPaused()) return;
            if (freeCameraMode) {
            double frameTime = event.getSource().getFrameTime();
            float movement = (float)frameTime * 0.01f;
            switch (event.getSubject()) {
                case GLFW_KEY_A:
                    moveRelativeLocal(-movement, 0f, 0f);
                    break;
                case GLFW_KEY_D:
                    moveRelativeLocal(movement, 0f, 0f);
                    break;
                case GLFW_KEY_W:
                    moveRelativeLocal(0f, 0f, -movement);
                    break;
                case GLFW_KEY_S:
                    moveRelativeLocal(0f, 0f, movement);
                    break;
                case GLFW_KEY_SPACE:
                    moveRelativeLocal(0f, movement, 0f);
                    break;
                case GLFW_KEY_LEFT_SHIFT:
                    moveRelativeLocal(0f, -movement, 0f);
                    break;
                case GLFW_KEY_LEFT_CONTROL:
                    if (leftMouseButton) {
                        Vector2i pos = gridProvider.getActiveCell();
                        if (pos != null) { //there is actually an activecell
                            if (gridProvider.getCell(pos.x(), pos.y()).getTower() == null) {
                                buildTower(true);
                            }
                        }
                    }
                    break;
                case GLFW_KEY_X:
                    Vector2i pos = gridProvider.getActiveCell();
                    if (pos != null) { //there is an activecell
                        if (gridProvider.getCell(pos.x(), pos.y()).getTower() != null) {
                            Cell cellTower = gridProvider.getCell(pos.x, pos.y());

                            if (cellTower.getTower().getType().getValue() != 0) { //Don't sell your own castle ;)
                                //Refund some percentage of money
                                double refundPercentage = 0.8d;
                                int price = cellTower.getTower().getPrice();
                                double towerHealth = cellTower.getTower().getHealth();
                                double maxTowerHealth = cellTower.getTower().maxHealth;
                                double valueHealth = towerHealth / maxTowerHealth;
                                addBudget((int) (price * refundPercentage * valueHealth));
//                              System.out.println("Selling tower: " + cellTower.getTower().getType().toString() + " for: " + (price * valueHealth) + " With health: " + towerHealth + "/" + maxTowerHealth + " healthPortion: " + valueHealth);

                                //Kill Tower
                                cellTower.getTower().die();
                                cellTower.update();
                            }
                        }
                    }
                    break;
            }
        }

        if (lockedCameraMode){
            double frameTime = event.getSource().getFrameTime();
            float movement = (float)frameTime * 0.01f;
            switch (event.getSubject()) {
                case GLFW_KEY_A:
                    moveGlobalCentre(-movement,0,0);
                    break;
                case GLFW_KEY_D:
                    moveGlobalCentre(movement,0,0);
                    break;
                case GLFW_KEY_W:
                    moveGlobalCentre(0,0,-movement);
                    break;
                case GLFW_KEY_S:
                    moveGlobalCentre(0,0,movement);
                    break;
                case GLFW_KEY_SPACE:
                    moveRelativeLocal(0f, movement, 0f);
                    break;
                case GLFW_KEY_LEFT_SHIFT:
                    moveRelativeLocal(0f, -movement, 0f);
                    break;
            }
            rotationCameraUpdate();
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

    public void moveGlobalCentre(float offsetX, float offsetY, float offsetZ) {
        float XPosition = gCentreX;
        float ZPosition = gCentreY;

        if (offsetZ != 0) {
            if (Math.abs(gCentreX) <= maxX) {
                XPosition = (float) Math.sin(Math.toRadians(camera.getRotation().y)) * -1.0f * offsetZ;
            }
            if (Math.abs(gCentreY) <= maxZ) {
                ZPosition = (float) Math.cos(Math.toRadians(camera.getRotation().y)) * offsetZ;
            }
        }

        if (offsetX != 0) {
            if (Math.abs(gCentreX) <= maxX) {
                XPosition = (float) Math.sin(Math.toRadians(camera.getRotation().y - 90)) * -1.0f * offsetX;
            }
            if (Math.abs(gCentreY) <= maxZ) {
                ZPosition = (float) Math.cos(Math.toRadians(camera.getRotation().y - 90)) * offsetX;
            }
        }

            gCentreX += XPosition;
            gCentreY += ZPosition;
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

        if (YPosition + offsetY <= maxY && YPosition + offsetY >= minY) {
            YPosition += offsetY;
        }

        camera.setPosition(XPosition, YPosition, ZPosition);
    }

    private void buildTower(boolean hold){
        gridProvider.click();
        if (hold && prevTower != null){
            AbstractTower.MetaData metaData = AbstractTower.getMetaData(prevTower);
            int price = metaData.price;
            if (price > budget) {
                uiProvider.select(null);
            } else {
                Vector2i pos = gridProvider.getActiveCell();
                if (pos != null) {
                    towerProvider.buildTower(pos.x, pos.y, prevTower);
                }
            }
        }
        if (uiProvider.getSelected() != null) {
            Vector2i pos = gridProvider.getActiveCell();
            if (pos != null) {
                towerProvider.buildTower(pos.x, pos.y, uiProvider.getSelected());
                prevTower = uiProvider.getSelected();
                if (!hold) {
                    uiProvider.select(null);
                }
            }
        }
    }

    @Override
    public void onMouseButtonPressed(MouseEvent event) {
        invertedXaxis = engine.getWindow().getOptions().invertedXAxis;
        //System.out.println("InvertedXAxis: " + invertedXaxis);
        if (event.getSubject() == GLFW_MOUSE_BUTTON_1) {
            leftMouseButton = true;
            if (engine.isPaused()) {
                menuProvider.onClick(event);
            } else {
                if (uiProvider.onClick(event)) {
                    buildTower(false);
                }
            }
        }
        if (event.getSubject() == GLFW_MOUSE_BUTTON_2) {
            //System.out.println("R at (" + event.getX() + ", " + event.getY() + ")");
            rightMouseButton = true;

            /******************************************************************************************\
             Set current location as the start for the delta X and Y. Otherwise it will use outdated data
             and think you'll have moveD in-between pressing releasing middle mouse and pressing it again.
             \******************************************************************************************/
            oldx = event.getX();
            oldy = event.getY();
        }
        if (event.getSubject() == GLFW_MOUSE_BUTTON_3) {
            //System.out.println("M at (" + event.getX() + ", " + event.getY() + ")");
            middleMouseButton = true;

            /******************************************************************************************\
             Set current location as the start for the delta X and Y. Otherwise it will use outdated data
             and think you'll have moveD in-between pressing releasing middle mouse and pressing it again.
             \******************************************************************************************/
            oldmx = event.getX();
            oldmy = event.getY();
        }
    }

    @Override
    public void onMouseButtonReleased(MouseEvent event) {
        if (event.getSubject() == GLFW_MOUSE_BUTTON_1) {
            leftMouseButton = false;
        }
        if (event.getSubject() == GLFW_MOUSE_BUTTON_2) {
            //System.out.println("M at (" + event.getX() + ", " + event.getY() + ")");
            rightMouseButton = false;
        }
        if (event.getSubject() == GLFW_MOUSE_BUTTON_3) {
            //System.out.println("M at (" + event.getX() + ", " + event.getY() + ")");
            middleMouseButton = false;
        }
    }

    @Override
    public void onMouseMove(MouseEvent event) {
        if (engine.isPaused()) return;

        gridProvider.recalculateActiveCell(new Vector2i(event.getX(), event.getY()), camera, renderer, event.getSource());
        //Get current values
        if (rightMouseButton && freeCameraMode) {
            float x = event.getX();
            float y = event.getY();

            float deltaxMouse = (x - oldx) * (sensitivity / 10);
            float deltayMouse = (y - oldy) * (sensitivity / 10);

            //If Y-axis rotation is threatening to backflip the camera, prevent it from rotating around the y-axis
            if ((camera.getRotation().x() + deltayMouse <= 90) && (camera.getRotation().x() + deltayMouse > -35)) {
                camera.rotate(deltayMouse, deltaxMouse, 0);
            } else {
                camera.rotate(0, deltaxMouse, 0);
            }

            //Set new old values
            oldx = x;
            oldy = y;
        }

        if (rightMouseButton && lockedCameraMode) {
            float sensitivity = 0.5f;
            float sensitivity2 = 0.05f;

            float x = event.getX();
            float y = event.getY();
            float deltaXMouse = x - oldx;
            float deltaYMouse = y - oldy;

            float xDiff = gCentreX - camera.getPosition().x();
            float zDiff = gCentreY - camera.getPosition().z();
            cameraDiameter = (float)Math.sqrt(Math.pow(xDiff,2) + Math.pow(zDiff,2));

            accumulatedx += deltaXMouse * sensitivity * invertedXaxis;
            if (accumulatedy + deltaYMouse * sensitivity2 < maxY-5 && accumulatedy + deltaYMouse * sensitivity2 > 1) {
                accumulatedy += deltaYMouse * sensitivity2;
            }

            rotationCameraUpdate();

            oldx = x;
            oldy = y;
            oldaccumulatedx = accumulatedx;
            oldaccumulatedy = accumulatedy;
        }
    }

    private void rotationCameraUpdate(){
        double x = gCentreX;
        double y = 0.5;
        double z = gCentreY;
        double angley;

        x -= (float)(cameraDiameter * Math.sin(Angle.radf(accumulatedx)));
        y = (accumulatedy * sensitivity/10);
        z -= (float)(cameraDiameter * Math.cos(Angle.radf(accumulatedx)));

        camera.setPosition((float)x,(float)y,(float)z);

        double xDiff = gCentreX - camera.getPosition().x();
        double yDiff = 0.5 - camera.getPosition().y();
        double zDiff = gCentreY - camera.getPosition().z();
        cameraDistance = Math.sqrt(Math.pow(xDiff,2) + Math.pow(yDiff,2) + Math.pow(zDiff,2));

        double anglex = oldaccumulatedx - accumulatedx;
        if (cameraDistance >= cameraDiameter) {
            angley = 90 - ((Math.asin(cameraDiameter / cameraDistance)) * 180/Math.PI);
        } else {
            angley = 0;
        }

//        spam system.out with camera information
//        System.out.println("x: " + camera.getPosition().x());
//        System.out.println("y: " + camera.getPosition().y());
//        System.out.println("z: " + camera.getPosition().z());
//        System.out.println("gCentreX: " + gCentreX);
//        System.out.println("gCentreY: " + gCentreY);
//        System.out.println("cameraDiameter: " + cameraDiameter);
//        System.out.println("cameraDistance: " + cameraDistance);
//        System.out.println("angley: " + angley);

        double diffangley = angley - oldangley;

        camera.rotate((float) diffangley, (float) anglex, 0);
        oldangley = (float)angley;
    }

    @Override
    public void onMouseHover(MouseEvent event) {
        if (true) return;
        if (freeCameraMode) {
            if (engine.isPaused()) return;
            Window window = event.getSource();

            float percDistFromEdge = 0.07f;
            float distFromEdge = percDistFromEdge * window.getWidth();
            float speed = (float) window.getFrameTime();
            float sensitivityScroll = 0.001f * speed * ticksSinceTrigger;

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

            if (!(xMouse > distFromEdge && xMouse <= window.getWidth() - distFromEdge && yMouse > distFromEdge && yMouse
                    <= window.getHeight() - distFromEdge) && ticksSinceTrigger <= 120) {
                ticksSinceTrigger += 0.001f;
            }

            if (xMouse > distFromEdge && xMouse <= window.getWidth() - distFromEdge && yMouse > distFromEdge && yMouse <= window.getHeight() - distFromEdge) {
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
                moveRelativeLocal(0, 0, -(distFromEdge - yMouse) * sensitivityScroll);
            }

            //Scroll down if mouse is within border range
            if (yMouse >= window.getHeight() - distFromEdge && yMouse < window.getHeight()) {
                moveRelativeLocal(0, 0, (yMouse - (window.getHeight() - distFromEdge)) * sensitivityScroll);
            }
        }
    }

    @Override
    public void onMouseScroll(MouseEvent event) {
        if (freeCameraMode) {
            Vector3f directionOfCamera = gridProvider.getDirectionOfCamera(renderer, event.getSource(), 0, 0);
            float speed = 0.04f;
            moveLocal((event.getY()) * directionOfCamera.x() * speed, (event.getY()) * directionOfCamera.y() * speed, (event.getY()) * directionOfCamera.z() * speed);
        }
        if (lockedCameraMode) {
            float xDiff = gCentreX - camera.getPosition().x();
            float yDiff = 0 - camera.getPosition().y();
            float zDiff = gCentreY - camera.getPosition().z();
            if ((cameraDiameter - event.getY() / 10f) < 10 && (cameraDiameter - event.getY() / 10f) > 2) {
                cameraDiameter -= event.getY() / 10f;
                cameraDistance = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2) + Math.pow(zDiff, 2));
                rotationCameraUpdate();
            }
        }
        if (engine.isPaused()) return;
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
