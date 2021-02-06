package openmap.standard;

import openmap.framework.OsmWay;

import java.util.List;
import java.util.Map;

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
