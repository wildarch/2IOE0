package nl.tue.c2IOE0.group5.engine.rendering.shader;

import nl.tue.c2IOE0.group5.engine.rendering.ShaderException;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Yoeri Poels.
 */
public class DepthMap {

    /*
    public int width = 4096;
    public int height = 4096;
    */
    //Has to be a multiple of 2 to be run on intel graphics!
    public int width = 1024;
    public int height = 1024;

    private int depthFboID;

    private Texture depthMap;

    public DepthMap() throws Exception {
        // Create an FBO to render the depth map
        depthFboID = glGenFramebuffers();

        // Create the depth map texture
        depthMap = new Texture(width, height, GL_DEPTH_COMPONENT);

        // Attach the the depth map texture to the FBO
        glBindFramebuffer(GL_FRAMEBUFFER, depthFboID);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getId(), 0);
        // Only draw the depth, no colours
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new ShaderException("Failed creating frame buffer.");
        }

        // Unbind
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Texture getDepthMapTexture() {
        return depthMap;
    }

    public int getDepthMapFBO() {
        return depthFboID;
    }

    public void cleanup() {
        glDeleteFramebuffers(depthFboID);
        depthMap.cleanup();
    }
}
