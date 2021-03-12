package openmap.standard;

import openmap.framework.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphBuilderImpl implements graphBuilder {

    OsmParser parser;

    //Builder flags with defaults
    boolean shouldRefitBorders = true;
    boolean shouldOptimizeGraph = true;
    boolean bikePaths = false;
    boolean footPaths = false;


    public GraphBuilderImpl(OsmParser osmParser) {
        this.parser = osmParser;
    }

    private void ensureBounds(Bounds bounds, Node node){
        if(node.getX() >= bounds.getMaxX()){
            bounds.setMaxX(node.getX());
        }
        if(node.getX() <= bounds.getMinX()){
            bounds.setMinX(node.getX());
        }
        if(node.getY() >= bounds.getMaxY()){
            bounds.setMaxY(node.getY());
        }
        if(node.getY() <= bounds.getMinY()){
            bounds.setMinY(node.getY());
        }
    }

    @Override
    public Graph createGraph() {
        List<OsmWay> wayList = parser.parseWays();
        Map<Long, Integer> nodeWayCounter = countNodes(wayList);
        Map<Long, Node> wayNodeMap = parser.parseNodes(nodeWayCounter);
        Bounds bounds = parser.parseBounds();

        System.out.println(wayList.size());
        System.out.println(nodeWayCounter.size());
        System.out.println(wayNodeMap.size());

        if(!shouldOptimizeGraph) {
            nodeWayCounter = null;
        }

        //Create the Map that will only contain intersections and endings. Empty at first
        Map<Long, Node> finalNodeMap = new HashMap<Long, Node>();

        for (OsmWay way: wayList)  {

            List<Long> tempList = way.getNodeIdList();

            double pathLength = 0;

            long previousNodeId = -1; //-1 means no node

            for (int i = 0; i < tempList.size(); i++) {
                Long currentNodeId = tempList.get(i);
                //Since we load all nodes in path we do not need default.

                //Sum length of all paths between two nodes
                if(i != 0){
                    pathLength += getDistanceBetweenNodes(wayNodeMap.get(previousNodeId), wayNodeMap.get(currentNodeId));
                }

                int maxSpeed = FindMaxSpeed(way);

                if (!shouldOptimizeGraph  || nodeWayCounter.get(currentNodeId) > 1 || i == 0 || i == tempList.size()-1){
                    if(shouldRefitBorders) { ensureBounds(bounds, wayNodeMap.get(currentNodeId));}

                    if(previousNodeId != -1){


                        Node currNode = wayNodeMap.get(currentNodeId);
                        Node preNode = wayNodeMap.get(previousNodeId);

                        String oneway = way.getTags().getOrDefault("oneway", "false");
                        String junction = way.getTags().getOrDefault("junction", "false");
                        String highway = way.getTags().getOrDefault("highway", "false");

                        boolean isOneway = oneway.equals("yes") || //Check if way is oneway or if it is implicit given a highway or roundabout
                                oneway.equals("true") ||
                                oneway.equals("1") ||
                                highway.equals("motorway") ||
                                junction.equals("roundabout");

                        boolean isReverseOneway = oneway.equals("-1") ||
                                oneway.equals("reverse");

                        //Add paths to both nodes between intersections or ends.
                        if(isOneway){
                            preNode.addPath(new StandardPathImpl(currNode, pathLength));
                        }
                        else if(isReverseOneway){
                            currNode.addPath(new StandardPathImpl(preNode, pathLength));
                        }
                        else {
                            currNode.addPath(new StandardPathImpl(preNode, pathLength));
                            preNode.addPath(new StandardPathImpl(currNode, pathLength));
                        }

                        finalNodeMap.put(currentNodeId, currNode);
                        finalNodeMap.put(previousNodeId, preNode);

                        //We move our previous node id
                        previousNodeId = currentNodeId;

                        pathLength = 0;
                    }
                    else{
                        previousNodeId = currentNodeId;
                    }
                }

            }
        }

        return new GraphImpl(finalNodeMap, bounds);
    }

    /**
     * Method for finding the maxspeed on a given way
     * @param way
     * @return
     */
    private int FindMaxSpeed(OsmWay way) {
        //Multiply with speed limit to get time in seconds
        int maxSpeed = 50;
        String highway = way.getTags().getOrDefault("highway", "false");
        String maxSpeedTag = way.getTags().getOrDefault("maxspeed", "false");

        try{
            //System.out.println(maxSpeedTag);
            maxSpeed = Integer.parseInt(maxSpeedTag);
        } catch (NumberFormatException e) {
            switch(highway) {
                case "motorway":
                    maxSpeed = 130;
                    break;
                case "primary":
                case "secondary":
                case "tertiary":
                case "trunk":
                    maxSpeed = 80;
                    break;
                default:
                    break;
            }
        }
        return maxSpeed;
    }

    private double getDistanceBetweenNodes(Node n1, Node n2){
        return Math.sqrt(Math.pow(n1.getX()-n2.getX(), 2) + Math.pow(n1.getY()-n2.getY(), 2));
    }

    /**
     * O(n*m)
     */
    private Map<Long, Integer> countNodes(List<OsmWay> WayList) {
        Map<Long, Integer> nodeWayCounter = new HashMap<Long, Integer>();
        WayList.forEach(Way -> {
            Way.getNodeIdList().forEach(id -> {
                nodeWayCounter.put(id, nodeWayCounter.getOrDefault(id, 0)+1);
            });
        });
        return nodeWayCounter;
    }

    /**
     * Check if borders are refit, default = true
     * @return True or false depending on setting
     */
    public boolean ShouldRefitBorders() {
        return shouldRefitBorders;
    }

    /**
     * Set if borders should be extended to ensure all nodes are in the given bounds
     * @param shouldRefitBorders Boolean true or false
     */
    public void setShouldRefitBorders(boolean shouldRefitBorders) {
        this.shouldRefitBorders = shouldRefitBorders;
    }

    /**
     * Tells if the graph should be optimized
     * @param shouldOptimizeGraph True or false boolean value
     */
    public void setShouldOptimizeGraph(boolean shouldOptimizeGraph) {
        this.shouldOptimizeGraph = shouldOptimizeGraph;
    }

    /**
     * Check flag
     * @return Flag that tells if graph will be optimized.
     */
    public boolean ShouldOptimizeGraph() {
        return shouldOptimizeGraph;
    }

    /**
     * tells if foot paths are allowed in the graph
     * @param footPaths True or false boolean value
     */
    public void setFootPaths(boolean footPaths) {
        this.footPaths = footPaths;
    }

    /**
     * tells if bike paths are allowed in the graph
     * @param bikePaths True or false boolean value
     */
    public void setBikePaths(boolean bikePaths) {
        this.bikePaths = bikePaths;
    }



}
