package nl.tue.c2IOE0.group5.userinterface;

import org.joml.Vector4f;

import java.util.Set;

/**
 * Created by s152717 on 12-10-2017.
 */
public class MenuParent extends MenuItem {

    private Set<MenuItem> children;

    public MenuParent(int x, int y, int width, int height, Vector4f color, Vector4f textColor, Vector4f lineColor, String text, Set<MenuItem> children) {
        super(text, () -> {

                }
        );
        this.children = children;
    }

    @Override
    public void onClick() {

    }

    public void getChildren(){

    }
}
