package nl.tue.c2IOE0.group5.engine.controller.input;

import nl.tue.c2IOE0.group5.engine.controller.input.events.Event;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Listener;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jorren Hendriks
 *
 * Handles the input of a certain {@link Window} and notifies attached listeners of the input events.
 */
public class InputHandler {

    private Window window;
    private List<Listener> listeners;

    private boolean[] keyBuffer;

    public InputHandler() {
        listeners = new ArrayList<>();
        keyBuffer = new boolean[350];
    }

    /**
     * Initialize {@link InputHandler} and associate it with a certain {@link Window}.
     *
     * @param window The window to associate this inputhandler with.
     */
    public void init(Window window) {
        this.window = window;

        window.registerListener(new KeyEventHandler());
        window.registerListener(new MouseButtonEventHandler());
        window.registerListener(new MouseMoveEventHandler());
        window.registerListener(new MouseScrollEventHandler());
    }

    /**
     * Fire non-native events
     */
    public void fire() {
        for (int key = 0; key < keyBuffer.length; key++) {
            if (keyBuffer[key]) {
                Event event = new Event(window, key);
                listeners.forEach(listener -> listener.onKeyHold(event));
            }
        }
        Vector2i mPos = window.getMousePosition();
        MouseEvent event = new MouseEvent(window, -1, mPos.x, mPos.y);
        listeners.forEach(listener -> listener.onMouseHover(event));
    }

    /**
     * Add a {@link Listener} to this inputhandler.
     *
     * @param listener The listener to add.
     */
    public void addListener(Listener listener) {
        listeners.add(listener);
    }



    private class KeyEventHandler extends GLFWKeyCallback {
        @Override
        public void invoke(long windowHandle, int keyCode, int scancode, int action, int mods) {
            if (keyCode < 0) return;

            Event event = new Event(window, keyCode);
            if (action == GLFW_PRESS) {
                keyBuffer[keyCode] = true;
                listeners.forEach(listener -> listener.onKeyPressed(event));
            } else if (action == GLFW_RELEASE) {
                keyBuffer[keyCode] = false;
                listeners.forEach(listener -> listener.onKeyReleased(event));
            }
        }
    }

    private class MouseButtonEventHandler extends GLFWMouseButtonCallback {
        @Override
        public void invoke(long windowHandle, int button, int action, int mods) {
            Vector2i pos = window.getMousePosition();
            MouseEvent event = new MouseEvent(window, button, pos.x(), pos.y());
            if (action == GLFW_PRESS) {
                listeners.forEach(listener -> listener.onMouseButtonPressed(event));
            } else if (action == GLFW_RELEASE) {
                listeners.forEach(listener -> listener.onMouseButtonReleased(event));
            }
        }
    }

    private class MouseMoveEventHandler extends GLFWCursorPosCallback {
        @Override
        public void invoke(long windowHandle, double xPos, double yPos) {
            MouseEvent event = new MouseEvent(window, -1, (int) xPos, (int) yPos);
            listeners.forEach(listener -> listener.onMouseMove(event));
        }
    }

    private class MouseScrollEventHandler extends GLFWScrollCallback {
        @Override
        public void invoke(long windowHandle, double xScroll, double yScroll) {
            MouseEvent event = new MouseEvent(window, -1, (int) (xScroll*10), (int) (yScroll*10));
            listeners.forEach(listener -> listener.onMouseScroll(event));
        }
    }
}


