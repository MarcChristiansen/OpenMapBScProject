package openmap.gui;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.gui.framework.TileMap;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Map;

public class QuadTileMapImpl implements TileMap {

    private List<Long> highlightedNodeList;

    QuadTile rootTile;
    Graph graph;

    public QuadTileMapImpl(Graph graph, byte maxLayer){
        this.graph = graph;


        this.rootTile = new QuadTile(maxLayer, graph.getBounds());

        //set whether or not the tile should check for overlap
        rootTile.setCheckOverlap(false);

        //add all nodes to our root quadtile and let them propegate.
        for (Map.Entry<Long, Node> entry: graph.getNodeMap().entrySet()) {
            rootTile.addNode(entry.getValue());
        }
        rootTile.preRenderCacheImages();

    }

    @Override
    public void drawMapView(double x, double y, int gWindowWidth, int gWindowHeight, double zoomFactor, Graphics2D g) {
        rootTile.drawMapView(x,y, gWindowWidth, gWindowHeight, zoomFactor, g);
        drawHighlightedPath(x, y, zoomFactor, g);
    }

    @Override
    public void setHighlightedPath(List<Long> nodeList) {
        this.highlightedNodeList = nodeList;
    }

    private AffineTransform getMapDrawingAffineTransform(double panX, double panY, double zoomFactor) {
        AffineTransform at = new AffineTransform();
        at.scale(zoomFactor, zoomFactor);
        at.translate(-panX, panY);
        at.scale(1,-1);
        return at;
    }

    private void drawHighlightedPath(double panX, double panY, double zoomFactor, Graphics2D g) {
        if (highlightedNodeList == null) { return; }

        AffineTransform oldTransform = g.getTransform();
        Stroke oldStroke = g.getStroke();
        AffineTransform at = getMapDrawingAffineTransform(panX, panY, zoomFactor);
        g.transform(at);
        g.setStroke(new BasicStroke((int)Math.max(5/zoomFactor, 5)));


        Node lastNode = null;
        for (Long nl : highlightedNodeList) {
            Node currentNode = graph.getNodeMap().get(nl);

            g.setColor(Color.RED);
            if (lastNode != null) {

                g.drawLine((int) (currentNode.getX()),
                        (int) (currentNode.getY()),
                        (int) (lastNode.getX()),
                        (int) (lastNode.getY()));
            }
            lastNode = currentNode;
        }

        g.setStroke(oldStroke);
        g.setTransform(oldTransform);
    }
}
