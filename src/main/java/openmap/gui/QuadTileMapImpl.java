package openmap.gui;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.gui.framework.TileMap;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class QuadTileMapImpl implements TileMap {



    QuadTile rootTile;
    Graph graph;

    public QuadTileMapImpl(Graph graph, byte maxLayer, int height, int width){
        this.graph = graph;

        double initZoomLvl = Math.min((double)(height)/(graph.getBounds().getMaxY() - graph.getBounds().getMinY()),
                (double)(width)/(graph.getBounds().getMaxX() - graph.getBounds().getMinX()));

        this.rootTile = new QuadTile(maxLayer, graph.getBounds(), initZoomLvl);

        //add all nodes to our root quadtile and let them propegate.
        for (Map.Entry<Long, Node> entry: graph.getNodeMap().entrySet()) {
            rootTile.addNode(entry.getValue());
        }

        //TODO figure out if we want to prerender everything.
    }

    @Override
    public void drawMapView(double x, double y, int gWindowWidth, int gWindowHeight, double zoomFactor, Graphics2D g) {

    }

    @Override
    public void setHighlightedPath(List<Long> nodeList) {

    }
}
