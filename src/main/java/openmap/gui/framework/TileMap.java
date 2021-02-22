package openmap.gui.framework;

import java.awt.*;
import java.util.List;

public interface TileMap {
    void drawMapView(double x, double y, int gWindowWidth, int gWindowHeight, double zoomFactor, Graphics2D g);

    void setHighlightedPath(List<Long> nodeList);
}
