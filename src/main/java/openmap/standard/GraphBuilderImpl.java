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



        //Create the Map that will only contain intersections and endings. Empty at first
        Map<Long, Node> finalNodeMap = new HashMap<Long, Node>();

        wayList.forEach(way -> {
            //check if current highway type is to be part of graph
            String highway = way.getTags().getOrDefault("highway", "false");
            boolean disabledPath = false;
            switch(highway) {
                case "steps":
                case "corridor":
                    //only for walking
                    disabledPath = !footPaths;
                    break;
                case "bridleway":
                    //for horses
                    disabledPath = true;
                    break;
                case "footway":
                    disabledPath = !footPaths;
                    if(way.getTags().getOrDefault("bicycle", "false").equals("yes")){
                        disabledPath = !bikePaths || !footPaths;
                    }
                    break;
                case "path":
                    //path for either bike or foot
                    disabledPath = !bikePaths || !footPaths;
                    break;
                case "cycleway":
                    //bike only paths
                    disabledPath = !bikePaths;
                    break;
                default:
                    break;
            }
            if(!disabledPath){
                List<Long> tempList = way.getNodeIdList();

                double pathLength = 0;

                long previousNodeId = -1; //-1 means no node

                for (int i = 0; i < tempList.size(); i++) {
                    Long currentNodeId = tempList.get(i);
                    //Since we load all nodes in path we do not need default.
                    int nodeWays = nodeWayCounter.get(currentNodeId);

                    //Sum length of all paths between two nodes
                    if(i != 0){
                        pathLength += getDistanceBetweenNodes(wayNodeMap.get(previousNodeId), wayNodeMap.get(currentNodeId));
                    }

                    int maxSpeed = FindMaxSpeed(way);


                    if (nodeWays > 1 || i == 0 || i == tempList.size()-1 || !shouldOptimizeGraph){
                        if(shouldRefitBorders) { ensureBounds(bounds, wayNodeMap.get(currentNodeId));}

                        if(previousNodeId != -1){


                            Node currNode = wayNodeMap.get(currentNodeId);
                            Node preNode = wayNodeMap.get(previousNodeId);

                            String oneway = way.getTags().getOrDefault("oneway", "false");
                            String junction = way.getTags().getOrDefault("junction", "false");

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

        });

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
