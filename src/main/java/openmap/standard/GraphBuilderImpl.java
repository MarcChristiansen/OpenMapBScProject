package openmap.standard;

import openmap.framework.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GraphBuilderImpl implements graphBuilder {

    OsmParser parser;

    //Builder flags with defaults
    boolean shouldRefitBorders = true;
    int OptimizationLevel = 0;


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
        //List<OsmWay> wayList = parser.parseWays();
        Map<Long, Byte> nodeWayCounter = countNodes();
        Map<Long, Node> wayNodeMap;
        if(OptimizationLevel > 1){
            System.out.println("Node way counter size before optimization: " + nodeWayCounter.size());
            nodeWayCounter.values().removeIf(val -> val == 1);
            System.out.println("Node way counter size after optimization: " + nodeWayCounter.size());

            wayNodeMap = parser.parseNodes(nodeWayCounter, 1);
        }
        else{
            wayNodeMap = parser.parseNodes(nodeWayCounter);
        }

        Bounds bounds = parser.parseBounds();

        //System.out.println(wayList.size());
        System.out.println("node way counter size: " + nodeWayCounter.size());
        System.out.println("Number of nodes loaded: " + wayNodeMap.size());

        //Create the Map that will only contain intersections and endings. Empty at first

        Map<Long, Node> finalNodeMap;

        //If the optimization level is 1 we reduce without saving on memory and therefore this is needed.
        if(OptimizationLevel != 1){ finalNodeMap = wayNodeMap; }
        else{ finalNodeMap = new HashMap<Long, Node>(); }

        Consumer<OsmWay> action = (way -> {

            List<Long> tempList = way.getNodeIdList();

            double pathLength = 0;

            long previousNodeId = -1; //-1 means no node

            for (int i = 0; i < tempList.size(); i++) {
                Long currentNodeId = tempList.get(i);

                Node currNode = wayNodeMap.getOrDefault(currentNodeId, null);
                //Sum length of all paths between two nodes. Only check if node actually exists
                if(i != 0 && currNode != null){
                    pathLength += getDistanceBetweenNodes(wayNodeMap.get(previousNodeId), wayNodeMap.get(currentNodeId));
                }

                int maxSpeed = FindMaxSpeed(way);

                if (currNode != null && (OptimizationLevel != 1 || ((ParsingNodeImpl)currNode).getWayCounter() > 1 || i == 0 || i == tempList.size()-1)){
                    if(shouldRefitBorders) { ensureBounds(bounds, wayNodeMap.get(currentNodeId));}

                    if(previousNodeId != -1){

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

                        if(OptimizationLevel == 1) {
                            finalNodeMap.put(currentNodeId, currNode);
                            finalNodeMap.put(previousNodeId, preNode);
                        }

                        //We move our previous node id
                        previousNodeId = currentNodeId;

                        pathLength = 0;
                    }
                    else{
                        previousNodeId = currentNodeId;
                    }
                }

            }
        });

        parser.runWithAllWays(action);

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
     * Count the amount of ways a node interacts with, start and end nodes are counted double
     */
    private Map<Long, Byte> countNodes() {
        Map<Long, Byte> nodeWayCounterTemp = new HashMap<>();

        Consumer<OsmWay> action = (Way -> {
            //Enforce we count one extra for being the first or last element of a way.
            long firstId = Way.getNodeIdList().get(0);
            long finalId = Way.getNodeIdList().get(Way.getNodeIdList().size()-1);
            nodeWayCounterTemp.put(firstId, (byte)(nodeWayCounterTemp.getOrDefault(firstId, ((byte)0))+1));
            nodeWayCounterTemp.put(finalId, (byte)(nodeWayCounterTemp.getOrDefault(finalId, ((byte)0))+1));


            Way.getNodeIdList().forEach(id -> {
                nodeWayCounterTemp.put(id, (byte)(nodeWayCounterTemp.getOrDefault(id, ((byte)0))+1));
            });
        });

        parser.runWithAllWays(action);

        return nodeWayCounterTemp;
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
     * 0 = Mo optimization
     * 1 = Result optimization with no ram optimization (Maintains path length correctly)
     * 2 = Memory optimization (might result in slightly imprecise paths)
     * @param optimizationLevel 0, 1, 2. Anything above 2 is seen as 2
     */
    public void SetOptimizationLevel(int optimizationLevel) {
        this.OptimizationLevel = optimizationLevel;
    }

    /**
     * 0 = No optimization
     * 1 = Result optimization with no ram optimization (Maintains path length correctly)
     * 2 = Memory optimization (might result in slightly imprecise paths)
     * @return Optimization level
     */
    public int GetOptimizationLevel() {
        return OptimizationLevel;
    }

}
