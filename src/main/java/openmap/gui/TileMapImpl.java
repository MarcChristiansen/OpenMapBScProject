package openmap.gui;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.gui.framework.MapTile;
import openmap.gui.framework.TileMap;
import org.apache.commons.lang3.NotImplementedException;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Somewhat legacy - replaced with quadTiles.
 * Simple tile map implementation with a single layer and an array
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 17-03-2021
 */
public class TileMapImpl implements TileMap {

    //Constants
    private final double nodeRatioFactor = 0.0001; //Controls when to begin removing nodes/roads depending on zoom. A higher value means sooner removal, lower means later removal
    private final double maxNodesToSkip = 1000; //Controls the max amount of nodes we want to skip when drawing. //TODO make this dynamic in a way that makes sense

    private MapTile[][] tileMapArray;
    private int tileSquareSize;
    private Graph graph;

    //Buffered render related stuff
    private int preRenderSize;
    BufferedImage bufferImg;
    double initPanX;
    double initPanY;
    double initZoomLvl;
    int oldWidth;
    int oldHeight;

    //Usefull stuff for drawing
    int drawLocationX;
    int getDrawLocationY;


    public TileMapImpl(Graph graph, int tileSquareSize, int preRenderSize){
        this.tileSquareSize = tileSquareSize;
        this.graph = graph;
        this.preRenderSize = preRenderSize;
        double xSize = graph.getBounds().getMaxX()-graph.getBounds().getMinX();
        double ySize = graph.getBounds().getMaxY()-graph.getBounds().getMinY();

        double tileSize = tileSquareSize; //We do this to prevent int conversion errors

        //Ceil to ensure we have enough
        int arrayX = (int)Math.ceil(xSize/((double)tileSquareSize));
        int arrayY = (int)Math.ceil(ySize/((double)tileSquareSize));

        tileMapArray = new MapTile[arrayX][arrayY];

        for (Map.Entry<Long, Node> entry : graph.getNodeMap().entrySet()) {
            Node n = entry.getValue();
            double nX = n.getX() - graph.getBounds().getMinX();
            double nY = n.getY() - graph.getBounds().getMinY();

            int nArrX = (int)Math.floor(nX / tileSquareSize);
            int nArrY = (int)Math.floor(nY / tileSquareSize);

            if(tileMapArray[nArrX][nArrY] == null){ tileMapArray[nArrX][nArrY] = new MapTileImpl(); }
            tileMapArray[nArrX][nArrY].addNode(n);
        }
    }

    @Override
    public void drawMapView(double panX, double panY, int width, int height, double zoomFactor, Graphics2D g) {
        //if we do not have a buffer, or our window changed, create a buffer image that we can reuse.
        if(bufferImg == null || width != oldWidth || height != oldHeight){
            oldWidth = width;
            oldHeight = height;
            initPanX = graph.getBounds().getMinX();
            initPanY = graph.getBounds().getMinY()+(graph.getBounds().getMaxY()-graph.getBounds().getMinY());
            initZoomLvl = Math.min((double)(height*preRenderSize)/(graph.getBounds().getMaxY() - graph.getBounds().getMinY()),
                                    (double)(width*preRenderSize)/(graph.getBounds().getMaxX() - graph.getBounds().getMinX()));

            bufferImg = new BufferedImage(width*preRenderSize, height*preRenderSize, BufferedImage.TYPE_INT_RGB );
            Graphics2D bg = bufferImg.createGraphics();
            bg.setPaint ( Color.white );
            bg.fillRect ( 0, 0, width*preRenderSize, height*preRenderSize );
            drawRelevantTiles(initPanX, initPanY, width*preRenderSize, height*preRenderSize, initZoomLvl, bg);
            bg.dispose();
        }

        //Use buffer image if the current zoom level allows it.
        if(zoomFactor < initZoomLvl){
            AffineTransform oldTransform = g.getTransform();
            double tempPanX = (panX-graph.getBounds().getMinX())*zoomFactor;
            double tempPanY = (panY-(graph.getBounds().getMinY()+(graph.getBounds().getMaxY()-graph.getBounds().getMinY())))*zoomFactor;
            g.translate(-tempPanX, tempPanY);
            g.scale(zoomFactor/initZoomLvl, zoomFactor/initZoomLvl);
            g.drawImage(bufferImg, 0, 0, null);
            g.setTransform(oldTransform);

            return;
        }

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB );
        Graphics2D gg =  img.createGraphics();

