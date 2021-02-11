package openmap.standard;

import openmap.framework.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphBuilderImpl implements graphBuilder {

    OsmXmlParser parser;

    public GraphBuilderImpl(OsmXmlParser osmParser) {
        this.parser = osmParser;
    }

    @Override
    public Graph createGraph() {
        List<OsmWay> wayList = parser.parseWays();
        Map<Long, Integer> nodeWayCounter = countNodes(wayList);
        Map<Long, Node> wayNodeMap = parser.parseNodes(nodeWayCounter);
        Bounds bounds = parser.parseBounds();

        //Create the Map that will only contain intersections and endings. Empty at first
        Map<Long, Node> finalNodeMap = new HashMap<Long, Node>();

        wayList.forEach(way -> {
            List<Long> tempList = way.getNodeIdList();

            double pathLength = 0;

            long previousNodeId = -1; //-1 means no node

            for (int i = 0; i < tempList.size(); i++) {
                Long currentNodeId = tempList.get(i);
                //Since we load all nodes in path we do not need default.
                int nodeWays = nodeWayCounter.get(currentNodeId);

                if(i != 0){
                    pathLength += getDistanceBetweenNodes(wayNodeMap.get(previousNodeId), wayNodeMap.get(currentNodeId));
                }

                if (nodeWays > 1 || i == 0 || i == tempList.size()-1){
                    if(previousNodeId != -1){

                        Node currNode = wayNodeMap.get(currentNodeId);
                        Node preNode = wayNodeMap.get(previousNodeId);

                        String oneway = way.getTags().getOrDefault("oneway", "false");
                        String highway = way.getTags().getOrDefault("highway", "false");
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
        });

        return new GraphImpl(finalNodeMap, bounds);
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


}
