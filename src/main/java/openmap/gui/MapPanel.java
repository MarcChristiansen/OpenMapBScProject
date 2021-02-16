package openmap.gui;

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
    private double panSpeed = 1;
    private double zoomFactor = 1;
    private Graph graph;
    private double panX;
    private double panY;

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
        panX = 0;
        panY = -(graph.getBounds().getMaxY()-graph.getBounds().getMinY());

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
                    int deltaY = origPoint.y - e.getY();

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

        at.translate(-panX, -panY);

        g.transform(at);
        g.scale(1,-1);
        g.translate(0,getHeight());


        //Rotation stuff

        g.setColor(Color.RED);

        int tempCounter = 0;

        drawCircle(g,10 , 10, getScaledNodeRadius());

        for (Map.Entry<Long, Node> entry : graph.getNodeMap().entrySet())
        {
            tempCounter += 1;

            if(tempCounter == 1000){

            Node node = entry.getValue();

            g.setColor(Color.RED);
            int x = (int) ((node.getX() - graph.getBounds().getMinX()));
            int y = (int) ((node.getY() - graph.getBounds().getMinY()));
            //System.out.println(graph.getBounds().getMinX());
            //System.out.println(graph.getBounds().getMinY());
            //System.out.println("(x: " + x + ", y: " + y + ")" + "(lat: " + node.getLat() + ", lon: " + node.getLon() + ")");
            drawCircle(g,x , y, getScaledNodeRadius());

            /*g.drawLine((int)(0),
                    (int)(0),
                    (int)(x),
                    (int)(y));*/

            //System.out.println(x + " " + y);

            g.setColor(Color.BLACK);
            tempCounter = 0;
            }




            /*
            for (Path p : node.getPaths()) {
                g.drawLine((int)(node.getX() - graph.getBounds().getMinX()),
                            (int)(node.getY() - graph.getBounds().getMinY()),
                            (int)(p.getDestination().getX() - graph.getBounds().getMinX()),
                            (int)(p.getDestination().getY() - graph.getBounds().getMinY()));
            }*/
        }

        System.out.println("(panX: " + panX + ", " + "panY" + panY + ")" + ", ZoomFactor: " + zoomFactor);

        g.setTransform(matrix); // Restore

    }
}
