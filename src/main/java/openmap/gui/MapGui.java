package openmap.gui;

import openmap.framework.Graph;
import openmap.parsing.json.DiskUtility;
import openmap.framework.PathFinder;
import openmap.standard.DijkstraImpl;
import openmap.utility.ConsoleUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

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
        try {
            graph = DiskUtility.loadJsonGraph(path);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //Make the center component big, since that's the map
        JPanel myPanel = new MapPanel(graph);
        System.out.println(graph.getNodeMap().size());

        myPanel.setPreferredSize(new Dimension(800, 400));
        pane.add(myPanel, BorderLayout.CENTER);


        //((MapPanel)myPanel).setHighlightedPath(djikstra.getShortestPath(1511529408L, 1511479070L));

        //TODO implement this properly with actual working buttons and lists
        PathFinder djikstra = new DijkstraImpl(graph);

        JButton button = new JButton("Button 1 (PAGE_START)");
        pane.add(button, BorderLayout.PAGE_START);

        button = new JButton("Button 3 (LINE_START)");
        pane.add(button, BorderLayout.LINE_START);

        //button = new JButton("Long-Named Button 4 (PAGE_END)");
        //pane.add(button, BorderLayout.PAGE_END);

        //button = new JButton("5 (LINE_END)");
        //pane.add(button, BorderLayout.LINE_END);
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
