package openmap.gui;

import openmap.framework.Bounds;
import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.Path;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Map;

class MapPanel extends JPanel {

    private final int nodeRadius = 5; //Node radius at default zoom or below
    private final double panSpeed = 1;
    private double zoomFactor = 1;
    private Graph graph;
    private double panX;
    private double panY;

    //Drawing optimization when zooming out
    private final double nodeRatioFactor = 0.1; //Controls when to begin removing nodes/roads depending on zoom. A higher value means sooner removal, lower means later removal
    private final double maxNodesToSkip = 1000; //Controls the max amount of nodes we want to skip when drawing. //TODO make this dynamic in a way that makes sense

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

    public MapPanel(Graph graph) {
        this.graph = graph;
        setBorder(BorderFactory.createLineBorder(Color.black));

        //Set initial graphics location
        panX = graph.getBounds().getMinX();
        panY = graph.getBounds().getMinY()+(graph.getBounds().getMaxY()-graph.getBounds().getMinY());
        //

        zoomFactor = (double)(getPreferredSize().height)/(graph.getBounds().getMaxY() - graph.getBounds().getMinY());
        if(zoomFactor > 1) { zoomFactor = 1; }


        MouseAdapter ma = new MouseAdapter() {
            private Point origPoint;

            @Override
            public void mousePressed(MouseEvent e) {
                origPoint = new Point(e.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (origPoint != null) {
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

    public double getZoomFactor() {
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
        g.fillOval(x - radius, y - radius, diameter, diameter);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1600, 900);
    }

    @Override
    protected void paintComponent(Graphics gg) {
        this.setBorder(null);
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        AffineTransform matrix = g.getTransform(); // Backup

        //Zoom related stuff and Panning stuff
        AffineTransform at = new AffineTransform();
        at.scale(zoomFactor, zoomFactor);

        at.translate(-panX, panY);

        g.transform(at);
        g.scale(1,-1);
        g.translate(0,0);


        //Rotation stuff

        g.setColor(Color.RED);

        int nodeSkipCounter = 0;

        for (Map.Entry<Long, Node> entry : graph.getNodeMap().entrySet())
        {
            Node node = entry.getValue();

            nodeSkipCounter += 1;

            double drawingFactor = nodeRatioFactor/zoomFactor;

            boolean isVisible = panX <= node.getX() && node.getX() <= (panX+(getWidth()/zoomFactor)) &&
                                panY >= node.getY() && node.getY() >= (panY-(getHeight()/zoomFactor));

            boolean shouldDrawNode = isVisible &&
                                     (drawingFactor <= 1 ||
                                     nodeSkipCounter >= drawingFactor * 20 || //TODO REMOVE MAGIC CONSTANT 20
                                     nodeSkipCounter >= maxNodesToSkip) ;

            if(shouldDrawNode){


                g.setColor(Color.RED);
                int x = (int) ((node.getX()));
                int y = (int) ((node.getY()));

                drawCircle(g,x , y, getScaledNodeRadius());
                //System.out.println(graph.getBounds().getMinX());
                //System.out.println(graph.getBounds().getMinY());
                //System.out.println("(x: " + x + ", y: " + y + ")" + "(lat: " + node.getLat() + ", lon: " + node.getLon() + ")");
                //System.out.println(x + " " + y);

                nodeSkipCounter = 0;

                //Road drawing
                if(zoomFactor >= nodeRatioFactor){
                    g.setColor(Color.BLACK);
                    for (Path p : node.getPaths()) {
                        g.drawLine((int)(node.getX()),
                                (int)(node.getY()),
                                (int)(p.getDestination().getX()),
                                (int)(p.getDestination().getY()));
                    }
                }
            }

        }

        System.out.println("(panX: " + panX + ", " + "panY" + panY + ")" + ", ZoomFactor: " + zoomFactor + " height: " + getHeight());

        g.setTransform(matrix); // Restore

    }
}
