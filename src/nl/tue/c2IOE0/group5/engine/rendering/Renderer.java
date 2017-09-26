package nl.tue.c2IOE0.group5.engine.rendering;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author Jorren Hendriks.
 */
public class Renderer {

    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

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
