package openmap.benchmarks;

import openmap.framework.Bounds;
import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.gui.LandmarkSelectionUtility;
import openmap.parsing.json.DiskUtility;
import openmap.utility.LatexUtility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuadTileOverlapBenchmark {

    public static void main(String[] args) throws IOException {


        List<List<String>> data = new ArrayList<>();
        List<String> header = new ArrayList<>();
        List<String> row = new ArrayList<>();

        header.add("Kort");
        row.add("overlap count");


        for (int i = 0; i < args.length; i++) {
            header.add(args[i]);
            row.add(BenchmarkSpecificMap(args[i]));
        }

        data.add(header);
        data.add(row);

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("BenchmarkQuadtileOverlap.txt"), "utf-8"))) {
            writer.write(LatexUtility.generateStandardTable(data));
        }
    }

    private static String BenchmarkSpecificMap(String filepath) {
        int overlapCounter = 0;

        Graph graph = null;
        try { graph = DiskUtility.loadJsonGraph(filepath); }
        catch (Exception e){ e.printStackTrace(); }

        for (Map.Entry<Long, Node> entry: graph.getNodeMap().entrySet()) {
            List<Path> pathList = entry.getValue().getOutgoingPaths();
            for(Path p : pathList){
                Node destNode = p.getDestination();
                //Make the matrix of passthrough tiles
                if(checkOverlap(entry.getValue(), destNode, graph.getBounds(), 6)){
                    overlapCounter++;
                };
            }
        }

        return overlapCounter+"";
    }

    private static boolean checkOverlap(Node origin, Node destination, Bounds bounds, int maxLayer){
        boolean overlap = false;
        int size = (int)(Math.pow(2, (maxLayer-1)));
        double tileWidth = bottomTileWidth(bounds, maxLayer);
        double tileHeight = bottomTileHeight(bounds, maxLayer);

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

        if(orig_x != dest_x || orig_y != dest_y){
            overlap = true;
        }

        /*
        while(curr_x != dest_x && curr_y != dest_y) {
            //check left and right
            if(isLeft){
                //System.out.println("checking left");
                double x = bounds.getMinX() + curr_x * tileWidth;
                double y = a*x+b;
                boolean yInBounds = bounds.getMaxY() - curr_y * tileHeight >= y && bounds.getMaxY() - (curr_y+1) * tileHeight <= y;
                if(yInBounds){
                    curr_x = curr_x - 1;
                    mat[curr_x][curr_y] = true;
                }
            }
            else { //assume right
                double x = bounds.getMinX() + (curr_x+1) * tileWidth;
                double y = a*x+b;
                boolean yInBounds = bounds.getMaxY() - curr_y * tileHeight >= y && bounds.getMaxY() - (curr_y+1) * tileHeight <= y;
                if(yInBounds){
                    curr_x = curr_x + 1;
                    mat[curr_x][curr_y] = true;
                }
            }

            //check up and down
            if(isAbove){
                double y = bounds.getMaxY() - curr_y * tileHeight;
                double x = (y-b)/a;
                //check if it is within y values for current tile
                boolean xInBounds = bounds.getMinX() + curr_x * tileWidth <= x && bounds.getMinX() + (curr_x+1) * tileWidth >= x;
                if(xInBounds){
                    curr_y = curr_y - 1;
                    mat[curr_x][curr_y] = true;
                }
            }
            else { //assume below
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
        */


        return overlap;
    }

    private static double bottomTileWidth(Bounds bounds, int maxLayer){
        double tileWidth = bounds.getMaxX() - bounds.getMinX();
        double bottomWidth = tileWidth / (Math.pow(2, maxLayer-1));
        return bottomWidth;
    }

    private static double bottomTileHeight(Bounds bounds, int maxLayer){
        double tileHeight = bounds.getMaxY() - bounds.getMinY();
        double bottomHeight = tileHeight / (Math.pow(2, maxLayer-1));
        return bottomHeight;
    }
}
