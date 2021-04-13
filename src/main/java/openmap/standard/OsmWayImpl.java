package openmap.standard;

import openmap.framework.OsmWay;

import java.util.List;
import java.util.Map;

/**
 * Intermediate class to represent raw Osm ways
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 9-02-2021
 */
public class OsmWayImpl implements OsmWay {

    List<Long> ids;
    Map<String, String> TagsMap;

    public OsmWayImpl(List<Long> ids, Map<String, String> TagsMap){
        this.ids = ids;
        this.TagsMap = TagsMap;
    }

    @Override
    public List<Long> getNodeIdList() {
        return ids; //Todo Check if we can make read only
    }

    @Override
    public Map<String, String> getTags() {
        return TagsMap;
    }
}
