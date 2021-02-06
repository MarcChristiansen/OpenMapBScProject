package openmap.framework;

import java.util.List;
import java.util.Map;

/**
 * Interface for representing ways from Open street map XML data
 * A way consists of a list of nodes and map of tags which have additional properties
 * such as the type of road, maxspeed etc.
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 06-02-2021
 */
public interface OsmWay {
    /**
     * Returns a list of node ids that are part of the road
     * @return a list of node ids that are part of the road
     */
    List<Long> getNodeIdList();

    /**
     * Returns a map of the current tags the road has
     * @return a map of the current tags the road has
     */
    Map<String, String> getTags();
}
