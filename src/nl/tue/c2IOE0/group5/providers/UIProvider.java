package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.userinterface.UIButton;
import nl.tue.c2IOE0.group5.userinterface.UIElement;
import org.joml.Vector4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    List<UIElement> elements;

    @Override
    public void init(Engine engine) {
        this.hud = engine.getHud();

        elements = new ArrayList<>();

        UIButton button = new UIButton(10, 10, 40, 40, () -> {
            try {
                hud.image("/texture.png", 10, 10, 40, 40, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, (event) -> {
            System.out.println("HUD is being sexually harrased");
        });

        elements.add(button);

        hud.create(() -> {
            elements.forEach(UIElement::draw);

            try {
                hud.polygon(30, bottom(20),
                        20,             bottom(30),
                        20,             bottom(60),
                        30,             bottom(70),
                        right(30),    bottom(70),
                        right(20),    bottom(60),
                        right(20),    bottom(30),
                        right(30),    bottom(20)
                );
                hud.fill(color.x, color.y, color.z, color.w);
                hud.stroke(5, 0.6f, 0.1f, 1f, 1f);
                hud.image("/texture.png", wWidth-70, wHeight-60, 30, 30, 0.6f);
                hud.text(40, wHeight - 52, 25f, Hud.Font.MEDIUM, NVG_ALIGN_LEFT | NVG_ALIGN_TOP, "Welcome to the HUD " + x, textColor);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public boolean onClick(MouseEvent event) {
        final boolean[] passthrough = {true};

        elements.forEach(element -> {
            if (element instanceof UIButton) {
                if (element.contains(event.getPosition())) {
                    ((UIButton) element).onClick(event);
                    passthrough[0] = false;
                }
            }
        });

        return passthrough[0];

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

    private int bottom(int y) {
        return this.wHeight - y;
    }

    private int right(int x) {
        return this.wWidth - x;
    }
}
