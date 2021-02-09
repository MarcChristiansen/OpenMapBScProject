package openmap.framework;

import java.util.List;
import java.util.Map;

/**
 * Interface of objects used to build graphs
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 09-02-2021
 */
public interface graphBuilder {

    /**
     * Create a graph and return it
     * @return a graph
     */
    public Graph createGraph();

}
