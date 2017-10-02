package nl.tue.c2IOE0.group5.engine.rendering;

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

    private Texture texture;

    public Mesh(float[] positions, float[] texCoords, float[] normals, int[] indices) {
        for (int i = 0; i < texCoords.length; i++) {
            System.out.print(texCoords[i] + " ");
        }

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
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }


    private int getVaoId() {
        return vaoId;
    }

    private int getVertexCount() {
        return vertexCount;
    }

    public void cleanup() {
        if (texture != null) {
            texture.cleanup();
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

    public void draw() {
        // If we have a texture for our mesh
        if (texture != null) {
            // Activate first texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }
        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

}
