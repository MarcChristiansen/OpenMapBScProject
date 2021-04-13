package openmap.gui;

import openmap.alternative.AStarImpl;
import openmap.framework.Graph;
import openmap.parsing.json.DiskUtility;
import openmap.framework.PathFinder;
import openmap.standard.DijkstraImpl;
import openmap.standard.DijkstraWrongImpl;
import openmap.utility.ConsoleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

/**
 * The top level class used for creating a GUI with a map attached.
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 017-03-2021
 */
class MapGui{

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

        myPanel.setPreferredSize(new Dimension(800, 400));
        pane.add(myPanel, BorderLayout.CENTER);


        //TODO implement this properly with actual working buttons and lists

        JLabel label = new JLabel("current file \"" + path + "\"");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.add(label, BorderLayout.PAGE_START);


        JPanel leftBox = new JPanel();
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.PAGE_AXIS));
        leftBox.add(getComboBox(myPanel, graph));
        JButton showVisitedBtn = new JButton("Show visited WIP");
        showVisitedBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftBox.add(showVisitedBtn);
        pane.add(leftBox, BorderLayout.LINE_START);
    }



    public static JComboBox getComboBox(MapPanel mapPanel, Graph graph){

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

    private static void createAndShowGUI(String path) {

        //Create and set up the window.
        JFrame frame = new JFrame("Map");
        frame.setPreferredSize(new Dimension(1920, 1080));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        addComponentsToPane(frame.getContentPane(), path);

        frame.pack();
        frame.setVisible(true);
    }
}
