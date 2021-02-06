package openmap.framework;

import java.util.List;

public interface OsmWay {
    List<Long> getNodeIdList();
    String getType();
}
