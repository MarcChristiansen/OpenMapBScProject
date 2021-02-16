package openmap.gui;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.Path;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

class MapPanel extends JPanel {

    private int nodeRadius = 5;
    private Graph graph;

    public MapPanel(Graph graph) {
        this.graph = graph;
        setBorder(BorderFactory.createLineBorder(Color.black));

        /*addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                moveSquare(e.getX(),e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveSquare(e.getX(),e.getY());
            }
        });*/

    }

   /* private void moveSquare(int x, int y) {
        int OFFSET = 1;
        if ((squareX!=x) || (squareY!=y)) {
            repaint(squareX,squareY,squareW+OFFSET,squareH+OFFSET);
            squareX=x;
            squareY=y;
            repaint(squareX,squareY,squareW+OFFSET,squareH+OFFSET);
        }
    }*/

    /**
     * Draw a correctly centered circle
     * @param g Graphics object
     * @param x x coordinate
     * @param y y coordinate
     * @param radius The radius to draw
     */
    public static void drawCircle(Graphics g, int x, int y, int radius) {

        int diameter = radius * 2;

        //Center correctly
        g.fillOval(x - radius, y - radius, diameter, diameter);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(250,200);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);

        Random rand = new Random();

        for (Map.Entry<Long, Node> entry : graph.getNodeMap().entrySet())
        {
            Node node = entry.getValue();

            g.setColor(Color.RED);
            int x = (int) ((node.getX() + graph.getBounds().getMaxX()));
            int y = (int) ((node.getY() + graph.getBounds().getMaxY()));
            //System.out.println(graph.getBounds().getMinX());
            //System.out.println(graph.getBounds().getMinY());
            //System.out.println("(x: " + x + ", y: " + y + ")" + "(lat: " + node.getLat() + ", lon: " + node.getLon() + ")");
            drawCircle(g,x , y, nodeRadius);

            g.setColor(Color.BLACK);

            for (Path p : node.getPaths()) {
                g.drawLine((int)(node.getX() - graph.getBounds().getMinX()),
                            (int)(node.getY() - graph.getBounds().getMinY()),
                            (int)(p.getDestination().getX() - graph.getBounds().getMinX()),
                            (int)(p.getDestination().getY() - graph.getBounds().getMinY()));
            }
        }

        System.out.println("");

    }
}
