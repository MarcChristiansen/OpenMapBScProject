package openmap.standard;

import openmap.framework.Node;
import openmap.framework.OsmWay;
import openmap.framework.OsmXmlParser;
import openmap.framework.graphBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphBuilderImpl implements graphBuilder {

    OsmXmlParser parser;

    public GraphBuilderImpl(OsmXmlParser osmParser) {
        this.parser = osmParser;
    }

    @Override
    public Map<Long, Node> createGraph() {
        List<OsmWay> wayList = parser.parseWays();
        Map<Long, Integer> nodeWayCounter = countNodes(wayList);
        Map<Long, Node> wayNodeMap = parser.parseNodes(nodeWayCounter);

        //Create the Map that will only contain intersections and endings. Empty at first
        Map<Long, Node> finalNodeMap = new HashMap<Long, Node>();

        wayList.forEach(Way -> {
            List<Long> tempList = Way.getNodeIdList();

            double pathLength = 0;

            long previousNodeId = -1; //-1 means no node

            for (int i = 0; i < tempList.size(); i++) {
                Long currentNodeId = tempList.get(i);
                //Since we load all nodes in path we do not need default.
                int nodeWays = nodeWayCounter.get(currentNodeId);

                if (nodeWays > 1 || i == 0 || i == tempList.size()-1){
                    if(previousNodeId != -1){
                        Node currNode = wayNodeMap.get(currentNodeId);
                        Node preNode = wayNodeMap.get(currentNodeId);

                        //Add paths to both nodes between intersections or ends.
                        currNode.addPath(new StandardPathImpl(preNode, pathLength));
                        preNode.addPath(new StandardPathImpl(currNode, pathLength));

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
                else {
                    pathLength += 1; //Todo make method
                }

            }
        });

        return finalNodeMap;
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
