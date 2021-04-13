package openmap.gui;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.gui.framework.TileMap;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Map;

/**
 * TileMap implementation using quadTiles.
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 15-03-2021
 */
public class QuadTileMapImpl implements TileMap {

    private List<Node> highlightedNodeList;

    QuadTile rootTile;
    Graph graph;

    public QuadTileMapImpl(Graph graph, byte maxLayer){
        this.graph = graph;


        this.rootTile = new QuadTile(maxLayer, graph.getBounds());

        //set whether or not the tile should check for overlap on added nodes
        rootTile.setCheckOverlap(true);

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
    public void setHighlightedPath(List<Node> nodeList) {
        this.highlightedNodeList = nodeList;
    }

    /**
     * Return the affine transformation used for drawing
     * @param panX The current pan for x
     * @param panY The current pan for y
     * @param zoomFactor The current zoom factor
     * @return A affine transformation that encapsulates the arguments for use when drawing
     */
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
        for (Node currentNode : highlightedNodeList) {

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
