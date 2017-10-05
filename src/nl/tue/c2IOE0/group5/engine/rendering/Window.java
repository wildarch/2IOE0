package nl.tue.c2IOE0.group5.engine.rendering;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Jorren Hendriks
 *
 * A window which initializes GLFW and manages it.
 */
public class Window {
    private final String title;
    private final boolean resizable;
    // buffers for mouse input
    private final DoubleBuffer mousePosX;
    private final DoubleBuffer mousePosY;

    private long window;
    private int width;
    private int height;
    private boolean vSync;

    public Window(String title) {
        this(title, 960, 720, true, false);
    }

    public Window(String title, int width, int height, boolean vSync, boolean resizable) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        this.resizable = resizable;

        this.mousePosX = BufferUtils.createDoubleBuffer(1);
        this.mousePosY = BufferUtils.createDoubleBuffer(1);
    }

    public void init() {
        // Setup error callback, print to System.err
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GL_TRUE : GL_FALSE);
        // Set OpenGL version
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create window
        window = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        if (this.resizable) {
            // Setup resize callback
            glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
                this.width = width;
                this.height = height;
            });
        }

        // Get primary display resolution
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center window on display
        glfwSetWindowPos(
                window,
                (vidmode.width() - this.width) / 2,
                (vidmode.height() - this.height) / 2
        );

        // Make GL context current
        glfwMakeContextCurrent(window);

        if (vSyncEnabled()) {
            // Turn on vSync
            glfwSwapInterval(1);
        }

        // Show window
        glfwShowWindow(window);

        GL.createCapabilities();

        // Set clear color to black
        glClearColor(0f, 0f, 0f, 0f);

        // Enable Depth Test
        glEnable(GL_DEPTH_TEST);
        // Enable 2d Texture
        glEnable(GL_TEXTURE_2D);
    }

    /**
     * Update the {@link Window}. This will deal with basic OpenGL formalities. Besides it will also poll for events
     * which occurred on the window. Finally returns whether the window should close based on what GLFW thinks.
     *
     * @return Whether the {@link Window} should continue running.
     */
    public boolean update() {
        // Swap buffers
        glfwSwapBuffers(window);

        // Poll for events
        glfwPollEvents();

        if (glfwWindowShouldClose(window)) {
            // Release window and window callbacks when window is closed
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

            return false;
        }

        clear();

        return true;
    }

    /**
     * Close the window
     */
    public void close() {
        glfwSetWindowShouldClose(window, true);
    }

    /**
     * Terminate GLFW and release GLFW error callback
     */
    public void cleanup() {
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }


    /**
     * Set the color which is used for clearing the window.
     *
     * @param red The red value (0.0 - 1.0)
     * @param green The green value (0.0 - 1.0)
     * @param blue The blue value (0.0 - 1.0)
     * @param alpha The alpha value (0.0 - 1.0)
     */
    public void setClearColor(float red, float green, float blue, float alpha) {
        glClearColor(red, green, blue, alpha);
    }

    /**
     * Check whether a certain key is pressed.
     *
     * @param keyCode The keycode of the key.
     * @return Whether the key with requested keyCode is pressed.
     */
    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }

    /**
     * Check whether a certain mouse button is pressed.
     *
     * @param button The button of the mouse.
     * @return Whether the requested button is pressed.
     */
    public boolean isMouseButtonPressed(int button) {
        return glfwGetMouseButton(window, button) == GLFW_PRESS;
    }

    /**
     * Get the current position of the mouse.
     *
     * @return The position of the mouse.
     */
    public MousePos getMousePosition() {
        glfwGetCursorPos(window, mousePosX, mousePosY);
        return new MousePos(mousePosX.get(0), mousePosY.get(0));
    }

    /**
     * Get whether the window should close.
     *
     * @return Whether the window should close.
     */
    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    /**
     * Get the title of the window.
     *
     * @return The title of the window.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the width of the window.
     *
     * @return The width of the window.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the window.
     *
     * @return The height of the window.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get whether resizing the window is allowed.
     *
     * @return Whether resizing the window is allowed.
     */
    public boolean resizeEnabled() {
        return resizable;
    }

    /**
     * Resize the window.
     *
     * @param width The new width of the window.
     * @param height The new Height of the window.
     */
    public void resize(int width, int height) {
        glfwSetWindowSize(window, width, height);

        // Get primary display resolution
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center window on display
        glfwSetWindowPos(
                window,
                (vidmode.width() - this.width) / 2,
                (vidmode.height() - this.height) / 2
        );
    }

    /**
     * Get whether vSync is currently enabled.
     *
     * @return Whether vSync is enabled.
     */
    public boolean vSyncEnabled() {
        return this.vSync;
    }

    /**
     * Clear the window.
     */
    public void clear() {
        // Clear framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Register a listener for window events.
     *
     * @param callback The callback function which is called on event firing.
     */
    public void registerListener(Object callback) {
        if (callback instanceof GLFWKeyCallbackI) {
            glfwSetKeyCallback(window, (GLFWKeyCallbackI) callback);
        }
        if (callback instanceof GLFWMouseButtonCallbackI) {
            glfwSetMouseButtonCallback(window, (GLFWMouseButtonCallbackI) callback);
        }
        if (callback instanceof GLFWCursorPosCallbackI) {
            glfwSetCursorPosCallback(window, (GLFWCursorPosCallbackI) callback);
        }
    }

    public class MousePos {
        private int x;
        private int y;

        private MousePos(double x, double y) {
            this.x = (int) x;
            this.y = (int) y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
