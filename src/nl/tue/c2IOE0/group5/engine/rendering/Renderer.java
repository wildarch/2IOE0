package nl.tue.c2IOE0.group5.engine.rendering;

import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.engine.rendering.shader.DepthMap;
import nl.tue.c2IOE0.group5.engine.rendering.shader.DirectionalLight;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.engine.rendering.shader.PointLight;
import nl.tue.c2IOE0.group5.util.Resource;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Jorren Hendriks.
 */
public class Renderer {
    private Map<String, Mesh> meshes;
    private Map<Mesh, List<Consumer<Mesh>>> meshBuffer;

    private Map<Mesh, List<Consumer<Mesh>>> shadowBuffer;

    private Window window;
    private Camera activeCamera;
    // generic shader
    private ShaderProgram sceneShader;
    // shader for shadow maps
    private ShaderProgram depthShader;
    // depth map used for shadows for the entire scene
    private DepthMap depthMap;

    private Transformation transformation;

    // light matrix for shadow mas
    private Matrix4f lightViewMatrix;



    /**
     * Constructor for initializing datastructures.
     */
    public Renderer() {
        meshes = new HashMap<>();
        meshBuffer = new HashMap<>();
        shadowBuffer = new HashMap<>();
        transformation = new Transformation();
    }

    public Mesh linkMesh(String filename, Consumer<Mesh> render, Consumer<Mesh> shadowrender) throws Exception {
        Mesh mesh = linkMesh(filename);
        if (meshBuffer.containsKey(mesh)) {
            meshBuffer.get(mesh).add(render);
        } else {
            meshBuffer.put(mesh, new ArrayList<>(Collections.singleton(render)));
        }
        if (shadowBuffer.containsKey(mesh)) {
            shadowBuffer.get(mesh).add(shadowrender);
        } else {
            shadowBuffer.put(mesh, new ArrayList<>(Collections.singleton(shadowrender)));
        }
        return mesh;
    }

    public Mesh linkMesh(String filename) throws Exception {
        if (meshes.containsKey(filename)) {
            return meshes.get(filename);
        } else {
            Mesh mesh = OBJLoader.loadMesh(filename);
            meshes.put(filename, mesh);
            return mesh;
        }
    }

    public void unlinkMesh(Mesh mesh, Runnable runnable) {
        meshBuffer.get(mesh).remove(runnable);
    }

    /**
     * Initialize the renderer, create shaders and link them.
     *
     * @throws ShaderException When an error occurred.
     */
    public void init(Window window) throws ShaderException, IOException {
        try {
            this.window = window;
            initSceneShader();
            initDepthShader();
            depthMap = new DepthMap();
            initDepthShader();
        } catch (Exception e) {
            e.printStackTrace();
            //critical error, so close program
            System.exit(0);
        }
    }

    private void initSceneShader() throws ShaderException, IOException {
        sceneShader = new ShaderProgram();
        sceneShader.createVertexShader(Resource.load("/shaders/vertex.vert"));
        sceneShader.createFragmentShader(Resource.load("/shaders/fragment.frag"));
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
        sceneShader.createPointLightsUniform("pointLights", ShaderProgram.MAX_POINT_LIGHTS);
        sceneShader.createDirectionalLightUniform("directionalLight");
        // Create uniforms for the bounce effect
        sceneShader.createUniform("bounceDegree");
        sceneShader.createUniform("boundingMax");
        sceneShader.createUniform("boundingMin");

        // Create uniform for rendering the skybox
        sceneShader.createUniform("isSkybox");


        // Initialize some fields
        sceneShader.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        DirectionalLight directionalLight = new DirectionalLight(
                new Vector3f(1f, 1f, 1f),
                new Vector3f(-0.78f, 0.4f, 0.66f),
                1f
        );
        directionalLight.setOrthoCords(-50.0f, 50.0f, -50.0f, 50.0f, -1.0f, 500.0f);
        sceneShader.setDirectionalLight(directionalLight);
    }

    private void initDepthShader() throws ShaderException, IOException {
        depthShader = new ShaderProgram();
        depthShader.createVertexShader(Resource.load("/shaders/vertex_depth.vert"));
        depthShader.createFragmentShader(Resource.load("/shaders/fragment_depth.frag"));
        depthShader.link();

        depthShader.createUniform("orthoProjectionMatrix");
        depthShader.createUniform("modelLightViewMatrix");
    }

    public void cleanup() {
        sceneShader.cleanup();
        depthShader.cleanup();
    }

