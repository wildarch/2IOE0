package nl.tue.c2IOE0.group5.engine.rendering.shader;

import nl.tue.c2IOE0.group5.engine.rendering.MeshException;
import org.joml.Vector4f;

/**
 * @author Jorren Hendriks.
 */
public class Material {

    private static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private Vector4f ambientColour;

    private Vector4f diffuseColour;

    private Vector4f specularColour;

    private float reflectance;

    private Texture texture;

    private boolean transparent = false;

    @Deprecated
    public Material() {
        this.ambientColour = DEFAULT_COLOUR;
        this.diffuseColour = DEFAULT_COLOUR;
        this.specularColour = DEFAULT_COLOUR;
        this.texture = null;
        this.reflectance = 0;
    }



    public void setTransparency(boolean value) { this.transparent = value; }
    public boolean getTransparency() { return this.transparent; };

    @Deprecated
    public Material(Vector4f colour, float reflectance) {
        this(colour, colour, colour, null, reflectance);
    }

    public Material(Texture texture) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, 0);
    }

    public Material(String texture) throws MeshException {
        this(texture, 0);
    }

    public Material(String texture, float reflectance) throws MeshException {
        this(new Texture(texture), reflectance);
    }

    public Material(Texture texture, float reflectance) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, reflectance);
    }

    @Deprecated
    public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour, Texture texture, float reflectance) {
        this.ambientColour = ambientColour;
        this.diffuseColour = diffuseColour;
        this.specularColour = specularColour;
        this.texture = texture;
        this.reflectance = reflectance;
    }

    public Vector4f getAmbientColour() {
        return ambientColour;
    }

    public void setAmbientColour(Vector4f ambientColour) {
        this.ambientColour = ambientColour;
    }

    public Vector4f getDiffuseColour() {
        return diffuseColour;
    }

    public void setDiffuseColour(Vector4f diffuseColour) {
        this.diffuseColour = diffuseColour;
    }

    public Vector4f getSpecularColour() {
        return specularColour;
    }

    public void setSpecularColour(Vector4f specularColour) {
        this.specularColour = specularColour;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

}
