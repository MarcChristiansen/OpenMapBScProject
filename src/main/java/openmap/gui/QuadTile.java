package openmap.gui;

import openmap.framework.Bounds;
import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.gui.framework.MapTile;
import openmap.standard.BoundsImpl;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

/**
 * Class that represents QuadTiles.
 * Used to create sort of a quadtree to enable faster drawing of a map with cached images
 *
 * The children are numbered the following way.
 * |0|1|
 * |2|3|
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 26-02-2021
 */
public class QuadTile implements MapTile {
    QuadTile[] children = new QuadTile[4]; //Quadtile has 4 children, given the name this fits...
    Image cacheImage;
    List<Node> nodeList;
    List<Node> overlappingNodeList;
    double zoomFactor;
    byte layer;
    byte maxLayer;
    Bounds bounds;
    QuadTile parent;

    /**
     * Top level QuadTile constructor
     * @param maxLayer The max amount of layers to create
     * @param bounds The bounds of the map
     * @param bounds The initial zoomFactor
     */
    public QuadTile(byte maxLayer, Bounds bounds, double zoomFactor){
        setupFields((byte)1, maxLayer, bounds, null, zoomFactor);
    }

    /**
     * Private constructor to create children
     * @param layer The childs layer
     * @param maxLayer The max amount of layers
     * @param bounds The bounds of the child
     * @param parent The parent of the child
     * @param zoomFactor The zoomfactor to render
     */
    private QuadTile(byte layer, byte maxLayer, Bounds bounds, QuadTile parent, double zoomFactor){
        setupFields(layer, maxLayer, bounds, parent, zoomFactor);
    }

    private void setupFields(byte layer, byte maxLayer, Bounds bounds, QuadTile parent, double zoomFactor) {
        nodeList = new ArrayList<>();
        this.layer = layer;
        this.maxLayer = maxLayer;
        this.bounds = bounds;
        this.zoomFactor = zoomFactor;
        this.parent = parent;
    }

    /**
     * Get a specific QuadTile child
     * The children are numbered the following way.
     * |0|1|
     * |2|3|
     * @param num get specific tile 0-3
     * @return The specific tile
     */
    public QuadTile getChild(int num)
    {
        return children[num];
    }

    public Image getCacheImage() {
        if(cacheImage == null){
            //TODO Create image based on overlappingNodes and nodeList
        }
        return cacheImage;
    }

    public void addNode(Node n){
        nodeList.add(n);
        boolean isLastLayer = maxLayer == layer;
        if(isLastLayer) { return; }

        addNodeToChildren(n);

        //TODO create overlapping lists
    }

    private void addNodeToChildren(Node n){
        int tileNum;


        boolean nodeInTile1or3 = n.getX() > (bounds.getMaxX()-(bounds.getMaxX()-bounds.getMinX())/2);
        boolean nodeInTile0or1 = n.getY() > (bounds.getMaxY()-(bounds.getMaxY()-bounds.getMinY())/2);
        System.out.println(n.getY());
        System.out.println((bounds.getMaxY()-(bounds.getMaxY()-bounds.getMinY())/2));

        if(nodeInTile1or3){ //check if we are in tile (2, 4) or (1,3)
            if(nodeInTile0or1) {
                System.out.println("hej1");
                tileNum = 1;
            }
            else{
                tileNum = 3;
                System.out.println("hej3");
            }
        }else {
            if(nodeInTile0or1){
                System.out.println("hej0");
                tileNum = 0;
            }
            else{
                System.out.println("hej2");
                tileNum = 2;
            }
        }

        boolean isChildNull = children[tileNum] == null;

        if(isChildNull) {createChild(tileNum); }
        children[tileNum].addNode(n);
    }

    private void createChild(int tileNum) {
        Bounds childBounds = new BoundsImpl();
        //Setup x bound
        if(tileNum == 0 || tileNum == 2) {
            childBounds.setMinX(bounds.getMinX());
            childBounds.setMaxX(bounds.getMaxX()-(bounds.getMaxX()-bounds.getMinX())/2);
        }
        else{
            childBounds.setMinX(bounds.getMaxX()-(bounds.getMaxX()-bounds.getMinX())/2);
            childBounds.setMaxX(bounds.getMaxX());
        }

        //Setup y bound
        if(tileNum == 2 || tileNum == 3) {
            childBounds.setMinY(bounds.getMinY());
            childBounds.setMaxY(bounds.getMaxY()-(bounds.getMaxY()-bounds.getMinY())/2);
        }
        else{
            childBounds.setMinY(bounds.getMaxY()-(bounds.getMaxY()-bounds.getMinY())/2);
            childBounds.setMaxY(bounds.getMaxY());
        }

        children[tileNum] = new QuadTile((byte)(layer+1), maxLayer, childBounds, this.parent, zoomFactor/4);
    }

    public void addOverlappingNode(Node n){
        //TODO create nodeSetup where we handle nodes that have overlapping paths
    }

    private AffineTransform getMapDrawingAffineTransform(double panX, double panY, double zoomFactor) {
        AffineTransform at = new AffineTransform();
        at.scale(zoomFactor, zoomFactor);
        at.translate(-panX, panY);
        at.scale(1,-1);
        return at;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    private Image createCacheImage(int width, int height){
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB );
        Graphics2D g =  img.createGraphics();
        g.setPaint ( Color.white );
        g.fillRect ( 0, 0, width, height );

        AffineTransform oldTransform = g.getTransform();
        AffineTransform at = getMapDrawingAffineTransform(panX, panY, zoomFactor);
        g.transform(at);



        g.dispose();
        return img;
    }

    public Image getMapView(double panX, double panY, int gWindowWidth, int gWindowHeight, double zoomFactor){
        if(zoomFactor >= this.zoomFactor){
            return getCacheImage(); //Only relevant for the rootTile
        }

        if(zoomFactor >= this.zoomFactor/4){
            //Todo let this combine it's children
        }
        else{
            //Todo Find relevant child(ren) and draw their representations
        }
    }

}
