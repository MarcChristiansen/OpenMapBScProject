package openmap.gui;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.PathFinder;
import openmap.gui.framework.TileMap;
import openmap.standard.DijkstraImpl;

import javax.swing.*;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MapPanel extends JPanel {

    private final int nodeRadius = 5; //Node radius at default zoom or below
    private final double panSpeed = 1;
    private double zoomFactor = 1;
    private Graph graph;
    private double panX;
    private double panY;

    //Tile map related stuff
    private TileMap tileMap;

    //Temp pathfinding stuff
    Node pathNode1;
    Node pathNode2;
    boolean pathNodesChanged = true;
    PathFinder pathFinder;



    /***
     * Scale the node drawing size depending on the zoom factor
     * @return A scaled node drawing size
     */
    public int getScaledNodeRadius() {
        if(zoomFactor >= 1) {
            return nodeRadius;
        }
        return (int)(nodeRadius/zoomFactor);
    }

    //Find the closest node in the graph to a given point.
    public Node getClosestNode(double x, double y){
        Node best = null;
        double dist = 99999999;

        for (Map.Entry<Long, Node> entry: graph.getNodeMap().entrySet()) {

            double nDist = Math.pow(entry.getValue().getX() - x,2 ) + Math.pow(entry.getValue().getY() - y,2);
            if(best == null || nDist < dist){
                best = entry.getValue();
                dist = nDist;
            }
        }

        return best;
    }

    public MapPanel(Graph graph) {


        this.graph = graph;
        //setBorder(BorderFactory.createLineBorder(Color.black));
        this.tileMap = new QuadTileMapImpl(graph, (byte)6);

        //TODO REMOVE and make modular
        pathFinder = new DijkstraImpl(this.graph);

        //Set initial graphics location
        panX = graph.getBounds().getMinX();
        panY = graph.getBounds().getMinY()+(graph.getBounds().getMaxY()-graph.getBounds().getMinY());

        //Tile map creation


        zoomFactor = Math.min((double)(getPreferredSize().height)/(graph.getBounds().getMaxY() - graph.getBounds().getMinY()),
                (double)(getPreferredSize().width)/(graph.getBounds().getMaxX() - graph.getBounds().getMinX()));
        if(zoomFactor > 1) { zoomFactor = 1; }


        MouseAdapter ma = new MouseAdapter() {
            private Point origPoint;

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    origPoint = new Point(e.getPoint());
                } else
                if(e.getButton() == 5 || e.getButton() == 4){ //5 is first side button, no constant exists

                    if(e.getButton() == 5) {
                        pathNode1 = getClosestNode(e.getX()/zoomFactor + panX, panY - e.getY()/zoomFactor);
                    }else{
                        pathNode2 = getClosestNode(e.getX()/zoomFactor+panX, panY - e.getY()/zoomFactor);
                    }

                    if(pathNode1 != null && pathNode2 !=null){
                        List<Node> pathIdList = pathFinder.getShortestPath(pathNode1, pathNode2);
                        if(pathIdList != null) {
                            setHighlightedPath(pathIdList);
                            repaint();
                        } else{
                            System.out.println("Path does not exist");
                        }

                    }

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    origPoint = null;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (origPoint != null ) {
                    int deltaX = origPoint.x - e.getX();
                    int deltaY = -origPoint.y + e.getY();

                    panX += deltaX*panSpeed/getZoomFactor();
                    panY += deltaY*panSpeed/getZoomFactor();
                    origPoint = e.getPoint();
                    repaint();
                    }
                }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                //Zoom in
                if(e.getWheelRotation()<0){
                    setZoomFactor(1.1*getZoomFactor());
                    repaint();
                }
                //Zoom out
                if(e.getWheelRotation()>0){
                    setZoomFactor(getZoomFactor()/1.1);
                    repaint();
                }

            }
        };

        this.addMouseListener(ma);
        this.addMouseMotionListener(ma);
        addMouseWheelListener(ma);

    }

    public void setHighlightedPath(List<Node> nodeList){
        tileMap.setHighlightedPath(nodeList);
    }

    private double getZoomFactor() {
        return zoomFactor;
    }

    private void setZoomFactor(double factor){
        if(factor<this.zoomFactor){
            this.zoomFactor=this.zoomFactor/1.1;
        }
        else{
            this.zoomFactor=factor;
        }
    }

    /**
     * Draw a correctly centered circle
     * @param g Graphics object
     * @param x x coordinate
     * @param y y coordinate
     * @param radius The radius to draw
     */
    private static void drawCircle(Graphics g, int x, int y, int radius) {

        int diameter = radius * 2;

        //Center correctly
        //g.fillRect(x,y, radius, radius); //TODO REMOVE
        g.fillOval(x - radius, y - radius, diameter, diameter);

    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1600, 900);
    }

    @Override
    protected void paintComponent(Graphics gg) {
        super.paintComponent(gg);

        //this.setBorder(null);

        Graphics2D g = (Graphics2D) gg;

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        gg.setColor ( Color.white );
        gg.fillRect ( 0, 0, getWidth(), getHeight() );
        AffineTransform matrix = g.getTransform(); // Backup

        /*//Zoom related stuff and Panning stuff
        AffineTransform at = new AffineTransform();
        at.scale(zoomFactor, zoomFactor);

        at.translate(-panX, panY);

        g.transform(at);
        g.scale(1, -1);
        g.translate(0, 0);

        BufferedImage img = new BufferedImage((int)(getWidth()), (int)(getHeight()), BufferedImage.TYPE_INT_RGB );
        Graphics2D gb =  img.createGraphics();

        gb.setColor(Color.RED);
        gb.drawLine(-10,-10,0,0);

        gg.drawImage(img, (int)(graph.getBounds().getMinX()), (int)(graph.getBounds().getMinY()+(graph.getBounds().getMaxY()-graph.getBounds().getMinY())), null);

         */



        tileMap.drawMapView(panX, panY, getWidth(), getHeight(), zoomFactor, g);


        //Rotation stuff
        /*

        g.setColor(Color.GREEN);

        int nodeSkipCounter = 0;

        for (Map.Entry<Long, Node> entry : graph.getNodeMap().entrySet()) {
            Node node = entry.getValue();

            nodeSkipCounter += 1;

            double drawingFactor = nodeRatioFactor / zoomFactor;
            boolean isVisible = panX <= node.getX() && node.getX() <= (panX + (getWidth() / zoomFactor)) &&
                    panY >= node.getY() && node.getY() >= (panY - (getHeight() / zoomFactor));

            boolean shouldDrawNode = isVisible &&
                    (drawingFactor <= 1 ||
                            nodeSkipCounter >= drawingFactor * 20 || //TODO REMOVE MAGIC CONSTANT 20
                            nodeSkipCounter >= maxNodesToSkip);

            if (shouldDrawNode) {


                g.setColor(Color.RED);
                int x = (int) ((node.getX()));
                int y = (int) ((node.getY()));

                //drawCircle(g, x, y, getScaledNodeRadius());
                nodeSkipCounter = 0;

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

         */

        //Draw highlighted path


        /*if (highlightedNodeList != null){
                Node lastNode = null;
            for (Long nl : highlightedNodeList) {
                Node currentNode = graph.getNodeMap().get(nl);
                //g.setColor(Color.CYAN);
                //drawCircle(g, (int) currentNode.getX(), (int) currentNode.getY(), getScaledNodeRadius());

                g.setColor(Color.RED);
                g.setStroke(new BasicStroke(6f));
                if (lastNode != null) {
                    g.drawLine((int) (currentNode.getX()),
                            (int) (currentNode.getY()),
                            (int) (lastNode.getX()),
                            (int) (lastNode.getY()));
                }
                lastNode = currentNode;
            }
        }*/

        System.out.println("(panX: " + panX + ", " + "panY" + panY + ")" + ", ZoomFactor: " + zoomFactor + " height: " + getHeight());

        g.setTransform(matrix); // Restore

    }
}
