package openmap.parsing;

import crosby.binary.osmosis.OsmosisReader;
import openmap.framework.Bounds;
import openmap.framework.Node;
import openmap.framework.OsmWay;
import openmap.framework.OsmParser;
import openmap.standard.BoundsImpl;
import openmap.standard.OsmWayImpl;
import openmap.special.ParsingNodeImpl;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OsmiumPbfParserImpl implements OsmParser{
    List<OsmWay> osmWays;
    Bounds bounds;
    Map<Long, Node> nodeMap;
    String fileIn;
    List<String> highWayFilter;

    //Flags
    boolean shouldCacheWaysInRam = false;

    public OsmiumPbfParserImpl(String fileIn, List<String> highWayFilter){
        this.fileIn = fileIn;
        this.highWayFilter = highWayFilter;
    }

    public void parseWaysAndBounds() {
        if(osmWays == null){
            OsmiumPathAndBoundsParser sink = new OsmiumPathAndBoundsParser(highWayFilter);
            runReaderWithSink(sink);

            this.bounds = sink.getBounds();

            if(shouldCacheWaysInRam){
                this.osmWays = sink.getOsmWays();
            }
        }
    }

    @Override
    public void runWithAllWays(Consumer<OsmWay> action) {
        if(shouldCacheWaysInRam) {parseWaysAndBounds(); } //If we want to always cache to avoid a triple pass save ways

        if(osmWays == null){
            System.out.println("Running action on paths");
            OsmiumPathAndBoundsParser sink = new OsmiumPathAndBoundsParser(highWayFilter, action);
            runReaderWithSink(sink);

            //We save bounds if we do not already have them, no reason not to do this
            if(bounds == null) {
                this.bounds = sink.getBounds();
            }
        }
        else {
            osmWays.forEach(action);
        }
    }

    @Override
    public Map<Long, Node> parseNodes(Map<Long, Byte> nodeWayCounter) {
        return parseNodes(nodeWayCounter, 0);
    }

    @Override
    public Map<Long, Node> parseNodes(Map<Long, Byte> nodeWayCounter, int minConnections) {
        if(nodeMap == null){
            OsmiumNodeParser sink = new OsmiumNodeParser(nodeWayCounter, minConnections);
            runReaderWithSink(sink);
            this.nodeMap = sink.getNodeMap();
        }
        return this.nodeMap;
    }

    @Override
    public Bounds parseBounds() {
        if(this.bounds == null){
            parseWaysAndBounds(); //Might be a bit weird, but allows us to skip a useless iteration
        }
        return this.bounds;
    }

    @Override
    public void CacheWays(boolean shouldCacheWays) {
        this.shouldCacheWaysInRam = shouldCacheWays;
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
        Consumer<OsmWay> action;

        //Flags
        Boolean actionIteration = false;


        public OsmiumPathAndBoundsParser(List<String> highWayFilter){
            this.highWayFilter = highWayFilter;
            this.osmWays = new ArrayList<>();
        }

        public OsmiumPathAndBoundsParser(List<String> highWayFilter, Consumer<OsmWay> action){
            this.highWayFilter = highWayFilter;
            this.action = action;
            actionIteration = true;
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
                        OsmWay osmWay = new OsmWayImpl(myWay.getWayNodes().stream().map(WayNode::getNodeId).collect(Collectors.toList()), tagMap);

                        if(actionIteration){
                            action.accept(osmWay);
                        }
                        else{
                            osmWays.add(osmWay);
                        }
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

        Map<Long, Byte> nodeWayCounter;

        Map<Long, Node> nodeMap;
        int minConnections;

        public OsmiumNodeParser(Map<Long, Byte> nodeWayCounter, int minConnections) {
            this.nodeWayCounter = nodeWayCounter;
            this.nodeMap = new HashMap<>();
            this.minConnections = minConnections;
        }

        @Override
        public void process(EntityContainer entityContainer) {
            if (entityContainer instanceof NodeContainer) {
                org.openstreetmap.osmosis.core.domain.v0_6.Node node = ((NodeContainer) entityContainer).getEntity();
                long id = node.getId();
                byte wayCount = nodeWayCounter.getOrDefault(id, (byte)(0));
                if(wayCount > minConnections){
                    nodeMap.put(id, new ParsingNodeImpl(id, node.getLatitude(), node.getLongitude(),  wayCount));
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

