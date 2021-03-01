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


        this.rootTile = new QuadTile(maxLayer, graph.getBounds());

        //add all nodes to our root quadtile and let them propegate.
        for (Map.Entry<Long, Node> entry: graph.getNodeMap().entrySet()) {
            rootTile.addNode(entry.getValue());
        }
        rootTile.preRenderCacheImages();

        //TODO figure out if we want to prerender everything.
    }

    @Override
    public void drawMapView(double x, double y, int gWindowWidth, int gWindowHeight, double zoomFactor, Graphics2D g) {
        rootTile.drawMapView(x,y, gWindowWidth, gWindowHeight, zoomFactor, g);
    }

    @Override
    public void setHighlightedPath(List<Long> nodeList) {

    }
}
