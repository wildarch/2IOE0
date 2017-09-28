package nl.tue.c2IOE0.group5.engine.rendering;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.joml.Matrix4f;

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

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    public final Transformation transformation;

    private final Map<String, Integer> uniforms;

    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    //private constructor for initializing a few variables
    public Renderer() {
        uniforms = new HashMap<>();
        transformation = new Transformation();
    }

    /**
     * Initialize the renderer, create shaders and link them.
     *
     * @throws Exception When an error occured.
     */
    public void init() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
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

    private void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
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

    public void setTransformationMatrix(Vector3f position, Vector3f rotation) {
        setTransformationMatrix(position, rotation, 1f);
    }

    public void setTransformationMatrix(Vector3f position, Vector3f rotation, float scale) {
        Matrix4f transformationMatrix = transformation.getModelViewMatrix(position, rotation, scale);
        setUniform("modelViewMatrix", transformationMatrix);
    }

    public void updateProjectionMatrix(Window window, Camera camera) {
        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Dump the matrix into a float buffer
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    private void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    private void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    private String loadResource(String name) {
        try (InputStream in = this.getClass().getResourceAsStream(name)) {
            Scanner scanner = new Scanner(in, "UTF-8");
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
