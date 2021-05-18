package openmap.gui;

import openmap.framework.Graph;
import openmap.parsing.json.DiskUtility;
import openmap.standard.DijkstraImpl;
import openmap.utility.ConsoleUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * The top level class used for creating a GUI with a map attached.
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 017-03-2021
 */
class MapGui{

    private static JFrame currframe;

    public static void main(String args[]) throws IOException {
        String path = "";

        if(args != null && args.length == 1){
            path = args[0];
        }
        else{
            path = ConsoleUtils.readLine(
                    "Enter json path : ");
        }

        createAndShowGUI(path);
    }

    public static void addComponentsToPane(Container pane, String path) {

        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

        Graph graph = null;
        try { graph = DiskUtility.loadJsonGraph(path); }
        catch (Exception e){ e.printStackTrace(); }

        //Make the center component big, since that's the map
        MapPanel myPanel = new MapPanel(graph, new DijkstraImpl(graph));
        System.out.println(graph.getNodeMap().size());
        pane.add(myPanel, BorderLayout.CENTER);

        //TODO implement this properly with actual working buttons and lists


        JPanel gbl = new JPanel(new GridBagLayout());
        gbl.setBorder(new TitledBorder("current file: \"" + path + "\""));
        pane.add(gbl, BorderLayout.LINE_START);

        JPanel Controls = new JPanel();//new GridLayout(0, 1, 10, 10));
        Controls.setLayout(new BoxLayout(Controls, BoxLayout.Y_AXIS));

        JPanel PathfinderControls = new JPanel(new GridLayout(0, 1, 10, 10));
        PathfinderControls.setBorder(new TitledBorder("Pathfinder controls"));

        Controls.add(PathfinderControls);
        Controls.add(Box.createRigidArea(new Dimension(0, 10)));
        JButton bShowPathfinderPath = new JButton("Show seen paths" );
        bShowPathfinderPath.addActionListener(e -> myPanel.toggleShouldVisualizePathfinder());
        PathfinderControls.add(getPathfinderComboBox(myPanel, graph));
        PathfinderControls.add(bShowPathfinderPath);

        JPanel landmarkControls = new JPanel(new GridLayout(0, 1, 10, 10));
        landmarkControls.setBorder(new TitledBorder("Landmark controls"));

        Controls.add(landmarkControls);
        JButton bShowLandmarks = new JButton("Show landmarks" );
        bShowLandmarks.addActionListener(e -> myPanel.toggleShouldVisualizeLandmarks());

        JButton bRecomputeLandmarks = new JButton("Recompute landmarks" );
        bRecomputeLandmarks.addActionListener(e -> myPanel.runLandmarkSelector());

        landmarkControls.add(getLandmarkComboBox(myPanel, graph));
        landmarkControls.add(bShowLandmarks);
        landmarkControls.add(getLandmarkNumberSpinner(myPanel, graph));
        landmarkControls.add(bRecomputeLandmarks);

        JPanel statControls = new JPanel(new GridLayout(0, 1, 10, 10));
        statControls.setBorder(new TitledBorder("statistics controls"));

        Controls.add(statControls);
        JButton bShowStats = new JButton("Show statistics" );
        bShowStats.addActionListener(e -> myPanel.showStatistics());
        statControls.add(bShowStats);


        gbl.add(Controls);

    }



    public static JComboBox getPathfinderComboBox(MapPanel mapPanel, Graph graph){

        PathFinderSelectionUtility pfsu = new PathFinderSelectionUtility(graph);

        String[] pathFinderStrings = pfsu.getPathFinderStrings();

        //Create the combo box
        JComboBox pathFinderList = new JComboBox(pathFinderStrings);
        pathFinderList.setSelectedIndex(0);
        pathFinderList.setMaximumSize(new Dimension(1920, 30));

        pathFinderList.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String item = (String)(e.getItem());
                mapPanel.setPathFinder(pfsu.getPathFinder(item));
            }
        });


        return pathFinderList;
    }

    public static JComboBox getLandmarkComboBox(MapPanel mapPanel, Graph graph){

        LandmarkSelectionUtility lsu = new LandmarkSelectionUtility(graph);

        String[] landmarkSelectionStrings = lsu.getLandmarkSelectionStrings();

        //Create the combo box
        JComboBox LandmarkList = new JComboBox(landmarkSelectionStrings);
        LandmarkList.setSelectedIndex(0);
        LandmarkList.setMaximumSize(new Dimension(1920, 30));

        mapPanel.setLandmarkSelector(lsu.getLandmarkSelector(lsu.getLandmarkSelectionStrings()[0]));

        LandmarkList.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String item = (String)(e.getItem());
                mapPanel.setLandmarkSelector(lsu.getLandmarkSelector(item));
            }
        });


        return LandmarkList;
    }

    public static JSpinner getLandmarkNumberSpinner(MapPanel mapPanel, Graph graph){
        SpinnerNumberModel numModel = new SpinnerNumberModel(mapPanel.getLandmarksToUse(), 0, graph.getNodeMap().size(), 1);
        JSpinner landmarkSpinner = new JSpinner(numModel);

        numModel.addChangeListener(e -> mapPanel.setLandmarksToUse(numModel.getNumber().intValue()));

        return landmarkSpinner;
    }

    private static void createAndShowGUI(String path) {

        //Create and set up the window.
        JFrame frame = new JFrame("Map");
        currframe = frame;
        frame.setPreferredSize(new Dimension(1920, 1080));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        addComponentsToPane(frame.getContentPane(), path);

        frame.pack();
        frame.setVisible(true);
    }
}
