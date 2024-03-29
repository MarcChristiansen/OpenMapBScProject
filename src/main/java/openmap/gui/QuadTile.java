package openmap.gui;

import openmap.framework.Bounds;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.gui.framework.MapTile;
import openmap.standard.BoundsImpl;
import org.w3c.dom.NodeList;

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
    ArrayList<Node> nodeList;
    ArrayList<Node> overlappingNodeList;
    double zoomFactor;
    byte layer;
    byte maxLayer;
    Bounds bounds;
    QuadTile parent;
    int width, height;
    String name;
    boolean checkOverlap = true;

    int drawX, drawY;

    /**
     * Top level QuadTile constructor
     * @param maxLayer The max amount of layers to create
     * @param bounds The bounds of the map
     */
    public QuadTile(byte maxLayer, Bounds bounds){
        name = "";

        int height = 1080;

        setupFields((byte)1, maxLayer, bounds, null, zoomFactor, 0, height, 0,0);

        this.zoomFactor = (double)(height)/(getBounds().getMaxY() - getBounds().getMinY());
        this.width = (int)Math.round((zoomFactor*(getBounds().getMaxX() - getBounds().getMinX())));

    }

    /**
     * Private constructor to create children
     * @param layer The childs layer
     * @param maxLayer The max amount of layers
     * @param bounds The bounds of the child
     * @param parent The parent of the child
     * @param zoomFactor The zoomfactor to render
     */
    private QuadTile(byte layer, byte maxLayer, Bounds bounds, QuadTile parent, double zoomFactor, int width, int height, int drawX, int drawY){
        setupFields(layer, maxLayer, bounds, parent, zoomFactor, width, height, drawX, drawY);
    }

    private QuadTile(byte layer, byte maxLayer, Bounds bounds, QuadTile parent, double zoomFactor, int width, int height, int drawX, int drawY, String name){
        setupFields(layer, maxLayer, bounds, parent, zoomFactor, width, height, drawX, drawY);
        this.name = name;
    }

    private void setupFields(byte layer, byte maxLayer, Bounds bounds, QuadTile parent, double zoomFactor,  int width, int height, int drawX, int drawY) {
        nodeList = new ArrayList<>();
        overlappingNodeList = new ArrayList<>();
        this.layer = layer;
        this.maxLayer = maxLayer;
        this.bounds = bounds;
        this.zoomFactor = zoomFactor;
        this.parent = parent;
        this.width = width;
        this.height =height;
        this.drawX = drawX;
        this.drawY = drawY;


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
            createCacheImage(width, height); //TODO TEMP FIND A WAY TO ENSURE DYNAMIC IMAGE (height/width)
        }
        return cacheImage;
    }

    public void addNode(Node n){
        //add nodes to quad tiles
        addNodeToTile(n);

        //loop through paths and add nodes to tiles if paths pass through
        if(checkOverlap) {
            List<Path> pathList = n.getOutgoingPaths();
            pathList.forEach(path -> {
                Node destNode = path.getDestination();
                double a = (n.getY() - destNode.getY()) / (n.getX() - destNode.getX());
                double b = n.getY() - a * n.getX();
                //Make the matrix of passthrough tiles
                boolean[][] passThrough = pathMatrix(n, destNode, a, b);
                addPath(n, passThrough);
            });
        }
    }

    public void addNodeToTile(Node n){
        nodeList.add(n);
        boolean isLastLayer = maxLayer == layer;
        if(isLastLayer) { return; }

        addNodeToChildren(n);
    }

    private void addNodeToChildren(Node n){
        int tileNum = getNodeTileNum(n, bounds);

        boolean isChildNull = children[tileNum] == null;

        if(isChildNull) {createChild(tileNum); }
        children[tileNum].addNodeToTile(n);
    }

    private boolean[][] pathMatrix(Node origin, Node destination, double a, double b){
        int size = (int)(Math.pow(2, (maxLayer-1)));
        double tileWidth = bottomTileWidth();
        double tileHeight = bottomTileHeight();
        //System.out.println("width: "+tileWidth);
        //System.out.println("height: "+tileHeight);
        boolean[][] mat = new boolean[size][size];

        boolean isLeft = origin.getX() >= destination.getX();
        boolean isAbove = origin.getY() <= destination.getY();

        //find origin tile

        int orig_x = (int)((origin.getX()-bounds.getMinX())/tileWidth);
        int orig_y = (int)((bounds.getMaxY()-origin.getY())/tileHeight);
        int dest_x = (int)((destination.getX()-bounds.getMinX())/tileWidth);
        int dest_y = (int)((bounds.getMaxY()-destination.getY())/tileHeight);

        //set
        if(orig_x > 31){
            //System.out.println("orig x bigger than 31");
            orig_x = 31;
        }
        if(orig_y > 31){
            //System.out.println("orig y bigger than 31");
            orig_y = 31;
        }
        if(dest_x > 31){
            //System.out.println("dest x bigger than 31");
            dest_x = 31;
        }
        if(dest_y > 31){
            //System.out.println("dest y bigger than 31");
            dest_y = 31;
        }

        int curr_x = orig_x;
        int curr_y = orig_y;
        /*
        System.out.println("orig x: " + orig_x);
        System.out.println("orig y: " + orig_y);
        System.out.println("dest x: " + dest_x);
        System.out.println("dest y: " + dest_y);
        System.out.println(curr_x);
        System.out.println(curr_y);
        System.out.println(origin.getX());
        System.out.println(bounds.getMinX());
        System.out.println(tileWidth);
        System.out.println(((origin.getX()-bounds.getMinX())/tileWidth));
         */

        mat[curr_x][curr_y] = true;
        mat[dest_x][dest_y] = true;

        while(curr_x != dest_x || curr_y != dest_y) {
            /*
            System.out.println("orig x: " + orig_x);
            System.out.println("orig y: " + orig_y);
            System.out.println("dest x: " + dest_x);
            System.out.println("dest y: " + dest_y);
            System.out.println(curr_x);
            System.out.println(curr_y);
             */
            //check left and right
            if(isLeft){
                if(curr_x != dest_x){
                    //System.out.println("checking left");
                    double x = bounds.getMinX() + curr_x * tileWidth;
                    double y = a*x+b;
                    boolean yInBounds = bounds.getMaxY() - curr_y * tileHeight >= y && bounds.getMaxY() - (curr_y+1) * tileHeight <= y;
                    if(yInBounds){
                        curr_x = curr_x - 1;
                        mat[curr_x][curr_y] = true;
                    }
                }

            }
            else { //assume right
                if(curr_x != dest_x){
                    double x = bounds.getMinX() + (curr_x+1) * tileWidth;
                    double y = a*x+b;
                    boolean yInBounds = bounds.getMaxY() - curr_y * tileHeight >= y && bounds.getMaxY() - (curr_y+1) * tileHeight <= y;
                    if(yInBounds){
                        curr_x = curr_x + 1;
                        mat[curr_x][curr_y] = true;
                    }
                }
            }

            //check up and down
            if(isAbove){
                if(curr_y != dest_y){
                    double y = bounds.getMaxY() - curr_y * tileHeight;
                    double x = (y-b)/a;
                    //check if it is within y values for current tile
                    boolean xInBounds = bounds.getMinX() + curr_x * tileWidth <= x && bounds.getMinX() + (curr_x+1) * tileWidth >= x;
                    if(xInBounds){
                        curr_y = curr_y - 1;
                        mat[curr_x][curr_y] = true;
                    }
                }
            }
            else { //assume below
                if(curr_y != dest_y){
                    double y = bounds.getMaxY() - (curr_y+1) * tileHeight;
                    double x = (y-b)/a;
                    //check if it is within y values for current tile
                    boolean xInBounds = bounds.getMinX() + curr_x * tileWidth <= x && bounds.getMinX() + (curr_x+1) * tileWidth >= x;
                    if(xInBounds){
                        curr_y = curr_y + 1;
                        mat[curr_x][curr_y] = true;
                    }
                }
            }
        }



        return mat;
    }

    private void addPath(Node origin, boolean[][] passThrough){
        //if origin is in node bounds, don't add to overlap list
        boolean originInBounds =  bounds.getMinX() <= origin.getX() && bounds.getMaxX() >= origin.getX() &&
                bounds.getMinY() <= origin.getY() && bounds.getMaxY() >= origin.getY();
        if(!originInBounds){ //If origin node is not in bounds for this tile, add to overlap list
            overlappingNodeList.add(origin);
        }

        int x=0;
        int y=0;
        int currLayer = maxLayer-1;
        for(char c : name.toCharArray()) {
            currLayer -= 1;
            int i = c - '0'; //get the integer for character c
            int pow = (int)(Math.pow(2, currLayer));
            x = x + i%2 * pow;
            y = y + i/2 * pow;
        }

        boolean[] PassthroughChild = new boolean[4];
        //if last layer check only own position
        if(layer != maxLayer){
            int tileSize = (int)(Math.pow(2, maxLayer-layer));
            for(int j = 0; j<tileSize; j++){
                for(int i = 0; i<tileSize; i++){
                    if(passThrough[x+i][y+j]){
                        if(i<tileSize/2 && j < tileSize/2){
                            PassthroughChild[0] = true;
                        }
                        else if(i>=tileSize/2 && j < tileSize/2){
                            PassthroughChild[1] = true;
                        }
                        else if(i<tileSize/2 && j >= tileSize/2){
                            PassthroughChild[2] = true;
                        }
                        else if(i >= tileSize/2 && j >= tileSize/2){
                            PassthroughChild[3] = true;
                        }
                    }
                }
            }
        }

        for(int tileNum = 0; tileNum < 4; tileNum++){
            if(PassthroughChild[tileNum]){
                boolean isChildNull = children[tileNum] == null;

                if(isChildNull) {createChild(tileNum); }
                children[tileNum].addPath(origin, passThrough);
            }
        }

    }

    private double bottomTileWidth(){
        double tileWidth = bounds.getMaxX() - bounds.getMinX();
        double bottomWidth = tileWidth / (Math.pow(2, maxLayer-1));
        return bottomWidth;
    }

    private double bottomTileHeight(){
        double tileHeight = bounds.getMaxY() - bounds.getMinY();
        double bottomHeight = tileHeight / (Math.pow(2, maxLayer-1));
        return bottomHeight;
    }

    private int getNodeTileNum(Node n, Bounds bounds){
        int tileNum;

        boolean nodeInTile1or3 = n.getX() > (bounds.getMaxX()-(bounds.getMaxX()-bounds.getMinX())/2);
        boolean nodeInTile0or1 = n.getY() > (bounds.getMaxY()-(bounds.getMaxY()-bounds.getMinY())/2);

        if(nodeInTile1or3){ //check if we are in tile (2, 4) or (1,3)
            if(nodeInTile0or1) {
                tileNum = 1;
            }
            else{
                tileNum = 3;
            }
        }else {
            if(nodeInTile0or1){
                tileNum = 0;
            }
            else{
                tileNum = 2;
            }
        }

        return tileNum;
    }

    private void createChild(int tileNum) {
        Bounds childBounds = new BoundsImpl();
        int newDrawX = this.drawX*2;
        int newDrawY = this.drawY*2;
        //Setup x bound
        if(tileNum == 0 || tileNum == 2) {
            childBounds.setMinX(bounds.getMinX());
            childBounds.setMaxX(bounds.getMaxX()-(bounds.getMaxX()-bounds.getMinX())/2);
        }
        else{
            childBounds.setMinX(bounds.getMaxX()-(bounds.getMaxX()-bounds.getMinX())/2);
            childBounds.setMaxX(bounds.getMaxX());
            newDrawX = newDrawX + width;
        }

        //Setup y bound
        if(tileNum == 2 || tileNum == 3) {
            childBounds.setMinY(bounds.getMinY());
            childBounds.setMaxY(bounds.getMaxY()-(bounds.getMaxY()-bounds.getMinY())/2);
            newDrawY = newDrawY + height;
        }
        else{
            childBounds.setMinY(bounds.getMaxY()-(bounds.getMaxY()-bounds.getMinY())/2);
            childBounds.setMaxY(bounds.getMaxY());

        }

        children[tileNum] = new QuadTile((byte)(layer+1), maxLayer, childBounds, this.parent, this.zoomFactor*2, width, height, newDrawX, newDrawY, name+String.valueOf(tileNum));
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

    public void preRenderCacheImages(){
        createCacheImage(width, height);

        for (QuadTile qt: children) { //No need to check layer as all children are null at max layer...
            if(qt != null){
                qt.preRenderCacheImages();
            }
        }
    }

    private void createCacheImage(int width, int height){
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g =  img.createGraphics();
        g.setPaint ( Color.WHITE );
        g.fillRect ( 0, 0, width, height );

        AffineTransform oldTransform = g.getTransform();
        System.out.println("layer: " + layer + " zoom " + zoomFactor + " tile " + name);
        AffineTransform at = getMapDrawingAffineTransform(bounds.getMinX(), bounds.getMinY()+(bounds.getMaxY()-bounds.getMinY()), zoomFactor);
        g.transform(at);


        //Road drawing
        drawNodes(g);

        g.dispose();
        cacheImage = img;
    }

    private void drawDirect(double panX, double panY, double zoomFactor, Graphics2D g){
        AffineTransform oldTransform = g.getTransform();
        System.out.println("Directly drawing" + " zoom " + zoomFactor);
        AffineTransform at = getMapDrawingAffineTransform((int)panX, (int)panY, zoomFactor);
        g.setTransform(new AffineTransform(oldTransform));
        g.transform(at);

        drawNodes(g);

        g.setTransform(oldTransform);
    }

    private void drawNodes(Graphics2D g) {
        for (Node node : nodeList) {
            g.setColor(Color.BLACK);
            for (Path p : node.getOutgoingPaths()) {
                g.drawLine((int) (node.getX()), (int) (node.getY()),
                        (int) (p.getDestination().getX()), (int) (p.getDestination().getY()));
            }
        }

        for (Node node : overlappingNodeList) {
            g.setColor(Color.BLACK);
            for (Path p : node.getOutgoingPaths()) {
                g.drawLine((int) (node.getX()), (int) (node.getY()),
                        (int) (p.getDestination().getX()), (int) (p.getDestination().getY()));
            }
        }
    }

    public void drawMapView(double panX, double panY, int gWindowWidth, int gWindowHeight, double zoomFactorInput, Graphics2D g){
        drawMapViewInternal(panX,  panY,  gWindowWidth,  gWindowHeight,  zoomFactorInput,  g, null);
    }

    private void drawMapViewInternal(double panX, double panY, int gWindowWidth, int gWindowHeight, double zoomFactorInput, Graphics2D g, AffineTransform orgTransform){
        boolean rectangleIntersectXCheck = panX > bounds.getMaxX() || bounds.getMinX() > panX+(gWindowWidth/zoomFactorInput);
        boolean rectangleIntersectYCheck = panY < bounds.getMinY() || bounds.getMaxY() < panY-(gWindowHeight/zoomFactorInput);

        boolean isTileVisible = !(rectangleIntersectXCheck || rectangleIntersectYCheck);
        if(!isTileVisible) {
            return;
        }

        AffineTransform oldGTransform = orgTransform; //Roottile will set something other than null

        if(layer == 1){ //Setup transform if rootTile
            oldGTransform = g.getTransform();
            double drawZoomFactor = this.zoomFactor;
            int tempLayer = 1;
            while(tempLayer < maxLayer && zoomFactorInput >= drawZoomFactor){
                drawZoomFactor *= 2;
                tempLayer += 1;
            }

            //Setup rendering hint for graphics interpolation
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            AffineTransform at = new AffineTransform();
            double tempPanX = (panX-getBounds().getMinX())*zoomFactorInput;
            double tempPanY = (panY-(getBounds().getMinY()+(getBounds().getMaxY()-getBounds().getMinY())))*zoomFactorInput;
            at.translate(-tempPanX, tempPanY);
            at.scale(zoomFactorInput/drawZoomFactor, zoomFactorInput/drawZoomFactor);
            g.setTransform(new AffineTransform(oldGTransform));
            g.transform(at);
        }

        if(zoomFactorInput <= this.zoomFactor) {
            g.drawImage(getCacheImage(), drawX, drawY, null); //Only relevant for the rootTile
        }else{
            if(maxLayer == this.layer) {
                //System.out.println("Lowest level reaches, drawing directly");//We have reached the lowest level, we will begin directly drawing
                //Before we do a direct draw we want to go back to the original transform
                AffineTransform temp = g.getTransform();
                g.setTransform(new AffineTransform(oldGTransform));
                drawDirect(panX, panY, zoomFactorInput, g);
                g.setTransform(temp);
                g.setTransform(temp);

            }
            else{
                if(children[0] != null ){
                    children[0].drawMapViewInternal(panX, panY, gWindowWidth, gWindowHeight, zoomFactorInput, g, oldGTransform);
                }
                if(children[1] != null){
                    children[1].drawMapViewInternal(panX, panY, gWindowWidth, gWindowHeight, zoomFactorInput, g, oldGTransform);
                }
                if(children[2] != null){
                    children[2].drawMapViewInternal(panX, panY, gWindowWidth, gWindowHeight, zoomFactorInput, g, oldGTransform);
                }
                if(children[3] != null){
                    children[3].drawMapViewInternal(panX, panY, gWindowWidth, gWindowHeight, zoomFactorInput, g, oldGTransform);
                }
            }
        }

        if(layer == 1){
            g.setTransform(oldGTransform); //We ensure this is set only in layer 1
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setCheckOverlap(boolean flag){
        checkOverlap = flag;
    }

    public void trimListsToSize(){
        nodeList.trimToSize();
        overlappingNodeList.trimToSize();

        if(children[0] != null ){
            children[0].trimListsToSize();
        }
        if(children[1] != null){
            children[1].trimListsToSize();
        }
        if(children[2] != null){
            children[2].trimListsToSize();
        }
        if(children[3] != null){
            children[3].trimListsToSize();
        }
    }
}
