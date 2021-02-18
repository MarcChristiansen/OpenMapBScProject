package openmap.gui;

import openmap.framework.Graph;
import openmap.utility.XMLUtility;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class MapGui{
    public static void main(String args[]){
        createAndShowGUI();
    }

    public static void addComponentsToPane(Container pane) {

        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

        JButton button = new JButton("Button 1 (PAGE_START)");
        pane.add(button, BorderLayout.PAGE_START);


        Graph graph = null;
        try {
            graph = XMLUtility.loadJsonGraph("mapunop.json");
        }
        catch (Exception e){
            e.printStackTrace();
        }


        //Make the center component big, since that's the map
        JPanel myPanel = new MapPanel(graph);
        myPanel.setPreferredSize(new Dimension(800, 400));

        //Quick test with klemensker //TODO REMOVE
        //((MapPanel)(myPanel)).setHighlightedPath(new ArrayList<Long>(Arrays.asList(1511529408L,795851719L, 1156449172L, 1511479070L)));
        pane.add(myPanel, BorderLayout.CENTER);

        button = new JButton("Button 3 (LINE_START)");
        pane.add(button, BorderLayout.LINE_START);

        button = new JButton("Long-Named Button 4 (PAGE_END)");
        pane.add(button, BorderLayout.PAGE_END);

        button = new JButton("5 (LINE_END)");
        pane.add(button, BorderLayout.LINE_END);
    }

    private static void createAndShowGUI() {

        //Create and set up the window.
        JFrame frame = new JFrame("Map");
        frame.setPreferredSize(new Dimension(1920, 1080));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());

        frame.pack();
        frame.setVisible(true);
    }
}
