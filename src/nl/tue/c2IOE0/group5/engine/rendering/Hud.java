package nl.tue.c2IOE0.group5.engine.rendering;

import nl.tue.c2IOE0.group5.util.Resource;
import org.joml.Vector4f;
import org.lwjgl.nanovg.NVGColor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Jorren
 */
public class Hud implements Drawable {

    public enum Font {
        REGULAR("/fonts/Orbitron-Regular.ttf"),
        MEDIUM("/fonts/Orbitron-Medium.ttf"),
        BOLD("/fonts/Orbitron-Bold.ttf"),
        BLACK("/fonts/Orbitron-Black.ttf");

        public final String name;
        public final String source;

        Font(String source) {
            this.name = toString();
            this.source = source;
        }
    }

    private long vg;
    private NVGColor color;
    private ByteBuffer fontBuffer;

    private List<Runnable> drawBuffer;

    /**
     * Initialize the Hud.
     *
     * @param window The window on which the hud is drawn.
     * @throws Exception If an error occures during the setup of the Hud.
     */
    public void init(Window window) throws Exception {
        vg = window.getOptions().antialiasing() ? nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) :
                nvgCreate(NVG_STENCIL_STROKES);
        if (this.vg == NULL) {
            throw new Exception("Could not initialize NanoVG");
        }

        Font font = Font.MEDIUM;
        fontBuffer = Resource.toByteBuffer(font.source, 96 * 1024);
        int f = nvgCreateFontMem(vg, font.name , fontBuffer, 0);
        if (f == -1) {
            throw new Exception("Could not create font " + font.name);
        }
        color = NVGColor.create();

        drawBuffer = new ArrayList<>();
    }

    /**
     * Create something for the hud to be drawn. Package the NanoVG draw commands inside a runnable which will be
     * executed once the Hud is ready to draw.
     *
     * @param render The code for drawing inside the hud.
     */
    public void create(Runnable render) {
        drawBuffer.add(render);
    }

    /**
     * Remove an existing draw handler from the Hud.
     *
     * @param render The handler to remove.
     */
    public void destroy(Runnable render) {
        drawBuffer.remove(render);
    }

    /**
     * Get an instance of NVGColor with the correct values. All color values are floating point numbers supposed to be
     * between 0f and 1f.
     *
     * @param red The red component.
     * @param green The green component.
     * @param blue The blue component.
     * @param alpha The alpha component.
     * @return an instance of NVGColor.
     */
    public NVGColor rgba(float red, float green, float blue, float alpha) {
        color.r(red);
        color.g(green);
        color.b(blue);
        color.a(alpha);

        return color;
    }

    /**
     * {@link #rgba(float, float, float, float)}
     */
    public NVGColor rgba(Vector4f color) {
        return rgba(color.x, color.y, color.z, color.w);
    }

    public void rectangle(int x, int y, int width, int height, Vector4f color) {
        nvgBeginPath(vg);
        nvgRect(vg, x, y, width, height);
    }

    public void circle(int x, int y, int radius, Vector4f color) {
        nvgBeginPath(vg);
        nvgCircle(vg, x, y, radius);
    }

    public void poly(int[][] points) {
        int length = points.length;
        nvgBeginPath(vg);
        nvgMoveTo(vg, points[length-1][0], points[length-1][1]);
        for (int[] point : points) {
            nvgLineTo(vg, point[0], point[1]);
        }
    }

    public void text(int x, int y, float size, Font font, int align, String text, Vector4f color) {
        nvgFontSize(vg, size);
        nvgFontFace(vg, font.name);
        nvgTextAlign(vg, align);
        nvgFillColor(vg, rgba(color));
        nvgText(vg, x, y, text);
    }

    public void fill(float red, float green, float blue, float alpha) {
        nvgFillColor(vg, rgba(red, green, blue, alpha));
        nvgFill(vg);
    }

    public void stroke(int width, float red, float green, float blue, float alpha) {
        nvgStrokeWidth(vg, width);
        nvgStrokeColor(vg, rgba(red, green, blue, alpha));
        nvgStroke(vg);
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        // Begin NanoVG frame
        nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);
        // Draw all drawhandlers
        drawBuffer.forEach(Runnable::run);
        // End NanoVG frame
        nvgEndFrame(vg);

        // restore window state
        window.restoreState();
    }

    /**
     * Clean up the Hud.
     */
    public void cleanup() {
        nvgDelete(vg);
    }

}
