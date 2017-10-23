package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import org.joml.Vector4f;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class UIText extends UIElement {
    Supplier<String> supplier;

    public UIText(int x, int y, int width, int height, Supplier<String> contentSupplier) {
        super(x, y, width, height);
        this.supplier = contentSupplier;
    }

    @Override
    public void draw(Hud hud) {
        hud.text(x, y, 30, Hud.Font.MEDIUM, 0, supplier.get(), new Vector4f(1f));
    }
}
