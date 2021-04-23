package openmap.gui;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.gui.framework.TileMap;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * TileMap implementation using quadTiles.
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 15-03-2021
 */
public class QuadTileMapImpl implements TileMap {

    private List<Node> highlightedNodeList;
    private List<Node> landmarkList;

    private QuadTile rootTile;
    private Graph graph;
    private List<Node> landmarksUsed;

    //Flags
    private boolean shouldDrawLandmarks = false;

    public QuadTileMapImpl(Graph graph, byte maxLayer){
        this.graph = graph;
        this.landmarksUsed = new ArrayList<>();


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
        drawMapView(x,  y,  gWindowWidth,  gWindowHeight, zoomFactor, null, g);
    }


    public void drawMapView(double x, double y, int gWindowWidth, int gWindowHeight, double zoomFactor, Function<Node, NodeDrawingInfo> nodeCond, Graphics2D g) {
        rootTile.drawMapView(x,y, gWindowWidth, gWindowHeight, zoomFactor, g);

        if(nodeCond != null && highlightedNodeList != null){
            visualizePathFinderNodeUsage(x,y, zoomFactor, nodeCond, g);
        }

        drawHighlightedPath(x, y, zoomFactor, g);

        if(shouldDrawLandmarks){
            drawLandmarks(x, y, zoomFactor, g);
        }

    }



    @Override
    public void setHighlightedPath(List<Node> nodeList) {
        this.highlightedNodeList = nodeList;
    }

    @Override
    public void setShouldDrawLandmarks(boolean shouldDrawLandmarks) {
        this.shouldDrawLandmarks = shouldDrawLandmarks;
    }

    @Override
    public void setLandmarks(List<Node> landmarks) {
        this.landmarksUsed.clear(); //We need to clear any previously used landmarks
        this.landmarkList = landmarks;
    }

    @Override
    public void setLandmarksUsed(List<Integer> landmarksUsed) {
        this.landmarksUsed.clear();

        if(landmarksUsed != null && this.landmarkList != null){
            for(Integer i : landmarksUsed){
                this.landmarksUsed.add(this.landmarkList.get(i));
            }
        }
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

    private void drawLandmarks(double panX, double panY, double zoomFactor, Graphics2D g) {
        if (landmarkList == null) { return; }

        AffineTransform oldTransform = g.getTransform();
        AffineTransform at = getMapDrawingAffineTransform(panX, panY, zoomFactor);
        g.transform(at);

        for (Node currentNode : landmarkList) {

            g.setColor(Color.YELLOW);
            g.fillRect((int) (currentNode.getX()-5/zoomFactor),
                    (int) (currentNode.getY()-5/zoomFactor),
                    (int)(10/zoomFactor),
                    (int)(10/zoomFactor));
        }

        for (Node currentNode : landmarksUsed) {

            g.setColor(Color.RED);
            g.fillRect((int) (currentNode.getX()-3/zoomFactor),
                    (int) (currentNode.getY()-3/zoomFactor),
                    (int)(6/zoomFactor),
                    (int)(6/zoomFactor));
        }

        g.setTransform(oldTransform);
    }

    /**
     * Visualize nodes based on predicate. Meant to be used to visualize pathfinders and their visited nodes
     * @param panX Current pan for x
     * @param panY Current pan for y
     * @param zoomFactor Current zoom factor
     * @param nodeCond The predicate
     * @param g The graphics to draw to.
     */
    private void visualizePathFinderNodeUsage(double panX, double panY, double zoomFactor, Function<Node, NodeDrawingInfo> nodeCond , Graphics2D g){
        AffineTransform oldTransform = g.getTransform();
        AffineTransform at = getMapDrawingAffineTransform(panX, panY, zoomFactor);
        g.transform(at);

        for (Node currentNode : graph.getNodeMap().values()) {

            NodeDrawingInfo testInfo = nodeCond.apply(currentNode);



            if (testInfo.shouldDraw()) { //If predicate says ok we draw it...
                g.setColor(testInfo.getColor());

                for (Path p : currentNode.getOutgoingPaths()) {
                    g.drawLine((int) (currentNode.getX()), (int) (currentNode.getY()),
                            (int) (p.getDestination().getX()), (int) (p.getDestination().getY()));
                }
            }
        }
        g.setTransform(oldTransform);
    }
}
