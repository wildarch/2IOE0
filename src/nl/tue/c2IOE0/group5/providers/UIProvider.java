package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import org.joml.Vector4f;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;

/**
 * @author Jorren
 */
public class UIProvider implements Provider {

    private Hud hud;

    private int x = 3;
    private int wHeight = 0;
    private int wWidth = 0;

    private final Vector4f color = new Vector4f(0.3f, 0.3f, 0.8f, 0.8f);
    private final Vector4f textColor = new Vector4f(1f, 1f, 1f, 1f);

    @Override
    public void init(Engine engine) {
        this.hud = engine.getHud();

        hud.create(() -> {
            hud.poly(new int[][] {
                    new int[]{30, wHeight-20},
                    new int[]{20, wHeight-30},
                    new int[]{20, wHeight-60},
                    new int[]{30, wHeight-70},
                    new int[]{wWidth-30, wHeight-70},
                    new int[]{wWidth-20, wHeight-60},
                    new int[]{wWidth-20, wHeight-30},
                    new int[]{wWidth-30, wHeight-20}
            });
            hud.fill(color.x, color.y, color.z, color.w);
            hud.stroke(5, 0.6f, 0.1f, 1f, 1f);
            hud.text(40, wHeight - 52, 25f, Hud.Font.MEDIUM, NVG_ALIGN_LEFT | NVG_ALIGN_TOP, "Welcome to the HUD " + x, textColor);
        });
    }

    @Override
    public void update() {
        this.x ++;
        this.x %= 100;
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        this.wWidth = window.getWidth();
        this.wHeight = window.getHeight();
    }
}
