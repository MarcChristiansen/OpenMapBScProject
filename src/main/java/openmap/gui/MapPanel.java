package openmap.gui;

import openmap.framework.Graph;
import openmap.framework.LandmarkSelection;
import openmap.framework.Node;
import openmap.framework.PathFinder;
import openmap.gui.framework.TileMap;
import openmap.standard.DijkstraImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The panel containing the actual map
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 017-03-2021
 */
class MapPanel extends JPanel {

    private static JFrame statFrame;
    private final int nodeRadius = 5; //Node radius at default zoom or below
    private final double panSpeed = 1;
    private double zoomFactor = 1;
    private Graph graph;
    private double panX;
    private double panY;

    //Tile map related stuff
    private TileMap tileMap;

    //Temp pathfinding stuff
    private Node pathNode1;
    private Node pathNode2;
    private PathFinder pathFinder;
    private boolean shouldVisualizePathfinder = false;
    private boolean shouldVisualizeLandmark = false;
    private LandmarkSelection landmarkSelector;

    private List<Node> highlightedNodeList;

    //Landmark stuff
    private List<Node> landmarkListTo;
    private List<Node> landmarksUsedTo;
    private List<Node> landmarkListFrom;
    private List<Node> landmarksUsedFrom;
    private int landmarksToUse = 20;



    /** Find the closest node in the graph to a given point.
     * @param x X coordinate
     * @param y y Coordinate
     * @return The closest node
     */
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

    public MapPanel(Graph graph, PathFinder pathFinder) {


        this.graph = graph;
        this.landmarksUsedTo = new ArrayList<>();
        this.landmarksUsedFrom = new ArrayList<>();
        this.tileMap = new QuadTileMapImpl(graph, (byte)6);

        pathNode1 = graph.getNodeMap().get(3365957063l);
        pathNode2 = graph.getNodeMap().get(1516809112l);

        this.pathFinder = pathFinder;

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

                    runPathFinder();

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

    public void toggleShouldVisualizePathfinder(){
        shouldVisualizePathfinder = !shouldVisualizePathfinder;
        repaint();
    }

    public void toggleShouldVisualizeLandmarks() {
        shouldVisualizeLandmark = !shouldVisualizeLandmark;
        repaint();
    }

    /**
     * Attempt to run the pathfinder on the graph if we have both path nodes already.
     */
    private void runPathFinder() {
        if(pathNode1 != null && pathNode2 !=null){
            List<Node> pathIdList = pathFinder.getShortestPath(pathNode1, pathNode2);
            if(pathIdList != null) {
                setHighlightedPath(pathIdList);
                setLandmarksUsedTo(pathFinder.getLandmarksUsedTo(), pathFinder.getLandmarksUsedFrom());
                repaint();
            } else{
                System.out.println("Path does not exist");
            }
        }
    }



    public void setPathFinder(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
        runPathFinder();
    }

    public void setLandmarkSelector(LandmarkSelection landmarkSelection) {
        this.landmarkSelector = landmarkSelection;
        runLandmarkSelector();

    }

    /**
     * Set the number of landmarks to use in the next calculation
     * Note: This this does not recompute landmarks currently used.
     * Getting new landmarkds has to be done through setting a new landmark type or manual use of runLandmarkSelector()
     * @param landmarksToUse
     */
    public void setLandmarksToUse(int landmarksToUse) {
        this.landmarksToUse = landmarksToUse;
    }

    public int getLandmarksToUse(){
        return landmarksToUse;
    }

    public void runLandmarkSelector() {
        landmarkSelector.findLandmarks(landmarksToUse);
        setLandmarks(landmarkSelector.getLandmarksTo(), landmarkSelector.getLandmarksFrom());
        runPathFinder();
    }

    public void setHighlightedPath(List<Node> nodeList){
        this.highlightedNodeList = nodeList;
    }

    public void setLandmarks(List<Node> landmarksTo, List<Node> landmarksFrom){
        this.landmarkListTo = landmarksTo;
        this.landmarkListFrom = landmarksFrom;
        repaint();
    }

    private void setLandmarksUsedTo(List<Integer> landmarksUsedToIndex, List<Integer> landmarksUsedFromIndex) {
        this.landmarksUsedTo.clear();

        if(landmarksUsedToIndex != null && this.landmarkListTo != null){
            for(Integer i : landmarksUsedToIndex){
                this.landmarksUsedTo.add(this.landmarkListTo.get(i));
            }
        }

        this.landmarksUsedFrom.clear();

        if(landmarksUsedFromIndex != null && this.landmarkListFrom != null){
            for(Integer i : landmarksUsedFromIndex){
                this.landmarksUsedFrom.add(this.landmarkListFrom.get(i));
            }
        }
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
        AffineTransform matrix = g.getTransform(); // Backup org transformation

        tileMap.drawMapView(panX, panY, getWidth(), getHeight(), zoomFactor,  g);



        if(shouldVisualizePathfinder){ tileMap.visualizePathFinderNodeUsage(panX, panY, zoomFactor, pathFinder.getVisitedCheckFunction(), g); }

        if(highlightedNodeList != null) { tileMap.drawHighlightedPath(panX, panY, zoomFactor,  g, highlightedNodeList); }

        if(landmarkListTo != null && shouldVisualizeLandmark) { tileMap.drawLandmarks(panX, panY, zoomFactor,  g, landmarkListTo, landmarksUsedTo, landmarkListFrom, landmarksUsedFrom); }

        g.setTransform(matrix); // Restore original transformation

    }

    public void showStatistics() {
        //Create and set up the window.
        JFrame frame = new JFrame("Table");
        JTable j;
        statFrame = frame;
        frame.setPreferredSize(new Dimension(800, 400));

        // Data to be displayed in the JTable

        PathFinderSelectionUtility pfsu = new PathFinderSelectionUtility(graph);
        String[] pathFinderStrings = pfsu.getPathFinderStrings();
        String[][] data = new String[pathFinderStrings.length][8];
        List<Node> path;

        int i = 0;
        for(String s : pathFinderStrings){
            path = pfsu.getPathFinder(s).getShortestPath(pathNode1, pathNode2);
            String[] dataRow = {s,
                                pfsu.getPathFinder(s).getLastExecutionTime()+" ms",
                                ""+pfsu.getPathFinder(s).getNodesVisited(),
                                ""+pfsu.getPathFinder(s).getNodesScanned(),
                                ""+path.size(),
                                (float)(path.size())/(float)(pfsu.getPathFinder(s).getNodesVisited())*100+"%",
                                (float)(pfsu.getPathFinder(s).getNodesVisited())/(float)(pfsu.getPathFinder(pathFinderStrings[0]).getNodesVisited())*100+"%",
                                (float)(pfsu.getPathFinder(s).getNodesScanned())/(float)(pfsu.getPathFinder(pathFinderStrings[0]).getNodesScanned())*100+"%"};
            data[i] = dataRow;
            i++;
        }


        // Column Names
        String[] columnNames = { "Pathfinder", "Execution time", "Nodes Visited", "Nodes scanned", "Nodes in Path", "Efficiency","Visited Compared to Dijkstra", "Scanned Compared to Dijkstra" };

        // Initializing the JTable
        j = new JTable(data, columnNames);
        j.setBounds(30, 40, 300, 300);

        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(j);

        frame.add(sp);

        frame.pack();
        frame.setVisible(true);
    }
}
