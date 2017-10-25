package nl.tue.c2IOE0.group5.engine.rendering;

import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.engine.Cleanable;
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
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
 * @author Jorren Hendriks.
 */
public class Renderer implements Cleanable {

    private ArrayDeque<Runnable> modifiers;

    private Map<String, Mesh> meshes;
    private Map<Mesh, List<InstancedMesh>> instancedMeshes;

    private Window window;
    private Camera activeCamera;
    // generic shader
    private ShaderProgram sceneShader;
    // shader for shadow maps
    private ShaderProgram depthShader;
    // depth map used for shadows for the entire scene
    private DepthMap depthMap;

    private Transformation transformation;

    // light matrix for shadow map
    private Matrix4f lightViewMatrix;

    // light matrix for usage shadow map
    private Matrix4f sceneLightViewMatrix;

    private DirectionalLight directionalLight;

    private Vector3f ambientLight;

    private Task task;
    private enum Task {
        SCENE,
        DEPTH_MAP
    }

    private boolean shadowMapping = true;

    /**
     * Constructor for initializing datastructures.
     */
    public Renderer() {
        modifiers = new ArrayDeque<>();
        meshes = new HashMap<>();
        instancedMeshes = new HashMap<>();
        transformation = new Transformation();
    }

    public Mesh linkMesh(String filename) {
        if (meshes.containsKey(filename)) {
            return meshes.get(filename);
        } else {
            try {
                Mesh mesh = OBJLoader.loadMesh(filename);
                meshes.put(filename, mesh);
                return mesh;
            } catch (IOException e) {
                throw new MeshException("Could not load " + filename);
            }
        }
    }

    public InstancedMesh linkMesh(Mesh mesh, Runnable render) {
        InstancedMesh iMesh = new InstancedMesh(mesh, render);
        if (instancedMeshes.containsKey(mesh)) {
            instancedMeshes.get(mesh).add(iMesh);
        } else {
            instancedMeshes.put(mesh, new ArrayList<>(Collections.singleton(iMesh)));
        }
        return iMesh;
    }

    public InstancedMesh linkMesh(String filename, Runnable render) {
        Mesh mesh = linkMesh(filename);
        return linkMesh(mesh, render);
    }

    /**
     * @param filename target .obj file with the mesh
     * @param material material of the mesh
     * @param render execution method for drawing
     * @return handle to this instance
     */
    public InstancedMesh linkMesh(String filename, Material material, Runnable render) {
        Mesh mesh = linkMesh(filename);
        mesh.setMaterial(material);
        return linkMesh(mesh, render);
    }

    public void unlinkMesh(InstancedMesh iMesh) {
        if (!instancedMeshes.get(iMesh.getMesh()).remove(iMesh)) {
            throw new IllegalArgumentException("The given instanced mesh was not linked to its mesh");
        }
    }

    public void changeOrtho(float left, float right, float bottom, float top, float near, float far) {
        DirectionalLight.OrthoCoords c = directionalLight.getOrthoCoords();
        directionalLight.setOrthoCords(c.left+left, c.right+right, c.bottom+bottom, c.top+top, c.near+near, c.far+far);
    }

    /**
     * Initialize the renderer, create shaders and link them.
     *
     * @throws ShaderException When an error occurred.
     */
    public void init(Window window) throws ShaderException, IOException {
        this.window = window;
        initSceneShader();
        initDepthShader();
        depthMap = new DepthMap();
        initDepthShader();
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

        // Create uniforms for shadow mapping
        sceneShader.createUniform("depthMap");
        sceneShader.createUniform("depthMapEnabled");
        sceneShader.createUniform("orthoProjectionMatrix");
        sceneShader.createUniform("modelLightViewMatrix");

        // Create uniform for special lighting conditions for background elements
        sceneShader.createUniform("background");
        sceneShader.createUniform("blackAsAlpha");
        sceneShader.createUniform("directionalLightOff");




        // Initialize some fields
        ambientLight = new Vector3f(0.1f, 0.1f, 0.1f);
        sceneShader.setAmbientLight(ambientLight);
        directionalLight = new DirectionalLight(
                new Vector3f(1f, 1f, 1f),
                new Vector3f(0.78f, 0.4f, 0.66f),
                1f
        );
        directionalLight.setOrthoCords(-10.0f, 10.0f, -14.0f, 14.0f, -14f, 14.0f);
        sceneShader.setDirectionalLight(directionalLight);

        sceneShader.setUniform("texture_sampler", 0);
    }

