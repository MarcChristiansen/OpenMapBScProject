package openmap.parsing;

import crosby.binary.osmosis.OsmosisReader;
import openmap.framework.Bounds;
import openmap.framework.Node;
import openmap.framework.OsmWay;
import openmap.framework.OsmParser;
import openmap.standard.BoundsImpl;
import openmap.standard.NodeImpl;
import openmap.standard.OsmWayImpl;
import org.openstreetmap.osmosis.core.container.v0_6.BoundContainer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Bound;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OsmiumPbfParserImpl implements OsmParser{
    List<OsmWay> osmWays;
    Bounds bounds;
    Map<Long, Node> nodeMap;
    String fileIn;
    List<String> highWayFilter;

    public OsmiumPbfParserImpl(String fileIn, List<String> highWayFilter){
        this.fileIn = fileIn;
        this.highWayFilter = highWayFilter;
    }

    @Override
    public List<OsmWay> parseWays() {
        if(osmWays == null){
            OsmiumPathAndBoundsParser sink = new OsmiumPathAndBoundsParser(highWayFilter);
            runReaderWithSink(sink);

            this.bounds = sink.getBounds();
            this.osmWays = sink.getOsmWays();
        }
        return this.osmWays;
    }

    @Override
    public Map<Long, Node> parseNodes(Map<Long, Integer> nodeWayCounter) {
        if(nodeMap == null){
            OsmiumNodeParser sink = new OsmiumNodeParser(nodeWayCounter);
            runReaderWithSink(sink);
            this.nodeMap = sink.getNodeMap();
        }
        return this.nodeMap;
    }

    @Override
    public Bounds parseBounds() {
        if(this.bounds == null){
            parseWays(); //Might be a bit weird, but allows us to skip a third iteration.
        }
        return this.bounds;
    }

    private void runReaderWithSink(Sink sink) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileIn);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OsmosisReader reader = new OsmosisReader(inputStream);
        reader.setSink(sink);
        reader.run();

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class OsmiumPathAndBoundsParser implements Sink {
        List<OsmWay> osmWays;
        Bounds bounds;
        List<String> highWayFilter;

        public OsmiumPathAndBoundsParser(List<String> highWayFilter){
            this.highWayFilter = highWayFilter;
            this.osmWays = new ArrayList<>();
        }

        @Override
        public void process(EntityContainer entityContainer) {
            if (entityContainer instanceof WayContainer) {
                Way myWay = ((WayContainer) entityContainer).getEntity();
                for (Tag testTag : myWay.getTags()) {
                    if ("highway".equalsIgnoreCase(testTag.getKey()) && highWayFilter.stream().anyMatch(testTag.getValue()::equalsIgnoreCase)) {

                        //To ensure compatibility we convert our tags to a map.
                        Map<String, String> tagMap = new HashMap<>();
                        for(Tag tag : myWay.getTags()) {
                            tagMap.put(tag.getKey(), tag.getValue());
                        }

                        //We convert the given nodelist to a list of ids that we can actually use.
                        osmWays.add(new OsmWayImpl(myWay.getWayNodes().stream().map(WayNode::getNodeId).collect(Collectors.toList()), tagMap));
                        break;
                    }
                }
            }
            else if(entityContainer instanceof BoundContainer){
                Bound bound = ((BoundContainer) entityContainer).getEntity();
                bounds = new BoundsImpl(bound.getBottom(), bound.getLeft(), bound.getTop(), bound.getRight());
            }
        }

        @Override
        public void initialize(Map<String, Object> metaData) {

        }

        @Override
        public void complete() {
        }

        @Override
        public void close() {

        }

        public List<OsmWay> getOsmWays() {
            return osmWays;
        }

        public Bounds getBounds() {
            return bounds;
        }
    }

    private class OsmiumNodeParser implements Sink {

        Map<Long, Integer> nodeWayCounter;

        Map<Long, Node> nodeMap;

        public OsmiumNodeParser(Map<Long, Integer> nodeWayCounter) {
            this.nodeWayCounter = nodeWayCounter;
            this.nodeMap = new HashMap<>();
        }

        @Override
        public void process(EntityContainer entityContainer) {
            if (entityContainer instanceof NodeContainer) {
                org.openstreetmap.osmosis.core.domain.v0_6.Node node = ((NodeContainer) entityContainer).getEntity();
                long id = node.getId();
                if(nodeWayCounter.getOrDefault(id, 0) > 0){
                    nodeMap.put(id, new NodeImpl(id, node.getLatitude(), node.getLongitude()));
                }
            }
        }

        @Override
        public void initialize(Map<String, Object> metaData) {

        }

        @Override
        public void complete() { }

        @Override
        public void close() {
        }

        public Map<Long, Node> getNodeMap() {
            return nodeMap;
        }
    }
}

