package nl.tue.c2IOE0.group5.engine.rendering;

import nl.tue.c2IOE0.group5.engine.objects.Camera;
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
 * Created by Administrator on 9-10-2017.
 */
public class ShaderProgram {

    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private final Map<String, Integer> uniforms;


    // FOV in radians
    private static final float FOV = (float) Math.toRadians(60.0f);
    // z-coordinates relative to the activeCamera.
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;

    private final Transformation transformation;


    private float specularPower = 10f;
    private Vector3f ambientLight = new Vector3f();
    private DirectionalLight directionalLight = new DirectionalLight(
            new Vector3f(),
            new Vector3f(),
            1f
    );
    private PointLight pointLight = new PointLight(
            new Vector3f(),
            new Vector3f(),
            1f);
    private PointLight.Attenuation pointLightAtt = new PointLight.Attenuation(0f, 0f, 0f);


    private Camera activeCamera;

    public ShaderProgram() {
        transformation = new Transformation();
        uniforms = new HashMap<>();
    }

    public void init() throws ShaderException {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new ShaderException("Could not create Shader");
        }
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
     * Link the program and cleanup the shaders.
     *
     * @throws ShaderException If an error occures linking the shader code.
     */
    public void link() throws ShaderException {
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


    public Vector3f getAmbientLight() {
        return this.ambientLight;
    }
    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public DirectionalLight getDirectionalLight  () {
        return this.directionalLight;
    }
    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public PointLight getPointLight() {
        return this.pointLight;
    }
    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    /**
     * applies a bounce-effect around the given gravity-middle, stretching boundingMin/2 up and down.
     * this effect stays until {@link #unboink()} has been called
     * @param bounceDegree the strength B of the effect, with B = 0 no effect,
     *                     B > 0 a horizontal expansion and B < 0 a vertical stretch
     * @param boundingMin the minimum coordinates of the 3D-model
     * @param boundingMax idem maximum
     * @param render the runnable where the bounce effect will apply to
     */
    public void boink(float bounceDegree, Vector3f boundingMin, Vector3f boundingMax, Runnable render){
        setUniform("bounceDegree", bounceDegree);
        setUniform("boundingMin", boundingMin);
        setUniform("boundingMax", boundingMax);

        render.run();

        setUniform("bounceDegree", 0f);
    }

    /**
     * Sets up the renderer to draw a skybox, e.g. an object that doesn't care but just draws its texture.
     *
     * @param render The code to render in skybox mode.
     */
    public void drawSkybox(Runnable render) {
        setUniform("isSkybox", 1);

        render.run();

        setUniform("isSkybox", 0);
    }

    public void drawHealthBolletje(Runnable render) {
        DirectionalLight directionalLightOff = new DirectionalLight(
                new Vector3f(),
                new Vector3f(),
                0f
        );
        setDirectionalLight(directionalLightOff);
        render.run();
        setDirectionalLight(directionalLight);
    }

    /**
     * disables previously activated bounce-effects
     * @see #boink(float, Vector3f, Vector3f, Runnable)
     */
    public void unboink(){
        setUniform("bounceDegree", 0f);
    }

    public void ambientLight(Vector3f color, Runnable render) {
        setUniform("ambientLight", color);
        render.run();
        setUniform("ambientLight", ambientLight);
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
     * Set the material of currently rendered object.
     *
     * @param material The material of the object.
     */
    public void setMaterial(Material material) {
        setUniform("material", material);
    }

    /**
     * Updatable the projection matrix, this has to do with the perspective of the activeCamera.
     *
     * @param window The window on which the scene will be rendered.
     */
    public void updateProjectionMatrix(Window window) {
        // Updatable projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix(getActiveCamera());

        // Updatable Light Uniforms
        setUniform("ambientLight", ambientLight);
        setUniform("specularPower", specularPower);
        // Get a copy of the light object and transform its position to view coordinates
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos, 1);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        setUniform("pointLight", currPointLight);
        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        setUniform("directionalLight", currDirLight);

        setUniform("texture_sampler", 0);
    }

    public Matrix4f getViewMatrix() {
        return transformation.getViewMatrix(getActiveCamera());
    }

    public Matrix4f getProjectionMatrix(Window window) {
        return transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
    }

    /**
     * Create the uniforms required for a PointLight
     *
     * @param uniformName The name of the uniform
     * @throws ShaderException If an error occurs getting the memory location.
     */
    public void createPointLightUniform(String uniformName) throws ShaderException {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".att.constant");
        createUniform(uniformName + ".att.linear");
        createUniform(uniformName + ".att.exponent");
    }

