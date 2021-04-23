package openmap.gui.framework;

import openmap.framework.Node;

import java.awt.*;
import java.util.List;

/**
 * Interface for the manager of a tilemap
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 017-03-2021
 */
public interface TileMap {
    /**
     * Draw the map view
     * @param panX Current x pan
     * @param panY Current y pan
     * @param windowWidth The height of the graphics window used.
     * @param windowHeight The height of the graphics window used.
     * @param zoomFactor The current zoom factor on the map.
     * @param g the graphics to draw to.
     */
    void drawMapView(double panX, double panY, int windowWidth, int windowHeight, double zoomFactor, Graphics2D g);

    void setHighlightedPath(List<Node> nodeList);

    void setLandmarks(List<Node> landmarks);

    void setLandmarksUsed(List<Integer> landmarksUsed);

    void setShouldDrawLandmarks(boolean shouldDrawLandmarks);
}