    private void initDepthShader() throws ShaderException, IOException {
        depthShader = new ShaderProgram();
        depthShader.createVertexShader(Resource.load("/shaders/vertex_depth.vert"));
        depthShader.createFragmentShader(Resource.load("/shaders/fragment_depth.frag"));
        depthShader.link();

        // Create uniforms for the bounce effect
        depthShader.createUniform("bounceDegree");
        depthShader.createUniform("boundingMax");
        depthShader.createUniform("boundingMin");

        depthShader.createUniform("orthoProjectionMatrix");
        depthShader.createUniform("modelLightViewMatrix");
    }

    @Override
    public void cleanup() {
        meshes.values().forEach(Mesh::cleanup);
        sceneShader.cleanup();
        depthShader.cleanup();
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

    public void setShadowMapping(boolean value) {
        this.shadowMapping = value;
    }

    /**
     * applies a bounce-effect around the given gravity-middle, stretching boundingMin/2 up and down.
     * this effect applies only to subjects rendered inside the render parameter.
     * @param bounceDegree the strength B of the effect, with B = 0 no effect,
     *                     B > 0 a horizontal expansion and B < 0 a vertical stretch
     * @param mesh mesh to be boinked
     */
    public void boink(float bounceDegree, Mesh mesh) {
        ShaderProgram shader = task == Task.SCENE ? sceneShader : depthShader;

        shader.setUniform("bounceDegree", bounceDegree);
        shader.setUniform("boundingMin", mesh.getMinBoundingBox());
        shader.setUniform("boundingMax", mesh.getMaxBoundingBox());

        modifiers.push(() ->
                shader.setUniform("bounceDegree", 0f));
    }

    public void boink(float bounceDegree, Mesh... meshes) {
        ShaderProgram shader = task == Task.SCENE ? sceneShader : depthShader;

        shader.setUniform("bounceDegree", bounceDegree);
        shader.setUniform("boundingMin", Mesh.combinedMinBoundingBox(meshes));
        shader.setUniform("boundingMax", Mesh.combinedMaxBoundingBox(meshes));

        modifiers.push(() ->
                shader.setUniform("bounceDegree", 0f));


    }

    /**
     * Sets up the renderer to draw a skybox, e.g. an object that doesn't care but just draws its texture.
     *
     */
    public void drawSkybox() {
        if (task == Task.DEPTH_MAP) return;
        sceneShader.setUniform("isSkybox", 1);
        modifiers.push(() ->
                sceneShader.setUniform("isSkybox", 0));
    }

    public void drawNoShadow() {
        if (task == Task.DEPTH_MAP) return;
        sceneShader.setUniform("background", 1);
        modifiers.push(() ->
                sceneShader.setUniform("background", 0));
    }

    public void drawBlackAsAlpha() {
        if (task == Task.DEPTH_MAP) return;
        sceneShader.setUniform("blackAsAlpha", 1);
        modifiers.push(() ->
                sceneShader.setUniform("blackAsAlpha", 0));
    }
    /**
     * Temporarily change the ambient light of an object. This effect applies to the object currently rendered.
     *
     * @param color The ambient light color.
     */
    public void ambientLight(Vector3f color) {
        if (task == Task.DEPTH_MAP) return;
        Vector3f newColor = new Vector3f(color);
        sceneShader.setUniform("ambientLight", newColor.add(ambientLight));
        modifiers.push(() ->
                sceneShader.setUniform("ambientLight", sceneShader.getAmbientLight()));
    }

    /**
     * Disable directional light for the currently rendered object.
     */
    public void noDirectionalLight() {
        if (task == Task.DEPTH_MAP) return;

        sceneShader.setUniform("directionalLightOff", 1);
        modifiers.push(() ->
                sceneShader.setUniform("directionalLightOff", 0));
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
     * Get the ViewMatrix of the currently active camera.
     *
     * @return The ViewMatrix.
     */
    public Matrix4f getViewMatrix() {
        return transformation.getViewMatrix(getActiveCamera());
    }

    /**
     * Set the view matricces for the current {@link Renderer#task}. Sets the modelView and modelLightView matrix for
     * the scene if task is Task.SCENE. Sets the modelLightView for the depthmap if task is Task.DEPTH_MAP.
     *
     * @param position The position of the objects that will be rendered next.
     * @param rotation The rotation of the objects that will be rendered next.
     * @param scale The scale of the objects that will be rendered next.
     */
    public void setMatrix(Vector3f position, Vector3f rotation, Vector3f scale) {
        if (task == Task.SCENE) {
            setModelViewMatrix(position, rotation, scale);
        } else if (task == Task.DEPTH_MAP) {
            setModelLightViewMatrix(position, rotation, scale);
        }
    }

    /**
     * Set the modelview matrix. This sets the location, rotation and scale of the things to be rendered next. Besides
     * it takes into account where the activeCamera is at.
     *
     * @param position The position of the objects that will be rendered next.
     * @param rotation The rotation of the objects that will be rendered next.
     * @param scale The scale of the objects that will be rendered next.
     */
    private void setModelViewMatrix(Vector3f position, Vector3f rotation, Vector3f scale) {
        Matrix4f transformationMatrix = transformation.getModelViewMatrix(position, rotation, scale, getActiveCamera());
        sceneShader.setUniform("modelViewMatrix", transformationMatrix);
        setModelLightViewMatrixScene(position, rotation, scale);
    }

    /**
     * Set the modelLightView in case you're drawing the scene.
     *
     * @param position The position of the objects that will be rendered next.
     * @param rotation The rotation of the objects that will be rendered next.
     * @param scale The scale of the objects that will be rendered next.
     */
    private void setModelLightViewMatrixScene(Vector3f position, Vector3f rotation, Vector3f scale) {
        Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(position, rotation, scale, sceneLightViewMatrix);
        sceneShader.setUniform("modelLightViewMatrix", modelLightViewMatrix);
    }

    /**
     * Set the modelLightView in case you're drawing the depthmap.
     *
     * @param position The position of the objects that will be rendered next.
     * @param rotation The rotation of the objects that will be rendered next.
     * @param scale The scale of the objects that will be rendered next.
     */
    private void setModelLightViewMatrix(Vector3f position, Vector3f rotation, Vector3f scale) {
        Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(position, rotation, scale, lightViewMatrix);
        depthShader.setUniform("modelLightViewMatrix", modelLightViewMatrix);
    }

    /**
     * Reset all applied modifiers.
     */
    private void resetModifiers() {
        while (!modifiers.isEmpty()) modifiers.pop().run();
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
     * Render a mesh based on a collection of renderers which set the variables needed to draw the mesh in the
     * right conditions.
     *
     * @param renderers A collection of runnables.
     */
    private void renderAll(Mesh mesh, Stream<Runnable> renderers) {
        mesh.initRender();

        renderers.forEach((render) -> {
            render.run();
            mesh.draw();
            resetModifiers();
        });

        mesh.endRender();
    }

    /**
     * Render the main objects of the scene.
     */
    private void renderScene() {
        task = Task.SCENE;

        sceneShader.bind();

        Matrix4f projectionMatrix = window.getProjectionMatrix();
        sceneShader.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
        sceneShader.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        sceneLightViewMatrix = transformation.getLightViewMatrix();

        Matrix4f viewMatrix = getViewMatrix();

        renderLights(viewMatrix);



        if (shadowMapping) {
            sceneShader.setUniform("depthMapEnabled", 1);
            sceneShader.setUniform("depthMap", 1);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, depthMap.getDepthMapTexture().getId());
        } else {
            sceneShader.setUniform("depthMap", 0);
            sceneShader.setUniform("depthMapEnabled", 0);
        }

        //first draw non transparent meshes
        instancedMeshes.forEach((mesh, consumers) -> {
            if (!mesh.getMaterial().getTransparency()) {
                setMaterial(mesh.getMaterial());
                renderAll(mesh, consumers.stream().map(InstancedMesh::getRender));
            }
        });
        //overlay with transparent meshes
        instancedMeshes.forEach((mesh, consumers) -> {
            if (mesh.getMaterial().getTransparency()) {
                setMaterial(mesh.getMaterial());
                renderAll(mesh, consumers.stream().map(InstancedMesh::getRender));
            }
        });

        sceneShader.unbind();
    }

    private void renderDepthMap() {
        task = Task.DEPTH_MAP;

        // Setup view port to match the texture size
        glBindFramebuffer(GL_FRAMEBUFFER, depthMap.getDepthMapFBO());
        glViewport(0, 0, depthMap.width, depthMap.height);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShader.bind();

        DirectionalLight light = sceneShader.getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        float lightAngleX = (float)Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(lightDirection.x*-1));
        float lightAngleZ = 0;
        lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowStrength()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));

        DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

        depthShader.setUniform("orthoProjectionMatrix", orthoProjMatrix);

        instancedMeshes.forEach((mesh, consumers) -> {
            if (mesh.CastShadows()) {
                renderAll(mesh, consumers.stream().map(InstancedMesh::getRender));
            }
        });

        // Unbind
        depthShader.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        if (shadowMapping) {
            renderDepthMap();
        }

        glViewport(0, 0, window.getWidth(), window.getHeight());
        renderScene();
    }
}
