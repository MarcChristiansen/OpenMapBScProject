package openmap.gui;

import java.awt.*;

/**
 * Simple class to represent if a node should be drawn and what color it should be. Primarily meant for lambdas given by pathfinder to visualization

 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 15-04-2021
 */
public class NodeDrawingInfo {
    private boolean shouldDraw;
    private Color color;

    public NodeDrawingInfo(boolean shouldDraw, Color color) {
        this.shouldDraw = shouldDraw;
        this.color = color;
    }

    public boolean shouldDraw() {
        return shouldDraw;
    }

    public Color getColor(){
        return color;
    }
}
