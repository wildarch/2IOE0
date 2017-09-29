package nl.tue.c2IOE0.group5.engine.rendering;

import nl.tue.c2IOE0.group5.engine.objects.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author Jorren Hendriks.
 */
public class Renderer {

    // FOV in radians
    private static final float FOV = (float) Math.toRadians(60.0f);
    // z-coordinates relative to the activeCamera.
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;

    private final Transformation transformation;

    private final Map<String, Integer> uniforms;

    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    private Camera activeCamera;

    /**
     * Constructor for initializing datastructures.
     */
    public Renderer() {
        uniforms = new HashMap<>();
        transformation = new Transformation();
    }

    /**
     * Initialize the renderer, create shaders and link them.
     *
     * @throws ShaderException When an error occurred.
     */
    public void init() throws ShaderException, IOException {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new ShaderException("Could not create Shader");
        }

        createVertexShader(loadResource("/vertex.vs"));
        createFragmentShader(loadResource("/fragment.fs"));
        link();

        // Create uniforms for world and projection matrices
        createUniform("projectionMatrix");
        createUniform("modelViewMatrix");
        createUniform("texture_sampler");

        setUniform("texture_sampler", 0);


    }

    /**
     * Set the camera which is currently active.
     *
     * @param activeCamera The camera to be active.
     */
    public void setActiveCamera(Camera activeCamera) {
        this.activeCamera = activeCamera;
    }

    /**
     * Get the currently active Camera.
     *
     * @return The active Camera.
     */
    public Camera getActiveCamera() {
        return this.activeCamera;
    }

    /**
     * Bind the renderer to the current rendering state
     */
    public void bind() {
        glUseProgram(programId);
    }

    /**
     * Unbind the renderer from the current rendering state
     */
    public void unbind() {
        glUseProgram(0);
    }

    /**
     * Cleanup the renderer after it's done
     */
    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

    /**
     * Link the program and cleanup the shaders.
     *
     * @throws ShaderException If an error occures linking the shader code.
     */
    private void link() throws ShaderException {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new ShaderException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    /**
     * see {@link #setModelViewMatrix(Vector3f, Vector3f, float)}, with {@code float scale = 1f}
     */
    public void setModelViewMatrix(Vector3f position, Vector3f rotation) {
        setModelViewMatrix(position, rotation, 1f);
    }

    /**
     * Set the modelview matrix. This sets the location, rotation and scale of the things to be rendered next. Besides
     * it takes into account where the activeCamera is at.
     *
     * @param position The position of the objects that will be rendered next.
     * @param rotation The rotation of the objects that will be rendered next.
     * @param scale The scale of the objects that will be rendered next.
     */
    public void setModelViewMatrix(Vector3f position, Vector3f rotation, float scale) {
        Matrix4f transformationMatrix = transformation.getModelViewMatrix(position, rotation, scale, getActiveCamera());
        setUniform("modelViewMatrix", transformationMatrix);
    }

    /**
     * Update the projection matrix, this has to do with the perspective of the activeCamera.
     *
     * @param window The window on which the scene will be rendered.
     */
    public void updateProjectionMatrix(Window window) {
        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        setUniform("projectionMatrix", projectionMatrix);
    }

    /**
     * Create a new uniform and get its memory location.
     *
     * @param uniformName The name of the uniform.
     * @throws ShaderException If an error occurs getting the memory location.
     */
    private void createUniform(String uniformName) throws ShaderException {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new ShaderException("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    /**
     * Set the value of a certain 4x4 matrix shader uniform.
     *
     * @param uniformName The name of the uniform.
     * @param value The new value of the uniform.
     */
    private void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Dump the matrix into a float buffer
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    /**
     * Set the value of a certain integer shader uniform
     *
     * @param uniformName The name of the uniform.
     * @param value The new value of the uniform.
     */
    private void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    /**
     * Create a new vertexshader and set the vertexshader id field to that of the newly created shader.
     *
     * @param shaderCode The shaderCode as a String.
     * @throws ShaderException If an error occurs during the creation of a shader.
     */
    private void createVertexShader(String shaderCode) throws ShaderException {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    /**
     * Create a new fragmentshader and set the fragmentshader id field to that of the newly created shader.
     *
     * @param shaderCode The shaderCode as a String.
     * @throws ShaderException If an error occurs during the creation of a shader.
     */
    private void createFragmentShader(String shaderCode) throws ShaderException {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    /**
     * Create a new shader and return the id of the newly created shader.
     *
     * @param shaderCode The shaderCode as a String.
     * @param shaderType The type of shader, e.g. GL_VERTEX_SHADER.
     * @return The id of the newly created shader.
     * @throws ShaderException If an error occurs during the creation of a shader.
     */
    private int createShader(String shaderCode, int shaderType) throws ShaderException {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new ShaderException("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new ShaderException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    /**
     * Load a text file as a String
     *
     * @param fileName The name of the file to load.
     * @return The contents of the file as a String.
     * @throws IOException If a read error occures.
     */
    private String loadResource(String fileName) throws IOException {
        try (InputStream in = this.getClass().getResourceAsStream(fileName)) {
            Scanner scanner = new Scanner(in, "UTF-8");
            return scanner.useDelimiter("\\A").next();
        }
    }

}
