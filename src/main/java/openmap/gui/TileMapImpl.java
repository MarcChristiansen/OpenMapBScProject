package openmap.gui;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.gui.framework.MapTile;
import openmap.gui.framework.TileMap;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class TileMapImpl implements TileMap {

    //Constants
    private final double nodeRatioFactor = 0.0001; //Controls when to begin removing nodes/roads depending on zoom. A higher value means sooner removal, lower means later removal
    private final double maxNodesToSkip = 1000; //Controls the max amount of nodes we want to skip when drawing. //TODO make this dynamic in a way that makes sense

    private MapTile[][] tileMapArray;
    private int tileSquareSize;
    private Graph graph;

    private List<Long> highlightedNodeList;

    //Buffered render related stuff
    private boolean highlightAlreadyDrawn;
    private int preRenderSize;
    BufferedImage bufferImg;
    double initPanX;
    double initPanY;
    double initZoomLvl;
    int oldWidth;
    int oldHeight;


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
        if(bufferImg == null || width != oldWidth || height != oldHeight || !highlightAlreadyDrawn){
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
            highlightAlreadyDrawn = true; //Set as if map
            bg.dispose();
        }

        //Use buffer image if the current zoom level allows it.
        if(zoomFactor < initZoomLvl){
            System.out.println(zoomFactor + " " + initZoomLvl);
            double tempPanX = (panX-graph.getBounds().getMinX())*zoomFactor;
            double tempPanY = (panY-(graph.getBounds().getMinY()+(graph.getBounds().getMaxY()-graph.getBounds().getMinY())))*zoomFactor;
            System.out.println("tempPan) " + tempPanX + " " + tempPanY);
            g.translate(-tempPanX, tempPanY);
            g.scale(zoomFactor/initZoomLvl, zoomFactor/initZoomLvl);
            g.drawImage(bufferImg, 0, 0, null);

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

        AffineTransform at = new AffineTransform();
        at.scale(zoomFactor, zoomFactor);
        at.translate(-panX, panY);
        g.transform(at);
        g.scale(1, -1);
        g.translate(0, 0);

        int initArrX = (int)Math.floor((panX-graph.getBounds().getMinX())/tileSquareSize);
        int initArrY = ((int)Math.floor((panY-graph.getBounds().getMinY())/tileSquareSize));

        int maxArrX = (int)Math.ceil((panX-graph.getBounds().getMinX() + (width / zoomFactor))/tileSquareSize);
        int maxArrY = ((int)Math.floor(((panY-graph.getBounds().getMinY()) - (height / zoomFactor))/tileSquareSize));

        //Ensure array bounds
        if(initArrX < 0) { initArrX = 0; }
        if(initArrY < 0) { System.out.println("Y outside of image, i think"); return;}
        if(initArrX > tileMapArray.length) { System.out.println("X outside of image, i think"); return; }
        if(initArrY >= tileMapArray[0].length) { initArrY = tileMapArray[0].length-1; }

        if(maxArrX < 0) {System.out.println("X outside of image, i think (max)"); return;}
        if(maxArrY < 0) { maxArrY = 0; }
        if(maxArrX > tileMapArray.length) { maxArrX = tileMapArray.length;}
        if(maxArrY > tileMapArray[0].length) {System.out.println("Y outside of image, i think (max)"); return;}

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
                                for (Path p : node.getPaths()) {
                                    g.drawLine((int) (node.getX()), (int) (node.getY()),
                                            (int) (p.getDestination().getX()), (int) (p.getDestination().getY()));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (highlightedNodeList != null){
            Node lastNode = null;
            for (Long nl : highlightedNodeList) {
                Node currentNode = graph.getNodeMap().get(nl);

                g.setColor(Color.RED);
                if (lastNode != null) {
                    g.setStroke(new BasicStroke((int)Math.max(5/zoomFactor, 5)));
                    g.drawLine((int) (currentNode.getX()),
                            (int) (currentNode.getY()),
                            (int) (lastNode.getX()),
                            (int) (lastNode.getY()));
                }
                lastNode = currentNode;
            }
        }

        /*
        for (Map.Entry<Long, Node> entry : graph.getNodeMap().entrySet()) {
            Node node = entry.getValue();


            double drawingFactor = nodeRatioFactor / zoomFactor;
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
                    for (Path p : node.getPaths()) {
                        g.drawLine((int) (node.getX()), (int) (node.getY()),
                                (int) (p.getDestination().getX()), (int) (p.getDestination().getY()));
                    }
                }
            }

        }*/
    }

    @Override
    public void setHighlightedPath(List<Long> nodeList) {
        this.highlightedNodeList = nodeList;
    }
}
