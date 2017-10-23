package nl.tue.c2IOE0.group5.engine.rendering.shader;

import de.matthiasmann.twl.utils.PNGDecoder;
import nl.tue.c2IOE0.group5.engine.rendering.MeshException;
import org.lwjgl.opengl.GL12;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * @author Jorren Hendriks.
 */
public class Texture {

    private static Map<String, Integer> cache = new HashMap<>();

    int id;

    private int width;

    private int height;

    public Texture(int id) {
        this.id = id;
    }

    public Texture(String filename) throws MeshException {
        this(loadTexture(filename));
    }

    private static int loadTexture(String filename) throws MeshException {
        int id;

        if (cache.containsKey(filename)) {
            return cache.get(filename);
        }

        try {
            PNGDecoder decoder = new PNGDecoder(Texture.class.getResourceAsStream(filename));

            ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buf.flip();

            // Create a new OpenGL texture
            id = glGenTextures();
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, id);

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

            // generate MipMap
            glGenerateMipmap(GL_TEXTURE_2D);

            cache.put(filename, id);
            return id;
        } catch (IOException e) {
            throw new MeshException(e.getMessage());
        }
    }

    Texture(int width, int height, int pixelFormat) throws Exception {
        this.id = glGenTextures();
        this.width = width;
        this.height = height;
        glBindTexture(GL_TEXTURE_2D, this.id);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, this.width, this.height, 0, pixelFormat, GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    }

    public int getId() {
        return id;
    }

    public void cleanup() {
        glDeleteTextures(id);
    }

}