    /**
     * Get the view matrix of the current scene.
     *
     * @return A View Matrix
     */
    public Matrix4f getViewMatrix() {
        return transformation.getViewMatrix(getActiveCamera());
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
        sceneShader.setUniform("modelViewMatrix", transformationMatrix);
    }

    /**
     * applies a bounce-effect around the given gravity-middle, stretching boundingMin/2 up and down.
     * this effect applies only to subjects rendered inside the render parameter.
     * @param bounceDegree the strength B of the effect, with B = 0 no effect,
     *                     B > 0 a horizontal expansion and B < 0 a vertical stretch
     * @param boundingMin the minimum coordinates of the 3D-model
     * @param boundingMax idem maximum
     * @param render the runnable where the bounce effect will apply to
     */
    public void boink(float bounceDegree, Vector3f boundingMin, Vector3f boundingMax, Runnable render){
        sceneShader.setUniform("bounceDegree", bounceDegree);
        sceneShader.setUniform("boundingMin", boundingMin);
        sceneShader.setUniform("boundingMax", boundingMax);

        render.run();

        sceneShader.setUniform("bounceDegree", 0f);
    }

    /**
     * Sets up the renderer to draw a skybox, e.g. an object that doesn't care but just draws its texture.
     *
     * @param render The code to render in skybox mode.
     */
    public void drawSkybox(Runnable render) {
        sceneShader.setUniform("isSkybox", 1);

        render.run();

        sceneShader.setUniform("isSkybox", 0);
    }

    /**
     * Temporarily change the ambient light of an object. This effect only applies to objects drawn inside the render
     * parameter.
     *
     * @param color The ambient light color.
     * @param render A runnable in which the objects to which this effect applies are drawn.
     */
    public void ambientLight(Vector3f color, Runnable render) {
        sceneShader.setUniform("ambientLight", color);
        render.run();
        sceneShader.setUniform("ambientLight", sceneShader.getAmbientLight());
    }

    /**
     * Set the material of currently rendered object.
     *
     * @param material The material of the object.
     */
    public void setMaterial(Material material) {
        sceneShader.setUniform("material", material);
    }

    /**
     * Render the lights in the scene
     *
     * @param viewMatrix A viewmatrix on which the point lights need to be drawn.
     */
    private void renderLights(Matrix4f viewMatrix) {
        sceneShader.setUniform("specularPower", sceneShader.getSpecularPower());
        sceneShader.setUniform("ambientLight", sceneShader.getAmbientLight());

        PointLight[] pointLights = sceneShader.getPointLights();
        for (int i = 0; i < pointLights.length; i++) {
            PointLight pointLight = new PointLight(pointLights[i]);
            Vector4f pos = new Vector4f(pointLight.getPosition(), 1);
            pos.mul(viewMatrix);
            pointLight.setPosition(new Vector3f(pos.x, pos.y, pos.z));
            sceneShader.setUniform("pointLights", pointLight, i);
        }

        DirectionalLight dirLight = new DirectionalLight(sceneShader.getDirectionalLight());
        Vector4f dir = new Vector4f(dirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        dirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        sceneShader.setUniform("directionalLight", dirLight);
    }


    /**
     * Render the main objects of the scene.
     */
    private void renderScene() {
        sceneShader.bind();

        Matrix4f projectionMatrix = window.getProjectionMatrix();
        sceneShader.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = getViewMatrix();

        renderLights(viewMatrix);

        sceneShader.setUniform("texture_sampler", 0);

        meshBuffer.forEach((mesh, consumers) -> {
            setMaterial(mesh.getMaterial());
            //setMaterial(new Material(depthMap.getDepthMapTexture()));
            mesh.renderAll(consumers);
        });

        sceneShader.unbind();
    }

    public void renderDepthMap() {
        // Setup view port to match the texture size
        glBindFramebuffer(GL_FRAMEBUFFER, depthMap.getDepthMapFBO());
        glViewport(0, 0, depthMap.width, depthMap.height);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShader.bind();

        DirectionalLight light = sceneShader.getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        float lightAngleX = (float)Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;
        lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowStrength()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

        depthShader.setUniform("orthoProjectionMatrix", orthoProjMatrix);

        shadowBuffer.forEach((mesh, consumers) -> {
            mesh.renderAll(consumers);
        });

        // Unbind
        depthShader.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void setModelLightViewMatrix(Vector3f position, Vector3f rotation, float scale) {
        Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(position, rotation, scale, lightViewMatrix);
        depthShader.setUniform("modelLightViewMatrix", modelLightViewMatrix);
    }
    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        renderDepthMap();

        glViewport(0, 0, window.getWidth(), window.getHeight());
        renderScene();
    }

}
