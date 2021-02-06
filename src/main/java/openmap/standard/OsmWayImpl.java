package openmap.standard;

import openmap.framework.OsmWay;

import java.util.List;

public class OsmWayImpl implements OsmWay {

    List<Long> ids;
    String type;

    public OsmWayImpl(List<Long> ids, String type){
        this.ids = ids;
        this.type = type;
    }

    @Override
    public List<Long> getNodeIdList() {
        return ids; //Todo Check if we can make read only
    }

    @Override
    public String getType() {
        return type;
    }
}
