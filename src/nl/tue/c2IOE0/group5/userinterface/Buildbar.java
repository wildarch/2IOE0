package nl.tue.c2IOE0.group5.userinterface;

import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.providers.UIProvider;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.AbstractTower.MetaData;

import java.io.IOException;
import java.util.List;

import static nl.tue.c2IOE0.group5.providers.UIProvider.*;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;

/**
 * @author Jorren
 */
public class Buildbar extends UIButton {

    private UIProvider provider;

    private final static int MARGIN = 10;
    public static final int STROKE_WIDTH = 5;
    public static final int INDENT = 10;

    private UIButton[] buildings;

    public Buildbar(int tilewidth, int tileheight, UIProvider provider) {
        super(0, 0, MARGIN + provider.towerProvider.all().size() * (tilewidth + MARGIN), 2 * MARGIN + tileheight);

        List<Class<? extends AbstractTower>> towers = provider.towerProvider.all();

        buildings = new UIButton[towers.size()];
        x += MARGIN;
        y += MARGIN;
        for (int i = 0; i < buildings.length; i++) {
            Class<? extends AbstractTower> tower = towers.get(i);
            MetaData metaData = AbstractTower.getMetaData(tower);
            buildings[i] = new UIButton(x, y, tilewidth, tileheight) {
                @Override
                public void onClick(MouseEvent event) {
                                                    provider.select(tower);
                                                                           }

                    @Override
                    public void draw(Hud hud) {
                        try {
                            hud.image(metaData.icon, this.x + 10, this.y, this.width - 10, this.height - 30, 1f);
                            hud.text(this.x + tilewidth/2, this.y + this.height - 30, 18, Hud.Font.MEDIUM,
                                    NVG_ALIGN_CENTER | NVG_ALIGN_TOP, metaData.name, COLOR_TEXT);
                            hud.text(this.x + tilewidth/2, this.y + this.height - 10, 18, Hud.Font.MEDIUM,
                                    NVG_ALIGN_CENTER | NVG_ALIGN_TOP, "" + metaData.price, COLOR_TEXT);
                            if (provider.getSelected() == tower) {
                                hud.rectangle(this.x, this.y, this.width, this.height);
                                hud.fill(COLOR_DARK);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to " + metaData.icon, e);
                        }
                    }
                };

            x += tilewidth + MARGIN;
        }

    }

    @Override
    public void draw(Hud hud) {
        hud.roundedRectangle(x, y, width, height, INDENT);
        hud.fill(COLOR_BLUE);
        hud.stroke(STROKE_WIDTH, COLOR_PINK);

        for (UIButton button : buildings) {
            button.draw(hud);
        }
    }

    @Override
    public void onClick(MouseEvent event) {
        for (UIButton element : buildings) {
            if (element.contains(event.getPosition())) {
                element.onClick(event);
                break;
            }
        }
    }

    @Override
    public void setX(int x) {
        this.x = x;
        x += MARGIN;
        for (UIButton element : buildings) {
            element.setX(x);
            x += element.getWidth() + MARGIN;
        }
    }

    @Override
    public void setY(int y) {
        this.y = y;
        y += MARGIN;
        for (UIButton element : buildings) {
            element.setY(y);
        }
    }
}