        gg.setPaint ( Color.white );
        gg.fillRect ( 0, 0, width, height );
        drawRelevantTiles(panX, panY, width, height, zoomFactor, gg);
        g.drawImage(img, 0, 0, null);


    }

    private void drawRelevantTiles(double panX, double panY, int width, int height, double zoomFactor, Graphics2D g) {
        AffineTransform oldTransform = g.getTransform();

        AffineTransform at = getMapDrawingAffineTransform(panX, panY, zoomFactor);
        g.transform(at);

        int initArrX = (int)Math.floor((panX-graph.getBounds().getMinX())/tileSquareSize);
        int initArrY = ((int)Math.floor((panY-graph.getBounds().getMinY())/tileSquareSize));

        int maxArrX = (int)Math.ceil((panX-graph.getBounds().getMinX() + (width / zoomFactor))/tileSquareSize);
        int maxArrY = ((int)Math.floor(((panY-graph.getBounds().getMinY()) - (height / zoomFactor))/tileSquareSize));

        //Ensure array bounds
        if(initArrX < 0) { initArrX = 0; }
        if(initArrY < 0) { System.out.println("Y outside of image"); return;}
        if(initArrX > tileMapArray.length) { System.out.println("X outside of image"); return; }
        if(initArrY >= tileMapArray[0].length) { initArrY = tileMapArray[0].length-1; }

        if(maxArrX < 0) {System.out.println("X outside of image (max)"); return;}
        if(maxArrY < 0) { maxArrY = 0; }
        if(maxArrX > tileMapArray.length) { maxArrX = tileMapArray.length;}
        if(maxArrY > tileMapArray[0].length) {System.out.println("Y outside of image (max)"); return;}

        //System.out.println(initArrX);
        //System.out.println(initArrY);
        //System.out.println(maxArrX);
        //System.out.println(maxArrY);

        for(int currX = initArrX; currX < maxArrX; currX++){
            for(int currY = initArrY; currY >= maxArrY; currY--){
                if(tileMapArray[currX][currY] != null){
                    for(Node node : tileMapArray[currX][currY].getNodeList()){

                        //double drawingFactor = nodeRatioFactor / zoomFactor;
                        boolean isVisible = panX <= node.getX() && node.getX() <= (panX + (width / zoomFactor)) &&
                                panY >= node.getY() && node.getY() >= (panY - (height / zoomFactor));

                        boolean shouldDrawNode = isVisible; // &&
                        //(drawingFactor <= 1 ||
                        //nodeSkipCounter >= drawingFactor * 20 || //TODO REMOVE MAGIC CONSTANT 20
                        // nodeSkipCounter >= maxNodesToSkip);

                        if (shouldDrawNode) {
                            //Road drawing
                            if (zoomFactor >= nodeRatioFactor) {
                                g.setColor(Color.BLACK);
                                for (Path p : node.getOutgoingPaths()) {
                                    g.drawLine((int) (node.getX()), (int) (node.getY()),
                                            (int) (p.getDestination().getX()), (int) (p.getDestination().getY()));
                                }
                            }
                        }
                    }
                }
            }
        }

        g.setTransform(oldTransform);



    }

    private AffineTransform getMapDrawingAffineTransform(double panX, double panY, double zoomFactor) {
        AffineTransform at = new AffineTransform();
        at.scale(zoomFactor, zoomFactor);
        at.translate(-panX, panY);
        at.scale(1,-1);
        return at;
    }

    @Override
    public void drawHighlightedPath(double panX, double panY, double zoomFactor, Graphics2D g, List<Node> highlightedNodeList) {
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

    @Override
    public void drawLandmarks(double panX, double panY, double zoomFactor, Graphics2D g, List<Node> landmarkListTo, List<Node> landmarksUsedTo, List<Node> landmarkListFrom, List<Node> landmarksUsedFrom) {
        throw new NotImplementedException("No landmarks yet in old map impl");
    }

    @Override
    public void visualizePathFinderNodeUsage(double panX, double panY, double zoomFactor, Function<Node, NodeDrawingInfo> nodeCond, Graphics2D g) {
        throw new NotImplementedException("Pathfinder node usage not implemented yet");
    }


}
