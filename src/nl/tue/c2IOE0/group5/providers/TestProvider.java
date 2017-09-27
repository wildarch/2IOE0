package nl.tue.c2IOE0.group5.providers;

import de.matthiasmann.twl.utils.PNGDecoder;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author Jorren Hendriks
 */
public class TestProvider implements Provider {

    // register resources here, e.g.
    private int updatecounter;

    private Mesh mesh;
    private Mesh mesh2;

    @Override
    public void init(Engine engine) {
        // initialize resources here, e.g.
        updatecounter = 0;

        this.mesh = new Mesh(new float[]{
                -0.5f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f,  0.5f, 0.0f,
        }, new int[]{
                0, 1, 3, 3, 1, 2,
        });
/*
        this.mesh2 = new Mesh(new float[] {
                -0.8f,  0.5f, 0.0f,
                -0.8f, -0.5f, 0.0f,
                0.5f,  0.5f, 0.0f
        }, new int[]{0, 1, 2});
*/
    }

    public void ud() {
        updatecounter += 1;
        updatecounter %= 5;
    }

    @Override
    public void update() {
        // do updates here using resources, e.g.

    }

    @Override
    public void draw(Window window) {
        // draw attached objects here, e.g.
        float r = 0;
        float g = 0;
        float b = 0;

        switch(updatecounter) {
            case 0:
                g = 1;
            case 1:
                r = 1;
                break;
            case 2:
                g = 1;
            case 3:
                b = 1;
                break;
            case 4:
                r = 1;
                b = 1;
        }

        window.setClearColor(r, g, b, 1f);


        //mesh.draw();

        //mesh2.draw();
/*
        float[] vertices = new float[]{
                0.0f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
        verticesBuffer.put(vertices).flip();



        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(verticesBuffer);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        // Unbind the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Unbind the VAO
        glBindVertexArray(0);

        if (verticesBuffer != null) {
            MemoryUtil.memFree(verticesBuffer);
        }
*/
    }
}