    /**
     * Create the uniforms required for a DirectionalLight
     *
     * @param uniformName The name of the uniform
     * @throws ShaderException If an error occurs getting the memory location.
     */
    public void createDirectionalLightUniform(String uniformName) throws ShaderException {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    /**
     * Create the uniforms required for a Material
     *
     * @param uniformName The name of the uniform
     * @throws ShaderException If an error occurs getting the memory location.
     */
    public void createMaterialUniform(String uniformName) throws ShaderException {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }

    /**
     * Create a new uniform and get its memory location.
     *
     * @param uniformName The name of the uniform.
     * @throws ShaderException If an error occurs getting the memory location.
     */
    public void createUniform(String uniformName) throws ShaderException {
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
    public void setUniform(String uniformName, Matrix4f value) {
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
    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    /**
     * Set the value of a certain float shader uniform
     *
     * @param uniformName The name of the uniform.
     * @param value The new value of the uniform.
     */
    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    /**
     * Set the value of a certain 3D Vector shader uniform
     *
     * @param uniformName The name of the uniform.
     * @param value The new value of the uniform.
     */
    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    /**
     * Set the value of a certain 4D Vector shader uniform
     *
     * @param uniformName The name of the uniform.
     * @param value The new value of the uniform.
     */
    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    /**
     * Set the value of a certain PointLight shader uniform
     *
     * @param uniformName The name of the uniform.
     * @param pointLight The new value of the uniform.
     */
    public void setUniform(String uniformName, PointLight pointLight) {
        setUniform(uniformName + ".colour", pointLight.getColor() );
        setUniform(uniformName + ".position", pointLight.getPosition());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation att = pointLight.getAttenuation();
        setUniform(uniformName + ".att.constant", att.getConstant());
        setUniform(uniformName + ".att.linear", att.getLinear());
        setUniform(uniformName + ".att.exponent", att.getExponent());
    }

    /**
     * Set the value of a certain DirecionalLight shader uniform
     *
     * @param uniformName The name of the uniform.
     * @param directionalLight The new value of the uniform.
     */
    public void setUniform(String uniformName, DirectionalLight directionalLight) {
        setUniform(uniformName + ".colour", directionalLight.getColor() );
        setUniform(uniformName + ".direction", directionalLight.getDirection());
        setUniform(uniformName + ".intensity", directionalLight.getIntensity());
    }

    /**
     * Set the value of a certain Material shader uniform
     *
     * @param uniformName The name of the uniform.
     * @param material The new value of the uniform.
     */
    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".ambient", material.getAmbientColour());
        setUniform(uniformName + ".diffuse", material.getDiffuseColour());
        setUniform(uniformName + ".specular", material.getSpecularColour());
        setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    /**
     * Create a new vertexshader and set the vertexshader id field to that of the newly created shader.
     *
     * @param shaderCode The shaderCode as a String.
     * @throws ShaderException If an error occurs during the creation of a shader.
     */
    public void createVertexShader(String shaderCode) throws ShaderException {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    /**
     * Create a new fragmentshader and set the fragmentshader id field to that of the newly created shader.
     *
     * @param shaderCode The shaderCode as a String.
     * @throws ShaderException If an error occurs during the creation of a shader.
     */
    public void createFragmentShader(String shaderCode) throws ShaderException {
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
    public int createShader(String shaderCode, int shaderType) throws ShaderException {
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


}
