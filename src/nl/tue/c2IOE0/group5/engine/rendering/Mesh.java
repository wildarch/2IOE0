package nl.tue.c2IOE0.group5.engine.rendering;

import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Texture;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Jorren Hendriks.
 */
public class Mesh {

    private final int vaoId;

    private final int posVboID;
    private final int idVboID;
    private final int texVboID;
    private final int norVboID;

    private final int vertexCount;

    private Material material;

    private final Vector3f minBoundingBox;
    private final Vector3f maxBoundingBox;

    private boolean render;

    public Mesh(float[] positions, float[] texCoords, float[] normals, int[] indices) {
        FloatBuffer posBuffer = null;
        FloatBuffer texBuffer = null;
        FloatBuffer norBuffer = null;
        IntBuffer indicesBuffer = null;
        try {

            vertexCount = indices.length;

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            posVboID = glGenBuffers();
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, posVboID);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture VBO
            texVboID = glGenBuffers();
            texBuffer = MemoryUtil.memAllocFloat(texCoords.length);
            texBuffer.put(texCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, texVboID);
            glBufferData(GL_ARRAY_BUFFER, texBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            norVboID = glGenBuffers();
            norBuffer = MemoryUtil.memAllocFloat(normals.length);
            norBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, norVboID);
            glBufferData(GL_ARRAY_BUFFER, norBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Index VBO
            idVboID = glGenBuffers();
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idVboID);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (texBuffer != null) {
                MemoryUtil.memFree(texBuffer);
            }
            if (norBuffer != null) {
                MemoryUtil.memFree(norBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }

        minBoundingBox = new Vector3f();
        maxBoundingBox = new Vector3f();
        for (int i = 0; i < positions.length; i += 3) {
            float x = positions[i];
            float y = positions[i+1];
            float z = positions[i+2];

            minBoundingBox.x = Math.min(minBoundingBox.x, x);
            minBoundingBox.y = Math.min(minBoundingBox.y, y);
            minBoundingBox.z = Math.min(minBoundingBox.z, z);

            maxBoundingBox.x = Math.max(maxBoundingBox.x, x);
            maxBoundingBox.y = Math.max(maxBoundingBox.y, y);
            maxBoundingBox.z = Math.max(maxBoundingBox.z, z);
        }
    }

    public boolean isTextured() {
        return this.material!= null && this.material.isTextured();
    }

    public Texture getTexture() {
        return this.material.getTexture();
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    private int getVaoId() {
        return vaoId;
    }

    private int getVertexCount() {
        return vertexCount;
    }

    public void cleanup() {
        if (isTextured()) {
            getMaterial().getTexture().cleanup();
        }

        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(posVboID);
        glDeleteBuffers(texVboID);
        glDeleteBuffers(idVboID);
        glDeleteBuffers(texVboID);


        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    /**
     * Initialize the render of this mesh.
     */
    void initRender() {
        if (isTextured()) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, getTexture().getId());
        }

        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        render = true;
    }

    /**
     * Disable fields associated with rendering this mesh.
     */
    void endRender() {
        render = false;

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    void draw() {
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
    }

    public Vector3f getMinBoundingBox() {
        return minBoundingBox;
    }

    public Vector3f getMaxBoundingBox() {
        return maxBoundingBox;
    }

    public static Vector3f combinedMinBoundingBox(Mesh... meshes) {
        Vector3f boundingBox = new Vector3f();
        for (Mesh mesh : meshes) {
            boundingBox.x = Math.min(mesh.minBoundingBox.x, boundingBox.x);
            boundingBox.y = Math.min(mesh.minBoundingBox.y, boundingBox.y);
            boundingBox.z = Math.min(mesh.minBoundingBox.z, boundingBox.z);
        }
        return boundingBox;
    }

    public static Vector3f combinedMaxBoundingBox(Mesh... meshes) {
        Vector3f boundingBox = new Vector3f();
        for (Mesh mesh : meshes) {
            boundingBox.x = Math.max(mesh.maxBoundingBox.x, boundingBox.x);
            boundingBox.y = Math.max(mesh.maxBoundingBox.y, boundingBox.y);
            boundingBox.z = Math.max(mesh.maxBoundingBox.z, boundingBox.z);
        }
        return boundingBox;
    }

}
