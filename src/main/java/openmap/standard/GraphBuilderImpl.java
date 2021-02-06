package openmap.standard;

import openmap.framework.OsmWay;
import openmap.framework.graphBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphBuilderImpl implements graphBuilder {

    @Override
    public Map<Long, Integer> CountNodes(List<OsmWay> WayList) {
        Map<Long, Integer> nodeWayCounter = new HashMap<Long, Integer>();
        WayList.forEach(Way -> {
            Way.getNodeIdList().forEach(id -> {
                nodeWayCounter.put(id, nodeWayCounter.getOrDefault(id, 0)+1);
            });
        });
        return null;
    }
}
