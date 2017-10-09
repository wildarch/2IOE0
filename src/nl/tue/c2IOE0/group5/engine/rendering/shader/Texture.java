package nl.tue.c2IOE0.group5.engine.rendering.shader;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * @author Jorren Hendriks.
 */
public class Texture {

    int id;

    public Texture(int id) {
        this.id = id;
    }

    public Texture(String filename) throws IOException {
        this(loadTexture(filename));
    }

    private static int loadTexture(String filename) throws IOException {
        int id;

        PNGDecoder decoder = new PNGDecoder(Texture.class.getResourceAsStream(filename));

        ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buf.flip();

        // Create a new OpenGL texture
        id = glGenTextures();
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, id);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

        // generate MipMap
        glGenerateMipmap(GL_TEXTURE_2D);

        return id;
    }

    public int getId() {
        return id;
    }

    public void cleanup() {
        glDeleteTextures(id);
    }

}
