package nl.tue.c2IOE0.group5.engine.rendering;

import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.engine.rendering.shader.DepthMap;
import nl.tue.c2IOE0.group5.engine.rendering.shader.DirectionalLight;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.engine.rendering.shader.PointLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
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

    // generic shader
    private ShaderProgram sceneShader;
    // shader for shadow maps
    private ShaderProgram depthShader;
    // depth map used for shadows for the entire scene
    private DepthMap depthMap;



    /**
     * Constructor for initializing datastructures.
     */
    public Renderer() {
        sceneShader = new ShaderProgram();
        depthShader = new ShaderProgram();
    }

    /**
     * Initialize the renderer, create shaders and link them.
     *
     * @throws ShaderException When an error occurred.
     */
    public void init() throws ShaderException, IOException {
        try {
            initSceneShader();
            depthMap = new DepthMap();
            initDepthShader();
        } catch (Exception e) {
            e.printStackTrace();
            //critical error, so close program
            System.exit(0);
        }
    }

    private void initSceneShader() throws ShaderException, IOException {
        sceneShader.init();
        sceneShader.createVertexShader(loadResource("/shaders/vertex.vert"));
        sceneShader.createFragmentShader(loadResource("/shaders/fragment.frag"));
        sceneShader.link();

        // Create uniforms for world and projection matrices
        sceneShader.createUniform("projectionMatrix");
        sceneShader.createUniform("modelViewMatrix");
        sceneShader.createUniform("texture_sampler");

        // Create the Material uniform
        sceneShader.createMaterialUniform("material");

        // Create the lighting uniforms
        sceneShader.createUniform("specularPower");
        sceneShader.createUniform("ambientLight");
        sceneShader.createPointLightUniform("pointLight");

        // current spread
        sceneShader.createUniform("bounceDegree");
        // height of middle of object/bounce
        sceneShader.createUniform("boundingMax");
        // height from top to bottom (the heightrange that is expanded)
        sceneShader.createUniform("boundingMin");

        // skybox uniform
        sceneShader.createUniform("isSkybox");

        sceneShader.createDirectionalLightUniform("directionalLight");

        sceneShader.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneShader.setDirectionalLight(new DirectionalLight(
                new Vector3f(1f, 1f, 1f),
                new Vector3f(-0.78f, 0.4f, 0.66f),
                1f
        ));
        PointLight pointLight = new PointLight(
                new Vector3f(1f, 1f, 1f),
                new Vector3f(0f, 0f, 1f),
                10.0f);
        pointLight.setAttenuation(new PointLight.Attenuation(0f, 0f, 1f));
        sceneShader.setPointLight(pointLight);
    }

    private void initDepthShader() throws ShaderException, IOException {
        depthShader.init();
        depthShader.createVertexShader(loadResource("/shaders/vertex_depth.vert"));
        depthShader.createFragmentShader(loadResource("/shaders/fragment_depth.frag"));
        depthShader.link();

        depthShader.createUniform("orthoProjectionMatrix");
        depthShader.createUniform("modelLightViewMatrix");
    }

    //methods for passing information on to the scene shader
    public void setModelViewMatrix(Vector3f position, Vector3f rotation) {
        sceneShader.setModelViewMatrix(position, rotation, 1f);
    }
    public void setModelViewMatrix(Vector3f position, Vector3f rotation, float scale) {
        sceneShader.setModelViewMatrix(position, rotation, scale);
    }
    public Matrix4f getViewMatrix() {
        return sceneShader.getViewMatrix();
    }
    public Matrix4f getProjectionMatrix(Window window) {
        return sceneShader.getProjectionMatrix(window);
    }
    public void setActiveCamera(Camera activeCamera) {
        sceneShader.setActiveCamera(activeCamera);
    }
    public Camera getActiveCamera() {
        return sceneShader.getActiveCamera();
    }
    public void cleanup() {
        sceneShader.cleanup();
    }
    public void bind() {
        sceneShader.bind();
    }
    public void unbind() {
        sceneShader.unbind();
    }
    public void updateProjectionMatrix(Window window) {
        sceneShader.updateProjectionMatrix(window);
    }
    public void ambientLight(Vector3f color, Runnable render) {
        sceneShader.ambientLight(color, render);
    }
    public void setMaterial(Material material) {
        sceneShader.setUniform("material", material);
    }
    public void drawSkybox(Runnable render) {
        sceneShader.drawSkybox(render);
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
